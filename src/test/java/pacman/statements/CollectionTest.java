package pacman.statements;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pacman.Activity;
import pacman.InArgument;
import pacman.Variable;
import pacman.statements.AddToCollection;
import pacman.statements.ExistsInCollection;
import pacman.statements.RemoveFromCollection;
import pacman.statements.Sequence;
import pacman.testsuite.StatementTestBase;

public class CollectionTest extends StatementTestBase {
	private List<Object> list;
	
	@Override
	protected Activity createActivity() {
		Sequence sequence = new Sequence();
		
		Variable collection = new Variable("collection", list = new ArrayList<Object>());// new New(ArrayList.class)
		sequence.getVariables().add(collection);
		
		Variable item = new Variable("item", new Object());
		sequence.getVariables().add(item);
		
		AddToCollection add1 = new AddToCollection();
		add1.Collection = new InArgument(collection);
		add1.Item = new InArgument(item);
		sequence.getActivities().add(add1);
		
		AddToCollection add2 = new AddToCollection();
		add2.Collection = new InArgument(collection);
		add2.Item = new InArgument(item);
		sequence.getActivities().add(add2);
		
		RemoveFromCollection remove = new RemoveFromCollection();
		remove.Collection = new InArgument(collection);
		remove.Item = new InArgument(item);
		sequence.getActivities().add(remove);
		
		ExistsInCollection exists = new ExistsInCollection();
		exists.Collection = new InArgument(collection);
		exists.Item = new InArgument(item);
		sequence.getActivities().add(exists);
		
		return sequence;
	}
	
	@Override
	protected Map<String, Object> createInputs() {
		return null;
	}
	
	@Override
	protected void assertOutputs(Map<String, Object> outputs) {
		assertEquals(1, list.size());
	}
	
}
