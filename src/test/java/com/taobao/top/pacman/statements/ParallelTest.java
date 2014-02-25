package com.taobao.top.pacman.statements;

import java.util.Map;

import org.junit.Ignore;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.Variable;
import com.taobao.top.pacman.VariableValue;
import com.taobao.top.pacman.WorkflowInstance;

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

	@Ignore
	public void cancel_test() throws java.lang.Exception {
		// TODO parallel should have parent, and call cancel while fault
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
