package cn.ljpc.client.string;

import static org.junit.Assert.assertEquals;

public class StringTest {
	public static void main(String[] args) {
		String str = "[]";
		String substring = str.substring(str.indexOf("[")+1, str.indexOf("]"));
		assertEquals(substring, "");
	}
}
