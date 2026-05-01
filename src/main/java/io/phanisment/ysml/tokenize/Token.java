package io.phanisment.ysml.tokenize;

/**
 * Tokenize charater and for parsing indication like EOF.
 */
public enum Token {
	EOF, // End of File
	NEW_LINE,
	EMPTY_LINE,
	COLON,
	TEXT,
	COMMENT_LINE,
	DASH,
	INDENT,
	DEDENT
}