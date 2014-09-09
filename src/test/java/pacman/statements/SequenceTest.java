package pacman.statements;

import java.util.Map;

import pacman.Activity;
import pacman.Variable;
import pacman.statements.Sequence;
import pacman.statements.WriteLine;
import pacman.testsuite.StatementTestBase;

public class SequenceTest extends StatementTestBase {
	protected Activity createActivity() {
		Sequence sequence = new Sequence();
		sequence.getVariables().add(new Variable("var", 1));
		sequence.getActivities().add(new WriteLine());
		sequence.getActivities().add(new WriteLine());
		return sequence;
	}
	
	@Override
	protected Map<String, Object> createInputs() {
		return null;
	}
	
	@Override
	protected void assertOutputs(Map<String, Object> outputs) {
	}
}
