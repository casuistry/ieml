package NewParser;

import java.util.ArrayList;
import java.util.Stack;

public class ParserImpl extends Tokenizer {

	Stack<Token> stack;
	
	public ParserImpl() {
		stack = new Stack<Token>();
	}
	
	public Token parse(String input) throws Exception {

		for (counter = 0; counter < input.length(); counter++){			
			Character charIn = new Character(input.charAt(counter));						
			nextChar(charIn);
		}
		
		//TODO: test case
		if (currentState != States.state_done) {
			throw new Exception("bad final state, missing *");
		}
		
		return stack.pop();
	}
	
	protected void reset() {
		stack = new Stack<Token>();
	}
	
	protected void a_i_ws(Character c) throws Exception{ 
		stack.push(new Token(c));
	}	
	protected void a_i_sc(Character c) throws Exception{ 
		stack.push(new Token(c));
	}
	protected void a_i_a(Character c) throws Exception{ 
		stack.push(new Token(c));
	}	
	protected void a_f_ws(Character c) throws Exception{ 
		pushNodeFromFinal(c);
	}
	protected void a_f_sc(Character c) throws Exception{ 
		pushNodeFromFinal(c);
	}
	protected void a_f_a(Character c) throws Exception{ 
		pushNodeFromFinal(c);
	}
	protected void a_d_ws(Character c) throws Exception{ 
		pushNodeFromAdd(c);
	}
	protected void a_d_sc(Character c) throws Exception{ 
		pushNodeFromAdd(c);
	}
	protected void a_d_a(Character c) throws Exception{ 
		pushNodeFromAdd(c);
	}
	protected void a_f_d(Character c) throws Exception{ 
		stack.push(null);
	}
	protected void a_a_f(Character c) throws Exception{ 
		//TODO:test case
		if (m_marks.get(c) != 0)
			throw new Exception("layer mark must be '" + c_marks.get(0) + "'");		
		
		//This is a node of layer 0.		
		Token pop = stack.pop();	
		pop.AppendToName(c);
		pop.layer = m_marks.get(c);
			
		pop.ComputeTaille();
		pushNode(pop);
	}
	protected void a_sc_f(Character c) throws Exception{ 
		//TODO:test case
		if (m_marks.get(c) != 1)
			throw new Exception("layer mark must be '" + c_marks.get(1) + "'");
				
		Token pop = stack.pop();	
		pop.AppendToName(c);
		pop.layer = m_marks.get(c);
		pop.opCode = multiplication;
		pop.AddNodes(scLookup.get(pop.GetName()).nodes);
		pop.ComputeTaille();
		pushNode(pop);
	}
	protected void a_ws_sc(Character c) throws Exception{ 		
		//TODO:test case
		if (!m_vowels.containsKey(c)) 
			throw new Exception("must use vowel");				
		stack.peek().AppendToName(c);
	}
	protected void a_f_f(Character c) throws Exception{
		
		Token tempNode = stack.peek();
		
		//TODO:test case
		if (tempNode.layer != m_marks.get(c)-1)
			throw new Exception("bad layer mark, expecting " + c_marks.get(tempNode.layer+1));
		
		Stack<Token> tempStack = new Stack<Token>();		
		for (int i = 0; i < 3; i++) {			
			if (stack.isEmpty())
				break;			
			tempNode = stack.peek();			
			if (tempNode == null)
				break;									
			if (tempNode.layer != m_marks.get(c)-1)
				break;			
			tempStack.push(stack.pop());
		}
		
		//sanity
		if (tempStack.isEmpty())
			throw new Exception("could not find nodes in multiplication relation");
				
		Token newNode = new Token(null, multiplication, m_marks.get(c));	
		while (!tempStack.isEmpty()){
			Token pop = tempStack.pop();
			newNode.AppendToName(pop.GetName());
			newNode.AddNode(pop);
		}
				
		newNode.AppendToName(c);
		
		if (newNode.layer == 1) {
			if (scAbbrev.containsKey(newNode.GetName()))
				throw new Exception("Abbreviation needed: " + scAbbrev.get(newNode.GetName()));					
		}
		
		if (initDone && newNode.HasTrailingEmpty())
			throw new Exception("trailing E");
		
		//empties not added to this name
		while (newNode.nodes.size() < 3)	
			newNode.AddNode(emptyLookup.get(newNode.layer-1));			
		
		newNode.ComputeTaille();	
		
		pushNode(newNode);
	}
	
	protected void a_f_p(Character c) throws Exception{
		//TODO:test case
		if (stack.size() > 1)
			throw new Exception("missing layer mark?");
	}
	
	//==================================================================================
	
	private void pushNodeFromAdd(Character c) throws Exception{
		//sanity
		if (stack.isEmpty()) 
			throw new Exception("pushNodeFromAdd cannot be used here, stack empty");
		//sanity
		if  (stack.peek() != null)
			throw new Exception("pushNodeFromAdd cannot be used here, not an addition");
		stack.pop(); //eat
		Token tempNode = stack.peek();
		
		//TODO: test case
		if (tempNode.layer == 0 && !m_alphabet.containsKey(c))
			throw new Exception("small cap cannot be used here");
		
		stack.push(null);
		stack.push(new Token(c));
	}
	
