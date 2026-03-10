package io.phanisment.ysml.parser;

import java.util.LinkedHashMap;
import java.util.Map;

import io.phanisment.ysml.tokenize.TokenStream;
import io.phanisment.ysml.tokenize.TokenType;

public class TokenParser {

	private final TokenStream stream;

	public TokenParser(TokenStream stream) {
		this.stream = stream;
		stream.next();
	}

	public Map<String, Object> parse() {
		return parse(0);
	}

	private Map<String, Object> parse(int indent) {
		Map<String, Object> root = new LinkedHashMap<>();

		while (stream.type != TokenType.EOF) {
			if (stream.type == TokenType.NEW_LINE || stream.type == TokenType.EMPTY_LINE || stream.type == TokenType.COMMENT_LINE) {
				stream.next();
				continue;
			}

			if (stream.indent < indent) break;
			if (stream.indent > indent) throw new RuntimeException("Invalid indent");

			if (stream.type != TokenType.TEXT) throw new RuntimeException("Expected key");

			String key = new String(stream.buffer, stream.start, stream.length);
			stream.next();

			if (stream.type != TokenType.COLON) throw new RuntimeException("Expected colon");
			stream.next();

			if (stream.type == TokenType.NEW_LINE) {
				stream.next();
				Map<String,Object> child = parse(stream.indent);
				root.put(key, child);
			} else {
				if (stream.type != TokenType.TEXT) throw new RuntimeException("Expected value");
				var value = new String(stream.buffer, stream.start, stream.length);
				stream.next();

				root.put(key, value);
			}
		}
		return root;
	}
}