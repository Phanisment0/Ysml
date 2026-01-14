package ysml.exception;

public final class YsmlParseException extends Exception {
	private final ParseError error;
	
	public YsmlParseException(ParseErrorType type, String message, int line, int column) {
		this(new ParseError(type, message, line, column));
	}
	
	public YsmlParseException(ParseError error) {
		super(error.toString());
		this.error = error;
	}
	
	public ParseError getError() {
		return error;
	}
}