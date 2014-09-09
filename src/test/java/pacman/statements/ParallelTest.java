package pacman.statements;

import java.util.Map;

import org.junit.Ignore;

import pacman.Activity;
import pacman.ActivityMetadata;
import pacman.NativeActivityContext;
import pacman.Variable;
import pacman.VariableValue;
import pacman.WorkflowInstance;
import pacman.statements.Parallel;
import pacman.statements.WriteLine;
import pacman.testsuite.StatementTestBase;

public class ParallelTest extends StatementTestBase {
	@Override
	protected Activity createActivity() {
		Parallel parallel = new Parallel();
		parallel.setDisplayName("parallel_test");

		Variable completed = new Variable("completed", true);
		parallel.getVariables().add(completed);

		parallel.CompletionCondition = new VariableValue(completed);

		parallel.getBranches().add(new WriteLine("parallel-1"));
		parallel.getBranches().add(new WriteLine("parallel-2"));

		return parallel;
	}

	@Override
	protected Map<String, Object> createInputs() {
		return null;
	}
	

	@Override
	protected void assertOutputs(Map<String, Object> outputs) {
	}

	@Ignore
	public void cancel_test() throws java.lang.Exception {
		// FIXME parallel should have parent, and call cancel while fault
		Parallel parallel = new Parallel();

		Variable completed = new Variable("completed", false);
		parallel.getVariables().add(completed);
		parallel.CompletionCondition = new VariableValue(completed);

		parallel.getBranches().add(new WriteLine("parallel-cancel-1"));
		parallel.getBranches().add(new WriteLine("parallel-cancel-2") {
			@Override
			protected void execute(NativeActivityContext context) {
				throw new NullPointerException("make faulted");
			}

			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				this.setDisplayName("writeLine2");
				super.cacheMetadata(metadata);
			}
		});
		parallel.getBranches().add(new WriteLine("parallel-cancel-3"));

		WorkflowInstance.invoke(parallel, null);
	}
}
