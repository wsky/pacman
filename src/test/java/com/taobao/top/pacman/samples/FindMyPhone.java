package com.taobao.top.pacman.samples;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.OutArgument;
import com.taobao.top.pacman.Variable;
import com.taobao.top.pacman.statements.If;
import com.taobao.top.pacman.statements.While;
import com.taobao.top.pacman.statements.WriteLine;

public class FindMyPhone extends NativeActivity {
	public InArgument Name;
	public OutArgument Phone;

	private Activity how;

	public FindMyPhone() {
		If _if = new If();
		_if.Condition = new InArgument(true);
		_if.Then = new WriteLine();

		While _while = new While();
		_while.Condition = new InArgument(new Variable("isOnline", true));
		_while.Body = new WriteLine();
		_if.Else = _while;
	}

	@Override
	protected void execute(NativeActivityContext context) {
		context.scheduleActivity(this.how);
	}
}
