package com.tidesquare.toac.stmt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

class Line {
	
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

public class FormatParser {
	
	public void run(String ... files) throws IOException {
		for ( String file : files )
			runOne(file);
	}
	
	public void runOne(String file) throws IOException {
		Map<String, Object> root = new LinkedHashMap<>();
		Stack<Map<String, Object>> stack = new Stack<>();
		Map<String, Object> top = stack.push(root);
		
		try ( BufferedReader br = new BufferedReader(new FileReader("data/" + file + ".txt")); ) {
			for ( Line line : br.lines().map(Line::valueOf).collect(Collectors.toList()) ) {
				top = stack.peek();
				
				System.out.format("%d %d %s %s\n", line.depth, stack.size(), line.name, line.type);
				
				while ( line.depth < stack.size() - 1 ) {
					System.out.println("pop");
					stack.pop();
					top = stack.peek();
				}

				if ( "Object".equals(line.type) ) {
					Map<String, Object> newObj = new LinkedHashMap<>();
					top.put(line.name, newObj);
					stack.push(newObj);
				} else if ( "Array-Object".equals(line.type) ) {
					List<Object> list = new LinkedList<>();
					Map<String, Object> newObj = new LinkedHashMap<>();
					list.add(newObj);
					top.put(line.name, list);
					stack.push(newObj);
				} else if ( line.type.startsWith("Array") ) {
					List<String> list = new LinkedList<>();
					list.add(line.type.replaceFirst("Array-", "") + " " + line.desc);
					top.put(line.name, list);
				} else {
					top.put(line.name, line.type + " " + line.desc);
				}
			}
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String value = mapper.writeValueAsString(root);
		System.out.println(file + " " + value);
	}
	
	public static void main(String[] args) throws IOException {
		FormatParser app = new FormatParser();
		//app.run("detail", "info", "option", "price");
		app.run("option");
	}
}
