package pacman.expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import pacman.Activity;
import pacman.ActivityInstance;
import pacman.ActivityMetadata;
import pacman.CompletionCallback;
import pacman.NativeActivity;
import pacman.NativeActivityContext;
import pacman.OutArgument;
import pacman.RuntimeArgument;
import pacman.Variable;
import pacman.WorkflowInstance;
import pacman.RuntimeArgument.ArgumentDirection;

public class OutArgumentTest {
	@Test
	public void variable_reference_test() throws Exception {
		final Variable variable = new Variable();
		final OutArgument out = new OutArgument(variable);

		final Activity body = new NativeActivity() {
			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				metadata.bindAndAddArgument(out, new RuntimeArgument("out", String.class, ArgumentDirection.Out));
			}

			@Override
			protected void execute(NativeActivityContext context) {
				out.set(context, "hi");
			}
		};

		WorkflowInstance.invoke(new NativeActivity() {
			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				metadata.addRuntimeVariable(variable);
				metadata.addChild(body);
			}

			@Override
			protected void execute(NativeActivityContext context) {
				context.scheduleActivity(body, new CompletionCallback() {
					@Override
					public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
						System.out.println("======== callback");
						assertEquals("hi", variable.get(context));
					}
				});
			}
		}, null);
	}
}
