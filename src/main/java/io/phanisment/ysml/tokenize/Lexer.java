package io.phanisment.ysml.tokenize;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Responsible for converting a character buffer into a stream of tokens.
 * 
 * This Lexer implements a manual state machine to handle:
 * 1. Indentation-based blocking (INDENT/DEDENT) via a stack-based approach.
 * 2. Zero-allocation tokenization by storing only 'start' and 'length' pointers.
 * 3. Unicode support using codePoint processing.
 */
public final class Lexer {
	// Constant character definition for better readability
	private static final char TAB      = '\t';
	private static final char NEW_LINE = '\n';
	private static final char SPACE    =  ' ';
	private static final char COLON    =  ':';
	private static final char HASH     =  '#';
	private static final char DASH     =  '-';

	private final char[] buffer;

	/** Flag to track if the lexer is at the beginning of a line to process indentation */
	private boolean start_line = true;
	
	// Current state of the lexer
	public int pos;      // Read character postition
	public int start;    // Start of reading the character
	public int length;   // Length of reading the character
	public int line = 0; // Tracking line numbers for error reporting 
	
	public Token token;  // Represent certain character to token, useful for readability and parsing
	
	/** Stack to track nested indentation levels */
	public Deque<Integer> indents = new ArrayDeque<>();

	/** Counter for multiple DEDENT tokens that need to be emitted sequentially */
	public int dedent_pending = 0;

	public Lexer(final char[] buffer) {
		if (buffer == null) throw new NullPointerException("Argument 'buffer' is can't be null"); // Null value safety
		this.buffer = buffer;
		this.indents.push(0); // Base level indentation is always 0
	}

	/**
	 * Advance to the next token in buffer.
	 */
	public void next() {
		//  Prioritize clearing pending dedents from queue before reading new characters
		if (dedent_pending > 0) {
			dedent_pending--;
			token = Token.DEDENT;
			return;
		}

		// Chek for EOF(End Of File)
		if (pos >= buffer.length) {
			token = Token.EOF;
			return;
		}

		// Handle empty lines at the start: prevents issues with parser logic
		if (start_line && pos < buffer.length && Character.codePointAt(buffer, pos) == NEW_LINE) {
			token = Token.EMPTY_LINE;
			start = pos;
			length = 1;
			line++;
			pos++;
			start_line = true;
			return;
		}

		// Indentation Logic: Triggered only at the start of a new line
		if (start_line) {
			int current_indent = 0;
			// Count whitespace characters (Tabs or Spaces)
			while (pos < buffer.length && (Character.codePointAt(buffer, pos) == TAB || Character.codePointAt(buffer, pos) == SPACE)) {
				current_indent++;
				pos++;
			}

			// IMPORTANT: Disable start_line once the leading whitespace is consumed.
			// This prevents the lexer from re-running indentation logic for the rest of the line content.
			start_line = false;
			int last_indent = indents.peek();

			// Case A: Increase Indentation
			if (current_indent > last_indent) {
				indents.push(current_indent);
				token = Token.INDENT;
				return;
			}

			// Case B: Decreased indentation (may require multiple DEDENT tokens)
			if (current_indent < last_indent) {
				while (indents.peek() > current_indent) {
					indents.pop();
					dedent_pending++;
				}

				// Return the first DEDENT immediately, store the rest in dedent_pending
				if (dedent_pending > 0) {
					dedent_pending--;
					token = Token.DEDENT;
					return;
				}
			}
		}

		// Handle Single-character tokens and comments
		int c = Character.codePointAt(buffer, pos);
		switch (c) {
			case NEW_LINE:
				token = Token.NEW_LINE;
				start = pos;
				length = 1;
				line++;
				pos++;
				start_line = true; // Signal the start of a new line for the next call
				return;
			case COLON:
				token = Token.COLON;
				start = pos;
				length = 1;
				pos++;
				return;
			case HASH:
				start = pos;
				pos++;
				token = Token.COMMENT_LINE;
				while (pos < buffer.length && Character.codePointAt(buffer, pos) != NEW_LINE) pos++; // Consume characters until the end of the line
				length = pos - start;
				token = Token.COMMENT_LINE;
				return;
			case DASH:
				token = Token.DASH;
				start = pos;
				length = 1;
				pos++;
				return;
		}

		// Default Case: TEXT token
		// Consumes characters until it hits a reserved symbol or a line break
		start = pos;
		while (pos < buffer.length) {
			int next_char = Character.codePointAt(buffer, pos);
			if (next_char == COLON || next_char == NEW_LINE || next_char == HASH) break;
			pos += Character.charCount(next_char);
		}

		// Calculate the relative length of the text segment.
		// This 'start' and 'length' pair serves as a pointer to the original buffer.
		length = pos - start;
		token = Token.TEXT;
	}

	@Override
	public String toString() {
		return "Lexer(\n  pos=" + pos + "\n  line=" + line + "\n  start_line=" + start_line + "\n  type=" + token + "\n  start=" + start + "\n  length=" + length + "\n  indents=" + indents + "\n  dedent_pending=" + dedent_pending + "\n)";
	}
}