package com.taobao.top.pacman.statements;

import java.util.Map;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.statements.TryCatch.Catch;

public class TryCatchTest extends StatementTestBase {
	@Override
	protected Activity createActivity() {
		TryCatch tryCatch = new TryCatch();
		tryCatch.Try = new NativeActivity() {
			@Override
			protected void execute(NativeActivityContext context) throws Exception {
				throw new NullPointerException("error in try");
			}
		};
		tryCatch.Try.setDisplayName("Try");
		tryCatch.getCatches().add(new Catch(SecurityException.class, new WriteLine("wrong catch!")));
		tryCatch.getCatches().add(new Catch(NullPointerException.class, new WriteLine("---- catch!")));
		tryCatch.getCatches().add(new Catch(Exception.class, new WriteLine("catch!?")));
		tryCatch.Finally = new WriteLine("---- finally!");
		tryCatch.Finally.setDisplayName("finally");
		return tryCatch;
	}

	@Override
	protected Map<String, Object> createInputs() {
		return null;
	}

}
