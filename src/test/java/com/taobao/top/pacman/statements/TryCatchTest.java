package com.taobao.top.pacman.statements;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.ActivityInstance;
import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.FaultCallback;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.NativeActivityFaultContext;
import com.taobao.top.pacman.OutArgument;
import com.taobao.top.pacman.RuntimeArgument;
import com.taobao.top.pacman.WorkflowInstance;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.statements.TryCatch.Catch;
import com.taobao.top.pacman.testsuite.StatementTestBase;

public class TryCatchTest extends StatementTestBase {
	@Override
	protected Activity createActivity() {
		TryCatch tryCatch = new TryCatch();
		tryCatch.Try = new NativeActivity() {
			@Override
			protected void execute(NativeActivityContext context) throws Exception {
				throw new NullPointerException("error in try");
			}
		};
		tryCatch.Try.setDisplayName("Try");
		tryCatch.getCatches().add(new Catch(SecurityException.class, new WriteLine("wrong catch!")));
		tryCatch.getCatches().add(new Catch(NullPointerException.class, new WriteLine("---- catch!")));
		tryCatch.getCatches().add(new Catch(Exception.class, new WriteLine("catch!?")));
		tryCatch.Finally = new WriteLine("---- finally!");
		tryCatch.Finally.setDisplayName("finally");
		return tryCatch;
	}

	@Override
	protected Map<String, Object> createInputs() {
		return null;
	}

	@Override
	protected void assertOutputs(Map<String, Object> outputs) {	
	}
	
	@Test
	public void find_catch_test() {
		TryCatch tryCatch = new TryCatch();
		tryCatch.getCatches().add(new Catch(Exception.class, null));
		tryCatch.getCatches().add(new Catch(NullPointerException.class, null));

		assertEquals(Exception.class, tryCatch.findCatch(new Exception()).getExceptionType());
		assertEquals(NullPointerException.class, tryCatch.findCatch(new NullPointerException()).getExceptionType());
		assertEquals(Exception.class, tryCatch.findCatch(new SecurityException()).getExceptionType());
	}

	@Test
	public void empty_catch_action_test() throws Exception {
		TryCatch tryCatch = new TryCatch();
		tryCatch.Try = new NativeActivity() {
			@Override
			protected void execute(NativeActivityContext context) throws Exception {
				throw new Exception("error in try");
			}
		};
		tryCatch.getCatches().add(new Catch(Exception.class, null));
		WorkflowInstance.invoke(tryCatch, null);
	}

	@Test(expected = NullPointerException.class)
	public void java_finally_test() throws Exception {
		try {
			throw new Exception();
		} catch (Exception e) {
			throw new NullPointerException();
		} finally {
			System.out.println("finally");
		}
	}

	@Test
	public void error_in_catch_and_finally_test() throws Exception {
		TryCatch tryCatch = new TryCatch();
		tryCatch.Try = new NativeActivity() {
			@Override
			protected void execute(NativeActivityContext context) throws Exception {
				throw new Exception("error in try");
			}
		};

		tryCatch.getCatches().add(new Catch(Exception.class, new NativeActivity() {
			@Override
			protected void execute(NativeActivityContext context) throws Exception {
				throw new IndexOutOfBoundsException("error in catch");
			}
		}));

		final AtomicInteger counter = new AtomicInteger();
		tryCatch.Finally = new NativeActivity() {
			@Override
			protected void execute(NativeActivityContext context) throws Exception {
				counter.incrementAndGet();
			}
		};

		try {
			assertEquals(IndexOutOfBoundsException.class,
					WorkflowInstance.invoke(new ExceptionHandled(tryCatch), null).
							get("exception").
							getClass());
		} finally {
			assertEquals(1, counter.get());
		}
	}

	@Test
	public void no_catch_and_finally_test() throws Exception {
		TryCatch tryCatch = new TryCatch();
		tryCatch.Try = new NativeActivity() {
			@Override
			protected void execute(NativeActivityContext context) throws Exception {
				throw new IndexOutOfBoundsException("error in try");
			}
		};

		final AtomicInteger counter = new AtomicInteger();
		tryCatch.Finally = new NativeActivity() {
			@Override
			protected void execute(NativeActivityContext context) throws Exception {
				counter.incrementAndGet();
			}
		};

		try {
			assertEquals(IndexOutOfBoundsException.class,
					WorkflowInstance.invoke(new ExceptionHandled(tryCatch), null).
							get("exception").
							getClass());
		} finally {
			assertEquals(1, counter.get());
		}
	}

	@Test
	public void error_in_finally_test() throws Exception {
		TryCatch tryCatch = new TryCatch();
		tryCatch.Try = new WriteLine("try");
		tryCatch.Finally = new NativeActivity() {
			@Override
			protected void execute(NativeActivityContext context) throws Exception {
				throw new IndexOutOfBoundsException("error in finally");
			}
		};
		assertEquals(IndexOutOfBoundsException.class,
				WorkflowInstance.invoke(new ExceptionHandled(tryCatch), null).
						get("exception").
						getClass());

	}

	@Test
	public void mark_canceled_in_finally_test() throws Exception {
		TryCatch tryCatch = new TryCatch();
		tryCatch.setDisplayName("tryCatch");
		tryCatch.Try = new NativeActivity() {
			@Override
			protected void execute(NativeActivityContext context) throws Exception {
				throw new IndexOutOfBoundsException("error in try");
			}
		};

		final AtomicInteger counter = new AtomicInteger();
		tryCatch.Finally = new NativeActivity() {
			@Override
			protected void execute(NativeActivityContext context) throws Exception {
				counter.incrementAndGet();
			}
		};

		try {
			// parent call cancelChildren() and context.markCanceled() called in onFinallyComplete
			assertEquals(IndexOutOfBoundsException.class,
					WorkflowInstance.invoke(new ExceptionCanceled(tryCatch), null).
							get("exception").
							getClass());
		} finally {
			assertEquals(1, counter.get());
		}
	}

	public class ExceptionHandled extends NativeActivity {
		public OutArgument exception;
		public Activity body;

		public ExceptionHandled(Activity body) {
			this.body = body;
		}

		@Override
		protected void cacheMetadata(ActivityMetadata metadata) {
			metadata.addChild(this.body);
			metadata.bindAndAddArgument(this.exception = new OutArgument(),
					new RuntimeArgument("exception", Exception.class, ArgumentDirection.Out));
		}

		@Override
		protected void execute(NativeActivityContext context) throws Exception {
			context.scheduleActivity(this.body, new FaultCallback() {
				@Override
				public void execute(
						NativeActivityFaultContext faultContext,
						Exception propagatedException,
						ActivityInstance propagatedFrom) {
					exception.set(faultContext, propagatedException);
					faultContext.handleFault();
				}
			});
		}
	}

	public class ExceptionCanceled extends ExceptionHandled {
		public ExceptionCanceled(Activity body) {
			super(body);
		}

		@Override
		protected void execute(NativeActivityContext context) throws Exception {
			context.scheduleActivity(this.body, new FaultCallback() {
				@Override
				public void execute(
						NativeActivityFaultContext faultContext,
						Exception propagatedException,
						ActivityInstance propagatedFrom) {
					exception.set(faultContext, propagatedException);
					faultContext.handleFault();
					faultContext.cancelChildren();
				}
			});
		}
	}
}
