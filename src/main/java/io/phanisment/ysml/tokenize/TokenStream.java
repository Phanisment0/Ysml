package io.phanisment.ysml.tokenize;

public class TokenStream {
	private static final char TAB           = '\t';
	private static final char NEW_LINE      = '\n';
	private static final char SPACE         =  ' ';
	private static final char COLON         =  ':';
	private static final char OPEN_BRACKET  =  '{';
	private static final char CLOSE_BRACKET =  '}';
	private static final char HASH          =  '#';

	private int pos;
	private boolean start_line = true;
	
	public final char[] buffer;
	public TokenType type;
	public int indent;
	public int start;
	public int length;

	public TokenStream(char[] buffer) {
		if (buffer == null) throw new IllegalArgumentException("buffer cant be null");
		this.buffer = buffer;
	}

	public char[] peek() {
		char[] out = new char[length];
		System.arraycopy(buffer, start, out, 0, length);
		return out;
	}

	public void skip(int length) {
		pos += length;
	}

	public void next() {
		if (pos >= buffer.length) {
			type = TokenType.EOF;
			return;
		}

		// Handle empty line, this will make issues in parser if doest have its own token.
		if (start_line && pos < buffer.length && Character.codePointAt(buffer, pos) == NEW_LINE) {
			type = TokenType.EMPTY_LINE;
			start = pos;
			length = 1;
			pos++;
			start_line = true;
			return;
		}

		// Handle Indent
		if (start_line) {
			indent = 0;
			while (pos < buffer.length && (Character.codePointAt(buffer, pos) == SPACE || Character.codePointAt(buffer, pos) == TAB)) {
				indent++;
				pos++;
			}
			start_line = false;
		}

		// Im bit worried about emoji support
		// maybe ill check more in to this later.
		int c = Character.codePointAt(buffer, pos);
		switch (c) {
			case NEW_LINE:
				type = TokenType.NEW_LINE;
				start = pos;
				length = 1;
				pos++;
				start_line = true;
				return;
			case COLON:
				type = TokenType.COLON;
				start = pos;
				length = 1;
				pos++;
				return;
			case OPEN_BRACKET:
				type = TokenType.OPEN_BRACKET;
				start = pos;
				length = 1;
				pos++;
				return;
			case CLOSE_BRACKET:
				type = TokenType.CLOSE_BRACKET;
				start = pos;
				length = 1;
				pos++;
				return;
			case HASH:
				start = pos;
				pos++;
				type = TokenType.COMMENT_LINE;
				while (pos < buffer.length && buffer[pos] != NEW_LINE) pos++;
				length = pos - start;
				type = TokenType.COMMENT_LINE;
				return;
		}
		start = pos;
		while (pos < buffer.length) {
			int next_char = Character.codePointAt(buffer, pos);
			if (next_char == COLON || next_char == NEW_LINE || next_char == OPEN_BRACKET || next_char == CLOSE_BRACKET) break;
			pos += Character.charCount(next_char);
		}
		length = pos - start;
		type = TokenType.TEXT;
	}
}