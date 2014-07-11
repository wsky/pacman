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
		
		// add key1
		AddToMap add1 = new AddToMap();
		add1.Map = new InArgument(map);
		add1.Key = new InArgument(key1);
		add1.Value = new InArgument(value);
		sequence.getActivities().add(add1);
		
		// add key2
		AddToMap add2 = new AddToMap();
		add2.Map = new InArgument(map);
		add2.Key = new InArgument(key2);
		add2.Value = new InArgument(value);
		sequence.getActivities().add(add2);
		
		// remove key1
		RemoveFromMap remove = new RemoveFromMap();
		remove.Map = new InArgument(map);
		remove.Key = new InArgument(key1);
		sequence.getActivities().add(remove);
		
		// exists key1
		ExistsInMap exists = new ExistsInMap();
		exists.Map = new InArgument(map);
		exists.Key = new InArgument(key1);
		sequence.getActivities().add(exists);
		
		GetFromMap get = new GetFromMap();
		get.setDisplayName("GetFromMap");
		get.Map = new InArgument(map);
		get.Key = new InArgument(key2);
		// add key3, assign value of key2
		AddToMap add3 = new AddToMap();
		add3.Map = new InArgument(map);
		add3.Key = new InArgument("key3");
		add3.Value = new InArgument(get);
		sequence.getActivities().add(add3);
		
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
		assertEquals("value", _map.get("key3"));
	}
	
}
