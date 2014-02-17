package com.taobao.top.pacman;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.hosting.WorkflowInstance;
import com.taobao.top.pacman.statements.WriteLine;

public class ScheduleTest {
	@Test
	public void schedule() {
		Map<String, Object> inputs = new HashMap<String, Object>();
		Activity workflow = new Workflow();
		WorkflowInstance.invoke(workflow, inputs);
	}

	class Workflow extends NativeActivity {
		private InArgument name;
		private OutArgument result;
		private Variable var;
		private Activity body;

		public Workflow() {
			this.name = new InArgument();
			this.result = new OutArgument();
			this.var = new Variable();
			WriteLine writeLine = new WriteLine();
			//writeLine.Text = new InArgument(this.var);
			writeLine.Text = new InArgument();
			this.body = writeLine;
		}

		@Override
		protected void execute(NativeActivityContext context) {
			this.var.set(context, this.name.get(context));
			context.scheduleActivity(this.body);
		}

		@Override
		protected void cacheMetadata(ActivityMetadata metadata) {
			metadata.bindAndAddArgument(this.name, new RuntimeArgument("name", String.class, ArgumentDirection.In));
			metadata.bindAndAddArgument(this.result, new RuntimeArgument("result", String.class, ArgumentDirection.Out));
			metadata.addRuntimeVariable(this.var);
			metadata.addChild(this.body);
		}
	}
}
