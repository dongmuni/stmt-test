package com.tidesquare.toac.stmt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Line {
	
	int depth;
	String name;
	String type;
	String desc;
	
	static Pattern LINE_PATTERN = Pattern.compile("^(\\S+)\\s+(\\S+)\\s+(.+)$");

	public Line(int depth, String name, String type, String desc) {
		super();
		this.depth = depth;
		this.name = name;
		this.type = type;
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "Line [depth=" + depth + ", name=" + name + ", type=" + type + ", desc=" + desc + "]";
	}

	public static Line valueOf(String line) {
		int depth = 0;
		String name = null;
		String type = null;
		String desc = null;
		int i = 0;

		loop: for (; i < line.length(); i++) {
			int ch = line.charAt(i);
			switch (ch) {
			case ' ':
				// do nothing
				break;
			case '-':
				depth++;
				break;
			default:
				break loop;
			}
		}
		
		Matcher matcher = LINE_PATTERN.matcher(line.substring(i));
		if ( !matcher.matches() )
			throw new IllegalArgumentException("Invalid format: " + line);
		
		name = matcher.group(1);
		type = matcher.group(2);
		desc = matcher.group(3);
		return new Line(depth, name, type, desc.replaceAll("\"", "'").trim());
	}
}
