package ysml.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Deque;

import ysml.exception.YsmlParseException;
import ysml.exception.ParseErrorType;

public class TextParser {
	private final String[] lines;
	private static final String EMPTY = "";
	private static final String NEW_LINE = "\n";
	private static final String COLON = ":";
	private static final char TAB = '\t';
	private static final char SPACE = ' ';
	
	public TextParser(String text) {
		this(text.split(NEW_LINE));
	}
	
	public TextParser(String[] text) {
		this.lines = text;
	}
	
	public Map<String, Object> parse() throws YsmlParseException {
		Map<String, Object> root = new HashMap<>();
		Deque<MapContext> stack = new ArrayDeque<>();
		
		stack.push(new MapContext(root, 0));
		var state = new IndentState(IndentType.NONE, 0);
		int previous_level = 0;
		boolean previous_is_map = false;
		
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.trim().isEmpty()) continue;
			int level = this.readIndent(line, i, state);
			
			if (level > previous_level + 1) throw new YsmlParseException(ParseErrorType.INDENT_JUMP, "Indent jump from level " + previous_level + " to " + level, i + 1, 1);
			if (level > previous_level && !previous_is_map) throw new YsmlParseException(ParseErrorType.SYNTAX_ERROR, "Indent after key has value", i + 1, 1);
			
			while (stack.size() > 1 && level <= stack.peek().level) {
				stack.removeFirst();
			}
			
			MapContext parent = stack.peek();
			String[] parts = line.split(COLON, 2);
			String key = parts[0].trim();
			String value = parts.length > 1 ? parts[1].trim() : EMPTY;
			if (!value.isEmpty()) {
				parent.map.put(key, value);
				previous_is_map = false;
			} else {
				Map<String, Object> child = new HashMap<>();
				parent.map.put(key, child);
				stack.push(new MapContext(child, level));
				previous_is_map = true;
			}
			previous_level = level;
		}
		return root;
	}
	
	private int readIndent(String line, int line_number, IndentState state) throws YsmlParseException {
		int count = 0;
		int human_readable_line = line_number + 1;
		
		while (count < line.length()) {
			char c = line.charAt(count);
			if (c == SPACE || c == TAB) count++;
			else break;
		}
		
		if (count == 0) return 0;
		char indent = line.charAt(0);
		if (state.type == IndentType.NONE) {
			if (indent == TAB) {
				state.type = IndentType.TAB;
				state.size = 1;
			} else {
				state.type = IndentType.SPACE;
				state.size = count;
			}
		} else {
			if (state.type == IndentType.TAB && indent != TAB) throw new YsmlParseException(ParseErrorType.INDENT_MIXED, "Space used after tab indentation", human_readable_line, 1);
			if (state.type == IndentType.SPACE && indent != SPACE) throw new YsmlParseException(ParseErrorType.INDENT_MIXED, "Tab used after space indentation ", human_readable_line, 1);
		}
		if (state.type == IndentType.SPACE && count % state.size != 0) throw new YsmlParseException(ParseErrorType.INDENT_SIZE_MISMATCH, "Indent size must be multiple of " + state.size, human_readable_line, count);
		return state.type == IndentType.TAB ? count : count / state.size;
	}
	
	private static class IndentState {
		public IndentType type = IndentType.NONE;
		public int size = 0;
		
		public IndentState(IndentType type, int size) {
			this.type = type;
			this.size = size;
		}
	}
	
	private static enum IndentType {
		TAB,
		NONE,
		SPACE;
	}
}