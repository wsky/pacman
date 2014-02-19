package com.taobao.top.pacman;

public class Helper {
	public static void assertTrue(boolean condition) {
		assertTrue(condition, null);
	}

	public static void assertTrue(boolean condition, String message) {
		if (!condition)
			throw new SecurityException(message);
	}

	public static void assertFalse(boolean condition) {
		if (condition)
			throw new SecurityException("should be false");
	}

	public static void assertFalse(boolean condition, String message) {
		if (condition)
			throw new SecurityException(message);
	}

	public static void assertEquals(Object expected, Object actual) {
		assertEquals(expected, actual, null);
	}

	public static void assertEquals(Object expected, Object actual, String message) {
		if (actual != expected)
			throw new SecurityException(message);
	}

	public static void assertNotEquals(Object expected, Object actual) {
		if (expected.equals(actual))
			throw new SecurityException("should not equal");
	}

	public static void assertNull(Object actual) {
		if (actual != null)
			throw new SecurityException("not null");
	}

	public static void assertNotNull(Object actual) {
		if (actual == null)
			throw new NullPointerException();
	}

	public static void assertFail() {
		throw new SecurityException("fail");
	}

	public static boolean isFatal(Exception e) {
		// FIXME default false and fix execetion propagate
		return true;
	}
}
