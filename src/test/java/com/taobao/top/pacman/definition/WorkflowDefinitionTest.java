package com.taobao.top.pacman.definition;

import org.junit.Test;

public class WorkflowDefinitionTest {
	@Test
	public void create_test() {
		WorkflowDefinition.create().
			in("arg").
			out("result").
			sequence().
				var("var").
				If().
					condition().
					then().endThen().
					Else().endElse().
				endIf().
				writeLine().
					text("").
				end().
			end();
	}
}