	private void pushNodeFromFinal(Character c) throws Exception{	
		
		//sanity
		if (stack.isEmpty() || stack.peek() == null) 
			throw new Exception("addNodeFromFinal cannot be used");
		
		Stack<Token> tempStack = new Stack<Token>();		
		for (int i = 0; i< 3; i++){									
			tempStack.push(stack.pop());			
			if (stack.isEmpty() || stack.peek() == null)
				break;			
			if (stack.peek().layer != tempStack.peek().layer) 
				break;			
			
			//TODO: test case
			if (tempStack.peek().layer == 0 && !m_alphabet.containsKey(c))
				throw new Exception("small cap cannot be used here");
		}
		
		//TODO: test case
		if (tempStack.size() == 3)
			throw new Exception("missing layer mark");
		
		while (!tempStack.isEmpty())
			stack.push(tempStack.pop());
		
		stack.push(new Token(c));	
	}
	
	private void pushNode(Token n) throws Exception{

		//sanity
		if (n == null)
			throw new Exception("null node");		
		//sanity
		if (n.layer < 0) 
			throw new Exception("negative node");
		
		if (stack.isEmpty() || stack.peek() != null) {
			stack.push(n);
			return;
		}

		stack.pop();
		Token popped = stack.pop();
		
		//sanity
		if (popped.layer < 0) 
			throw new Exception("negative previous node");
			
		if (n.layer != popped.layer) {
			stack.push(popped);
			stack.push(null);
			stack.push(n);
			return;
		}
		
		Token newNode;
		if (popped.opCode == addition){
			newNode = popped;
		}
		else {
			newNode = new Token(null, addition, popped.layer);
			newNode.AppendToName(popped.GetName());
			newNode.AddNode(popped);
		}
		
		newNode.AppendToName(addition);
		newNode.AppendToName(n.GetName());
		
		ArrayList<Token> children = newNode.nodes;
		Token lastChild = children.get(children.size()-1);	
		
		//TODO:testcase
		if (!lastChild.EvaluateOrder(n))
			throw new Exception("standard order is not respected");
		
		//TODO: test cases
		CheckExclusions(children, n);
		
		newNode.AddNode(n);
		stack.push(newNode);
	}
	
	private void CheckExclusions(ArrayList<Token> tokens, Token newToken) throws Exception {
		if (newToken.layer == 0) {
			Character c = newToken.GetName().charAt(0);
			if (Tokenizer.m_alphabet.containsKey(c)) {
				if (c.equals('I')){
					if (tokens.size() != 0)
						throw new Exception("cannot establish additive relation");
				}
				if (c.equals('F')){
					if (tokens.size() != 0)
						throw new Exception("cannot establish additive relation");					
				}
				if (c.equals('M')){
					for (Token t : tokens) {
						Character _c = t.GetName().charAt(0);
						if (_c.equals('S') || _c.equals('B') || _c.equals('T'))
							throw new Exception("M already contains previous characters");
						if (_c.equals('O'))
							throw new Exception("abbreviation needed");
					}										
				}
				if (c.equals('O')){
					for (Token t : tokens) {
						Character _c = t.GetName().charAt(0);
						if (_c.equals('U') || _c.equals('A') )
							throw new Exception("O already contains previous characters");
					}										
				}
				if (c.equals('S')){
					boolean mutex = false;
					for (Token t : tokens) {
						Character _c = t.GetName().charAt(0);
						if (_c.equals('M'))
							throw new Exception("S contained in previous character");
						if (_c.equals('B')) {
							if (mutex)
								throw new Exception("abbreviation needed");
							mutex = true;
						}
						if (_c.equals('T')) {
							if (mutex)
								throw new Exception("abbreviation needed");
							mutex = true;
						}
					}										
				}
				if (c.equals('B')){
					boolean mutex = false;
					for (Token t : tokens) {
						Character _c = t.GetName().charAt(0);
						if (_c.equals('M'))
							throw new Exception("B contained in previous character");
						if (_c.equals('S')) {
							if (mutex)
								throw new Exception("abbreviation needed");
							mutex = true;
						}
						if (_c.equals('T')) {
							if (mutex)
								throw new Exception("abbreviation needed");
							mutex = true;
						}
					}										
				}
				if (c.equals('T')){
					boolean mutex = false;
					for (Token t : tokens) {
						Character _c = t.GetName().charAt(0);
						if (_c.equals('M'))
							throw new Exception("T contained in previous character");
						if (_c.equals('S')) {
							if (mutex)
								throw new Exception("abbreviation needed");
							mutex = true;
						}
						if (_c.equals('B')) {
							if (mutex)
								throw new Exception("abbreviation needed");
							mutex = true;
						}
					}										
				}
				if (c.equals('U')){
					for (Token t : tokens) {
						Character _c = t.GetName().charAt(0);
						if (_c.equals('O'))
							throw new Exception("U contained in previous character");
						if (_c.equals('A')) 
							throw new Exception("abbreviation needed");
					}										
				}
				if (c.equals('A')){
					for (Token t : tokens) {
						Character _c = t.GetName().charAt(0);
						if (_c.equals('O'))
							throw new Exception("A contained in previous character");
						if (_c.equals('U')) 
							throw new Exception("abbreviation needed");
					}										
				}
			}
		}
	}
}
