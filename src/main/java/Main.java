import ysml.Ysml;

import ysml.exception.YsmlParseException;

public final class Main {
	public static void main(String[] args) throws YsmlParseException {
		System.out.println("Result: " + Ysml.load("""
		skill:
		    run:
		"""));
	}
}