package ysml;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

import ysml.exception.YsmlParseException;
import ysml.exception.ParseErrorType;
import ysml.parser.TextParser;

public final class Ysml {
	private Ysml() {
	}
	
	/**
	 * unique behavior, if name surfix file is not .ysml or .yml will throw error
	 */
	public static Map<String, Object> load(File file) throws FileNotFoundException, IOException, YsmlParseException {
		String name = file.getName();
		if (!name.endsWith(".ysml") || !name.endsWith(".yml")) throw new FileNotFoundException("Not found file with extension .ysml or .yml");
		try (var reader = new FileReader(file)) {
			return load(reader);
		}
	}
	
	public static Map<String, Object> load(Reader reader) throws IOException, YsmlParseException {
		List<String> lines = new ArrayList<>();
		try (var buf = new BufferedReader(reader)) {
			String line;
			while ((line = buf.readLine()) != null) {
				lines.add(line);
			}
		}
		return new TextParser(lines.toArray(String[]::new)).parse();
	}
	
	public static Map<String, Object> load(String text) throws YsmlParseException {
		return new TextParser(text).parse();
	}
}