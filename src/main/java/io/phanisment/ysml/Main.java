package io.phanisment.ysml;

import io.phanisment.ysml.parser.TokenParser;
import io.phanisment.ysml.tokenize.TokenStream;

// Just example
public final class Main {
	public static void main(String[] args) {
		char[] t = """
		# this is comment

		key:value
		key0:
		  key:value
		  key0:value
		  key1:
		    key:value
		key1:value
		""".toCharArray();
		var stream = new TokenStream(t);
		var parse = new TokenParser(stream);
		System.err.println(parse.parse());
	}
}
