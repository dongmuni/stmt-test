package com.tidesquare.toac.stmt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormatParser {
	
	public void run(String ... files) throws IOException {
		for ( String file : files )
			runOne(file);
	}
	
	/**
	 * @param file
	 * @throws IOException
	 */
	public void runOne(String file) throws IOException {
		Map<String, Object> root = new LinkedHashMap<>();
		Stack<Map<String, Object>> stack = new Stack<>();
		Map<String, Object> top = stack.push(root);
		
		try ( BufferedReader br = new BufferedReader(new FileReader("data/" + file + ".txt")); ) {
			int i = 0;
			
			for ( Line line : br.lines().map(Line::valueOf).collect(Collectors.toList()) ) {
				log.debug(String.format("index: %2d, depth: %d, stack: %d, name: %s, type: %s", 
						++i, line.depth, stack.size(), line.name, line.type));
				
				while ( line.depth < stack.size() - 1 ) {
					log.debug("pop");
					stack.pop();
				}
				
				top = stack.peek();

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
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		String value = mapper.writeValueAsString(root);
		log.info(file + " " + value);
	}
	
	public static void main(String[] args) throws IOException {
		FormatParser app = new FormatParser();
		app.run("detail", "info", "option", "price");
		//app.run("option");
	}
}
