package pacman.expressions;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import pacman.Activity;
import pacman.InArgument;
import pacman.OutArgument;
import pacman.expressions.New;
import pacman.statements.Assign;
import pacman.testsuite.StatementTestBase;

public class NewTest extends StatementTestBase {
	@Override
	protected Activity createActivity() {
		New newObj = new New(TestObject.class);
		newObj.getArguments().add(new InArgument("name"));
		newObj.getArguments().add(new InArgument(int.class, 123));
		
		Assign assign = new Assign();
		assign.Value = new InArgument(newObj);
		assign.To = new OutArgument();
		return assign;
	}
	
	@Override
	protected Map<String, Object> createInputs() {
		Map<String, Object> inputs=new HashMap<String, Object>();
		// inputs.put("Argument1", "nam1e");
		// inputs.put("Argument2", 123);
		return inputs;
	}
	
	@Override
	protected void assertOutputs(Map<String, Object> outputs) {
		TestObject obj = (TestObject) outputs.get("To");
		assertNotNull(obj);
		assertEquals(123, obj.id);
		assertEquals("name", obj.name);
	}
	
	@Test
	public void new_test() throws Exception {
		assertEquals(HashMap.class, this.invoke(new New(HashMap.class), null).get("Result").getClass());
		assertEquals(ArrayList.class, this.invoke(new New(ArrayList.class), null).get("Result").getClass());
		assertEquals(HashMap.class, this.invoke(new New(HashMap.class), null).get("Result").getClass());
	}
	
	// must be static
	public static class TestObject {
		public String name;
		public int id;
		
		public TestObject(String name, int id) {
			this.name = name;
			this.id = id;
		}
	}
}
