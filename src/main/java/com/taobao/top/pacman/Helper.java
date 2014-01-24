package com.taobao.top.pacman;

public class Helper {
	public static void assertTrue(boolean condition) {

	}

	public static void assertTrue(boolean condition, String message) {
		if (!condition)
			throw new SecurityException(message);
	}

	public static void assertFalse(boolean condition) {

	}

	public static void assertFalse(boolean condition, String message) {
		if (condition)
			throw new SecurityException(message);
	}

	public static void assertEquals(Object expected, Object actual) {

	}

	public static void assertEquals(Object expected, Object actual, String message) {
		if (actual != expected)
			throw new SecurityException(message);
	}

	public static void assertNotEquals(Object expected, Object actual) {

	}

	public static void assertNull(Object actual) {

	}

	public static void assertNotNull(Object actual) {

	}

}
