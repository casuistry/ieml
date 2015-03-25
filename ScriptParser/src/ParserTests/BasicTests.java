package ParserTests;

import static org.junit.Assert.*;

import org.junit.Test;

import NewParser.Parser;

public class BasicTests {

	@Test
	public void test() {
		
		String input, expected;
		
		input = "*S:B:.**";
		expected = "[*S:B:.**|1][S|0][B|0][E|0]";
		assertEquals("Failed: " + input, expected, Parser.run(input));
		
		input = "*S:B:T:.**";
		expected = "[*S:B:T:.**|1][S|0][B|0][T|0]";
		assertEquals("Failed: " + input, expected, Parser.run(input));
		
		input = "*S.**";
		expected = "  ^";
		assertEquals("Failed: " + input, expected, Parser.run(input));

		input = "*S:.**";
		expected = "[*S:.**|1][S|0][E|0][E|0]";
		assertEquals("Failed: " + input, expected, Parser.run(input));
		  
		
	}

}
