package pacman.statements;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import pacman.Activity;
import pacman.InArgument;
import pacman.OutArgument;
import pacman.statements.Assign;
import pacman.testsuite.StatementTestBase;

public class AssignTest extends StatementTestBase {
	@Override
	protected Activity createActivity() {
		Assign assign = new Assign();
		assign.Value = new InArgument();
		assign.To = new OutArgument();
		return assign;
	}
	
	@Override
	protected Map<String, Object> createInputs() {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("Value", "assign_test");
		return inputs;
	}
	
	@Override
	protected void assertOutputs(Map<String, Object> outputs) {
		assertEquals("assign_test", outputs.get("To"));
	}
}
