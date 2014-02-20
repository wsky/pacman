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
	// TODO test scheudleActivityWithResult
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
