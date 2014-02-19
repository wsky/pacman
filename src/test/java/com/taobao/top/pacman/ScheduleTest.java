package com.taobao.top.pacman;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

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

	// TODO test activityWithResult not fast-path
	// TODO test schedule with callback
	// TODO test abort and error
	// TODO test cancel

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

			WriteLine writeLine1 = new WriteLine();
			writeLine1.Text = new InArgument(this.inner);
			this.nest = writeLine1;

			WriteLine writeLine2 = new WriteLine();
			writeLine2.Text = new InArgument(this.var);
			// this.body = writeLine2;

			Sequence sequence = new Sequence();
			sequence.getActivities().add(writeLine2);

			this.body = sequence;
		}

		@Override
		protected void execute(NativeActivityContext context) {
			this.inner.set(context, "inner:" + this.name.get(context));

			this.result1.set(context, "1");
			this.result2.set(context, "2");

			context.scheduleActivity(this.nest);
			context.scheduleActivity(this.body);

			// FIXME test callback
			// context.scheduleActivity(this.body, new CompletionCallback() {
			// @Override
			// public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
			// result1.set(context, "1");
			// result2.set(context, "2");
			// }
			// });
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
