package com.taobao.top.pacman;

public interface Function<TArg, TReturn> {
	public TReturn execute(TArg arg);
}
