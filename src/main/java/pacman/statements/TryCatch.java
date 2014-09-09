package pacman.statements;

import java.util.ArrayList;
import java.util.List;

import pacman.Activity;
import pacman.ActivityInstance;
import pacman.ActivityMetadata;
import pacman.CompletionCallback;
import pacman.FaultCallback;
import pacman.FaultContext;
import pacman.NativeActivity;
import pacman.NativeActivityContext;
import pacman.NativeActivityFaultContext;
import pacman.Variable;
import pacman.ActivityInstance.ActivityInstanceState;

public class TryCatch extends NativeActivity {
	private Variable state;
	private List<Variable> variables;
	private List<Catch> catches;
	private FaultCallback exceptionHandler;

	public Activity Try;
	public Activity Finally;

	public TryCatch() {
		this.state = new Variable();
	}

	public List<Variable> getVariables() {
		if (this.variables == null)
			this.variables = new ArrayList<Variable>();
		return this.variables;
	}

	public List<Catch> getCatches() {
		if (this.catches == null)
			this.catches = new ArrayList<Catch>();
		return this.catches;
	}

	@Override
	protected void cacheMetadata(ActivityMetadata metadata) {
		if (this.Try != null)
			metadata.addChild(this.Try);
		if (this.Finally != null)
			metadata.addChild(this.Finally);
		if (this.catches != null)
			for (Catch c : this.catches)
				if (c.getAction() != null)
					metadata.addChild(c.getAction());

		metadata.addImplementationVariable(this.state);
		if (this.variables != null)
			metadata.setRuntimeVariables(this.variables);
	}

	@Override
	protected void execute(NativeActivityContext context) {
		this.state.set(context, new State());

		if (this.Try != null)
			context.scheduleActivity(this.Try,
					new CompletionCallback() {
						@Override
						public void execute(
								NativeActivityContext context,
								ActivityInstance completedInstance) {
							onTryComplete(context, completedInstance);
						}
					},
					new FaultCallback() {
						@Override
						public void execute(
								NativeActivityFaultContext faultContext,
								Exception propagatedException,
								ActivityInstance propagatedFrom) {
							onExceptionFromTry(faultContext, propagatedException, propagatedFrom);
						}
					});
		else
			this.onTryComplete(context, null);
	}

	@Override
	protected void cancel(NativeActivityContext context) {
		State state = (State) this.state.get(context);
		if (!state.SuppressCancel)
			context.cancelChildren();
	}

	private void onTryComplete(NativeActivityContext context, ActivityInstance completedInstance) {
		State state = (State) this.state.get(context);
		// only allow the Try to be canceled. catch/finally must be executed
		state.SuppressCancel = true;

		if (state.CaughtException != null) {
			Catch toSchedule = this.findCatch(state.CaughtException.getException());

			if (toSchedule != null) {
				state.ExceptionHandled = true;
				if (toSchedule.getAction() != null) {
					context.scheduleActivity(
							toSchedule.getAction(),
							new CompletionCallback() {
								@Override
								public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
									onCatchComplete(context, completedInstance);
								}
							},
							this.getExceptionFromCatchOrFinallyHandler());
					return;
				}
			}
		}

		onCatchComplete(context, null);
	}

	private void onExceptionFromTry(
			NativeActivityFaultContext faultContext,
			Exception propagatedException,
			ActivityInstance propagatedFrom) {
		if (propagatedFrom.isCancellationRequested()) {
			// The Try activity threw an exception during Cancel; abort the workflow
			faultContext.abort(propagatedException);
			faultContext.handleFault();
			return;
		}

		Catch catchHandler = this.findCatch(propagatedException);
		if (catchHandler == null)
			return;

		faultContext.cancelChild(propagatedFrom);
		State state = (State) this.state.get(faultContext);
		state.CaughtException = faultContext.createFaultContext();
		faultContext.handleFault();
	}

	private void onCatchComplete(NativeActivityContext context, ActivityInstance completedInstance) {
		// Start suppressing cancel for the finally activity
		State state = (State) this.state.get(context);
		state.SuppressCancel = true;

		if (completedInstance != null && completedInstance.getState() != ActivityInstanceState.Closed)
			state.ExceptionHandled = false;

		if (this.Finally != null)
			context.scheduleActivity(this.Finally,
					new CompletionCallback() {
						@Override
						public void execute(
								NativeActivityContext context,
								ActivityInstance completedInstance) {
							onFinallyComplete(context, completedInstance);
						}
					},
					this.getExceptionFromCatchOrFinallyHandler());
		else
			onFinallyComplete(context, null);
	}

	private void onFinallyComplete(NativeActivityContext context, ActivityInstance completedInstance) {
		State state = (State) this.state.get(context);
		if (context.isCancellationRequested() && !state.ExceptionHandled)
			// maybe error2 in finally and parent handle it,
			// then if error1 was not handled and parent start cancallation,
			// TryCatch should cancel itself here
			context.markCanceled();
	}

	private FaultCallback getExceptionFromCatchOrFinallyHandler() {
		if (this.exceptionHandler == null) {
			this.exceptionHandler = new FaultCallback() {
				@Override
				public void execute(
						NativeActivityFaultContext faultContext,
						Exception propagatedException,
						ActivityInstance propagatedFrom) {
					onExceptionFromCatchOrFinally(faultContext, propagatedException, propagatedFrom);
				}
			};
		}
		return this.exceptionHandler;
	}

	private void onExceptionFromCatchOrFinally(
			NativeActivityFaultContext faultContext,
			Exception propagatedException,
			ActivityInstance propagatedFrom) {
		// NOTE if error in catch, finally only executed after parent handle the error
		// as error not handled, it was treated as runtime crash.
		State state = (State) this.state.get(faultContext);
		state.SuppressCancel = false;
	}

	protected final Catch findCatch(Exception exception) {
		Class<?> exceptionType = exception.getClass();
		Catch potentialCatch = null;

		if (this.catches == null)
			return potentialCatch;

		for (Catch catchHandler : this.catches) {
			if (catchHandler.exceptionType.equals(exceptionType))
				return catchHandler;

			if (catchHandler.exceptionType.isAssignableFrom(exceptionType)) {
				if (potentialCatch != null) {
					// FIXME how java determine subClass?
					// if (catchHandler.exceptionType.isSubClass(potentialCatch.exceptionType))
					// potentialCatch = catchHandler;
				} else
					potentialCatch = catchHandler;
			}
		}

		return potentialCatch;
	}

	public static class State {
		public boolean ExceptionHandled;
		public FaultContext CaughtException;
		public boolean SuppressCancel;

	}

	public static class Catch {
		private Class<?> exceptionType;
		// FIXME impl ActivityAction
		private Activity action;

		public Catch(Class<? extends Exception> exceptionType, Activity action) {
			this.exceptionType = exceptionType;
			this.action = action;
		}

		public Class<?> getExceptionType() {
			return this.exceptionType;
		}

		public Activity getAction() {
			return this.action;
		}
	}

	// public static class CatchAction extends NativeActivity {
	// private InArgument exception;
	// private
	// public Exception getException(ActivityContext context) {
	// return (Exception) this.exception.get(context);
	// }
	//
	// @Override
	// protected void execute(NativeActivityContext context) throws Exception {
	// }
	//
	//
	// }
}
