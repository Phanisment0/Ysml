package ysml.parser;

import java.util.Map;

public class MapContext {
	public Map<String, Object> map;
	public int level;
	
	public MapContext(Map<String, Object> map, int level) {
		this.map = map;
		this.level = level;
	}
}