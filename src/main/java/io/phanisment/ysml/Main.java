package io.phanisment.ysml;

import java.text.ParseException;

import io.phanisment.ysml.parser.Parser;

public final class Main {
	public static void main(String[] args) throws ParseException {
		var a = """
		key:
		  key:value
		key1:ok
		key2:
		  key:
			 key:value
		key3:value
		""".toCharArray();

		var parser = new Parser(a);
		System.err.println(parser.parse());
	}
}
