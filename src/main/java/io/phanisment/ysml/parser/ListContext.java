package io.phanisment.ysml.parser;

import java.util.List;
import java.util.ArrayList;

public class ListContext {
	public List<String> list;
	public int level;
	public int indent;
	public StringBuilder current;
	
	public ListContext(List<String> list, int level, int indent) {
		this.level = level;
		this.list = list;
		this.indent = indent;
	}
}