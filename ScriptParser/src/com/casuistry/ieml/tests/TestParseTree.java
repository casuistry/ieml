package com.casuistry.ieml.tests;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Test;

import NewParser.ParserImpl;
import NewParser.Token;

public class TestParseTree {

	@Test
	public void test() throws Exception {
		ParserImpl parser = new ParserImpl();
		Token n = parser.parse("t.i.-s.i.-'");
		
		JSONObject tree= n.buildTree(null);
		
		n.PrintNodes("");
		
		System.out.println(tree.toString(1));
		
	}

}
