package io.phanisment.ysml.exception;

public final class ParseError {
	private final ParseErrorType type;
	private final String message;
	private final int line;
	private final int column;
	
	public ParseError(ParseErrorType type, String message, int line, int column) {
		this.type = type;
		this.message = message;
		this.line = line;
		this.column = column;
	}
	
	public ParseErrorType getType() {
		return type;
	}
	
	public String getMessage() {
		return message;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}
	
	@Override
	public String toString() {
		return "[" + type + "] line " + line + ", col " + column + ": " + message;
	}
}