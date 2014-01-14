package com.taobao.top.pacman;

import com.taobao.top.pacman.statements.If;
import com.taobao.top.pacman.statements.WriteLine;

public class FindMyPhone extends NativeActivity {
	public InArgument Name;
	public OutArgument Phone;

	private Activity how;

	public FindMyPhone() {
		If _if = new If();
		_if.Condition = new InArgument();
		_if.Then = new WriteLine();
		_if.Else = new WriteLine();
	}

	@Override
	protected void execute(NativeActivityContext context) {
		context.scheduleActivity(this.how);
	}
}
