package com.taobao.top.pacman.statements;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.ActivityContext;
import com.taobao.top.pacman.Function;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.expressions.FunctionValue;

public class WhileTest extends StatementTestBase {
	@Override
	protected Activity createActivity() {
		final AtomicInteger counter = new AtomicInteger();
		final AtomicBoolean flag = new AtomicBoolean(true);
		While while1 = new While();
		while1.Condition = new FunctionValue(new Function<ActivityContext, Object>() {
			@Override
			public Object execute(ActivityContext arg) {
				return flag.getAndSet(false);
			}
		});
		while1.Body = new NativeActivity() {
			@Override
			protected void execute(NativeActivityContext context) {
				assertEquals(1, counter.incrementAndGet());
				System.err.println("while body execute once");
			}
		};

		return while1;
	}

	@Override
	protected Map<String, Object> createInputs() {
		return null;
	}
}
