package ysml.exception;

public enum ParseErrorType {
	INDENT_MIXED,
	INDENT_SIZE_MISMATCH,
	INDENT_JUMP,
	FILE_EXTENSION,
	SYNTAX_ERROR;
}