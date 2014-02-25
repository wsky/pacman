package com.taobao.top.pacman;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.taobao.top.pacman.ActivityInstance.ActivityInstanceState;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.statements.Sequence;
import com.taobao.top.pacman.statements.WriteLine;

public class ScheduleTest {
	@Test
	public void single_test() throws Exception {
		WorkflowInstance.invoke(new Activity() {
		}, null);
	}

	@Test
	public void workflow_test() throws Exception {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("name", "--------------- print test_name");
		Activity workflow = new Workflow();
		Map<String, Object> outputs = WorkflowInstance.invoke(workflow, inputs);
		System.out.println(inputs);
		System.out.println(outputs);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void error_and_abort_test() throws Exception {
		final AtomicInteger internalAbort = new AtomicInteger();
		WorkflowInstance.invoke(new NativeActivity() {
			private Activity activity1;
			private Activity activity2;

			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				activity1 = new WriteLine("write1") {
					@Override
					protected void abort(NativeActivityAbortContext context) {
						System.err.println("abort");
						internalAbort.incrementAndGet();
					}
				};
				activity2 = new WriteLine("write2");
				metadata.addChild(activity1);
				metadata.addChild(activity2);
			}

			@Override
			protected void execute(NativeActivityContext context) {
				context.scheduleActivity(activity1);
				context.scheduleActivity(activity2);
				throw new IndexOutOfBoundsException();
			}
		}, null);
		assertEquals(1, internalAbort.get());
	}

	@Test
	public void cancel_test() throws Exception {
		WorkflowInstance.invoke(new NativeActivity() {
			private Activity activity1;
			private Activity activity2;

			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				activity1 = new WriteLine("write1") {
				};
				activity1.setDisplayName("activity1");
				activity2 = new WriteLine("write2");
				activity2.setDisplayName("activity2");
				metadata.addChild(activity1);
				metadata.addChild(activity2);
			}

			@Override
			protected void execute(NativeActivityContext context) {
				context.scheduleActivity(activity2);
				context.scheduleActivity(activity1, new CompletionCallback() {
					@Override
					public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
						context.cancelChildren();
						System.out.println("======== cancel children");
					}
				});
			}
		}, null);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void fault_test() throws Exception {
		fault_callback_test(false);
	}

	@Test
	public void fault_handled_test() throws Exception {
		fault_callback_test(true);
	}

	private void fault_callback_test(final boolean handle) throws Exception {
		WorkflowInstance.invoke(new NativeActivity() {
			private Activity body;

			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				this.body = new NativeActivity() {
					@Override
					protected void execute(NativeActivityContext context) {
						throw new IndexOutOfBoundsException("error");
					}
				};
				metadata.addChild(this.body);
			}

			@Override
			protected void execute(NativeActivityContext context) {
				context.scheduleActivity(this.body,
						new CompletionCallback() {
							@Override
							public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
								System.out.println("========== completion callback");
								assertEquals(ActivityInstanceState.Faulted, completedInstance.getState());
							}
						},
						new FaultCallback() {
							@Override
							public void execute(
									NativeActivityFaultContext faultContext,
									Exception propagatedException,
									ActivityInstance propagatedFrom) {
								System.out.println("========== fault callback");
								propagatedException.printStackTrace();
								if (handle)
									faultContext.handleFault();
							}
						});
			}
		}, null);
	}

	// TODO test activityWithResult not fast-path, async

	class Workflow extends NativeActivity {
		private InArgument name;
		private OutArgument result1;
		private OutArgument result2;
		private Variable var;
		private Variable inner;
		private Activity body;
		private Activity nest;

		public Workflow() {
			this.name = new InArgument();

			this.result1 = new OutArgument();
			this.result2 = new OutArgument();

			this.var = new Variable("var", new Function<ActivityContext, Object>() {
				@Override
				public Object execute(ActivityContext context) {
					return "var:" + name.get(context);
				}
			});
			this.inner = new Variable("inner");

			// nest
			WriteLine writeLine1 = new WriteLine();
			writeLine1.Text = new InArgument(this.inner);
			WriteLine writeLine2 = new WriteLine();
			writeLine2.Text = new InArgument(new ArgumentValue(this.name));
			Sequence sequence = new Sequence();
			sequence.getActivities().add(writeLine1);
			sequence.getActivities().add(writeLine2);
			this.nest = sequence;

			// TODO functionValue should be compiled to inlined function and chained access
			// writeLine2.Text = new InArgument(new Function<ActivityContext, Object>() {
			// @Override
			// public Object execute(ActivityContext context) {
			// return name.get(context);
			// }
			// });

			// body
			WriteLine writeLine3 = new WriteLine();
			writeLine3.Text = new InArgument(this.var);
			WriteLine writeLine4 = new WriteLine();
			writeLine4.Text = new InArgument("constValue");
			sequence = new Sequence();
			sequence.getActivities().add(writeLine3);
			sequence.getActivities().add(writeLine4);

			this.body = sequence;
		}

		@Override
		protected void execute(NativeActivityContext context) {
			context.scheduleActivity(this.nest);
			context.scheduleActivity(this.body, new CompletionCallback() {
				@Override
				public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
					inner.set(context, "inner:" + name.get(context));
					result1.set(context, "1");
					result2.set(context, "2");
				}
			});
		}

		@Override
		protected void cacheMetadata(ActivityMetadata metadata) {
			metadata.bindAndAddArgument(this.name, new RuntimeArgument("name", String.class, ArgumentDirection.In));
			metadata.bindAndAddArgument(this.result1, new RuntimeArgument("result1", String.class, ArgumentDirection.Out));
			metadata.bindAndAddArgument(this.result2, new RuntimeArgument("result2", String.class, ArgumentDirection.Out));
			metadata.addImplementationVariable(this.inner);
			metadata.addImplementationChild(this.nest);
			metadata.addRuntimeVariable(this.var);
			metadata.addChild(this.body);
		}
	}
}
