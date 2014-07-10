package com.taobao.top.pacman.statements;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.Variable;
import com.taobao.top.pacman.testsuite.StatementTestBase;

public class MapTest extends StatementTestBase {
	private Map<?, ?> _map;
	
	@Override
	protected Activity createActivity() {
		Sequence sequence = new Sequence();
		
		Variable map = new Variable("collection", _map = new HashMap<Object, Object>());
		sequence.getVariables().add(map);
		
		Variable key1 = new Variable("key1", "key1");
		Variable key2 = new Variable("key2", "key2");
		Variable value = new Variable("value", "value");
		sequence.getVariables().add(key1);
		sequence.getVariables().add(key2);
		sequence.getVariables().add(value);
		
		AddToMap add1 = new AddToMap();
		add1.Map = new InArgument(map);
		add1.Key = new InArgument(key1);
		add1.Value = new InArgument(value);
		sequence.getActivities().add(add1);
		
		AddToMap add2 = new AddToMap();
		add2.Map = new InArgument(map);
		add2.Key = new InArgument(key2);
		add2.Value = new InArgument(value);
		sequence.getActivities().add(add2);
		
		RemoveFromMap remove = new RemoveFromMap();
		remove.Map = new InArgument(map);
		remove.Key = new InArgument(key1);
		sequence.getActivities().add(remove);
		
		ExistsInMap exists = new ExistsInMap();
		exists.Map = new InArgument(map);
		exists.Key = new InArgument(key1);
		sequence.getActivities().add(exists);
		
		return sequence;
	}
	
	@Override
	protected Map<String, Object> createInputs() {
		return null;
	}
	
	@Override
	protected void assertOutputs(Map<String, Object> outputs) {
		System.out.println(_map);
		assertFalse(_map.containsKey("key1"));
		assertEquals("value", _map.get("key2"));
	}
	
}
