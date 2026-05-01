package io.phanisment.ysml.parser;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import io.phanisment.ysml.tokenize.Lexer;
import io.phanisment.ysml.tokenize.Token;

/**
 * Converts a stream of tokens into a nested Map structure.
 * 
 * This parser follows a Recursive Descent approach, specifically designed 
 * to handle hierarchical data through INDENT and DEDENT tokens.
 */
public class Parser {
	private final Lexer lexer;

	private final char[] buffer;

	public Parser(final char[] buffer) {
		if (buffer == null) throw new NullPointerException("Argument 'buffer' is can't be null"); // Null value safety
		this.buffer = buffer;
		this.lexer = new Lexer(buffer);
	}
	
	/**
	 * Entry point for parsing the buffer.
	 * @return A map representing the root structure.
	 */
	public Map<BufferedString, Object> parse() throws ParseException {
		lexer.next(); // Initialize the lexer by fetching the first token
		return parseMap();
	}

	/**
	 * Parses a block of key-value pairs.
	 * This method is recursive; it calls itself when an INDENT token is encountered
	 * to handle nested objects.
	 */
	private Map<BufferedString, Object> parseMap() throws ParseException {
		Map<BufferedString, Object> map = new HashMap<>();

		// Continue parsing until we hit the EOF or a decrease in indentation (DEDENT)
		while (lexer.token != Token.EOF && lexer.token != Token.DEDENT) {

			if (lexer.token == Token.TEXT) {
				// 1. Capture the Key
				// BufferedString is used to point to the buffer without allocation
				var key = new BufferedString(buffer, lexer.start, lexer.length, lexer.line);
				lexer.next(); // Consume the KEY

				// 2. Look for the Assignment Operator (COLON)
				if (lexer.token == Token.COLON) {	
					lexer.next(); // Consume the COLON

					// Skip any formatting tokens (newlines/empty lines) that might exist after a colon
					while (lexer.token == Token.NEW_LINE || lexer.token == Token.EMPTY_LINE) lexer.next();
					
					// 3. Determine the Value Type
					if (lexer.token == Token.INDENT) {
						// Case: Nested Object (Parent-Child Map)
						lexer.next(); // Consume the INDENT

						// Recursive call: Start a new map for the indented block
						map.put(key, this.parseMap());
						
						// After parseMap() returns, it should have hit a DEDENT.
						// We consume the DEDENT to return to the parent's indentation level.
						if (lexer.token == Token.DEDENT) lexer.next();

					} else if (lexer.token == Token.TEXT) {
						// Case: Simple Scalar Value
						var value = new BufferedString(buffer, lexer.start, lexer.length, lexer.line);
						map.put(key, value);
						lexer.next(); // Consume the VALUE
					} else if (lexer.token == Token.NEW_LINE || lexer.token == Token.EOF) map.put(key, null); // Case: Key with no value (null)
				}
			} else lexer.next(); // Skip tokens that are not keys (like stray newlines or comments)
		}
		return map;
	}
}