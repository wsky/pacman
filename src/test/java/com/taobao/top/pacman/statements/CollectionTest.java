package com.taobao.top.pacman.statements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.OutArgument;
import com.taobao.top.pacman.RuntimeArgument;
import com.taobao.top.pacman.Variable;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.expressions.New;
import com.taobao.top.pacman.testsuite.StatementTestBase;

public class CollectionTest extends StatementTestBase {
	@Override
	protected Activity createActivity() {
		final OutArgument result = new OutArgument();
		
		Sequence sequence = new Sequence() {
			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				super.cacheMetadata(metadata);
				metadata.bindAndAddArgument(result, new RuntimeArgument("Result", Collection.class, ArgumentDirection.Out));
			}
		};
		
		Variable collection = new Variable("collection", new New(ArrayList.class));
		sequence.getVariables().add(collection);
		
		Variable item = new Variable("item", new Object());
		sequence.getVariables().add(item);
		
		AddToCollection add = new AddToCollection();
		add.Collection = new InArgument(collection);
		add.Item = new InArgument(item);
		sequence.getActivities().add(add);
		
		RemoveFromCollection remove = new RemoveFromCollection();
		remove.Collection = new InArgument(collection);
		remove.Item = new InArgument(item);
		sequence.getActivities().add(remove);
		
		ExistsInCollection exists = new ExistsInCollection();
		exists.Collection = new InArgument(collection);
		exists.Item = new InArgument(item);
		sequence.getActivities().add(exists);
		
		// Assign assign=new Assign();
		// assign.Value=new InArgument(collection);
		// assign.To=new OutArgument(variable);
		// sequence.getActivities().add(assign);
		
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
