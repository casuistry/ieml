package NewParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Tokenizer {

	public static boolean initDone = false;
	public static Character multiplication = '*';
	public static Character addition = '+';	
	
	public static List<Character> c_alphabet = Arrays.asList(new Character[]{'E','U','A','O','S','B','T','M','F','I'});
	public static List<Character> c_smallCap = Arrays.asList(new Character[]{'y','o','e','u','a','i','j','g','s','b','t','h','c','k','m','n','p','x','d','f','l'});
	public static List<Character> c_vowels   = Arrays.asList(new Character[]{'o','a','u','e'});
	public static List<Character> c_ignore   = Arrays.asList(new Character[]{'(',')',' '});
	public static List<Character> c_wLetter  = Arrays.asList(new Character[]{'w'});
	public static List<Character> c_addOp    = Arrays.asList(new Character[]{'+'});
	public static List<Character> c_marks    = Arrays.asList(new Character[]{':', '.', '-', '\'', ',', '_', ';'});
	public static List<Character> c_star     = Arrays.asList(new Character[]{'*'});

	public static List<Character> c_verb     = Arrays.asList(new Character[]{'O', 'U', 'A'});
    public static List<Character> c_noun     = Arrays.asList(new Character[]{'M', 'S', 'B', 'T'});
    public static List<Character> c_aux      = Arrays.asList(new Character[]{'E'});
    public static List<Character> c_full      = Arrays.asList(new Character[]{'F'});
    public static List<Character> c_inf      = Arrays.asList(new Character[]{'I'});
	
	public enum GrammaticalClass {
		
		auxiliary(1),
		verb(2),		
		noun(4),		
		full(6),		
		info(7);
		
	    private int numVal;

	    GrammaticalClass(int numVal) {
	        this.numVal = numVal;
	    }

	    public int getNumVal() {
	        return numVal;
	    }
	}
	
	public enum States {
		
		state_none("invalid state"),
		state_i("initial"),		
		state_sc("small cap"),		
		state_ws("start small cap wo, wa, wu and we"),		
		state_a("primitives"),		
		state_f("node completed"),	    
		state_d("addition operation");

		
	    private final String fieldDescription;

	    States(String descr) {
	        this.fieldDescription = descr;
	    }
	    
	    public String getFieldDescription() {
	        return fieldDescription;
	    }
	    
	    public static String GetKey(States a, States b){
	    	return a.getFieldDescription()+b.getFieldDescription();
	    }
	}

	public enum Transitions {    
		t_i_sc,
		t_i_ws,
		t_i_a,
		t_f_sc,
		t_f_ws,
		t_f_a,
		t_d_sc,
		t_d_ws,
		t_d_a,
		t_a_f,    
		t_ws_sc,  
		t_sc_f,   
		t_f_d,
		t_f_f
	}
	
	public static HashMap<String, Transitions> transitionMap = new HashMap<String, Transitions>();
	static {
		transitionMap.put(States.GetKey(States.state_i, States.state_sc), Transitions.t_i_sc);
		transitionMap.put(States.GetKey(States.state_i, States.state_ws), Transitions.t_i_ws);
		transitionMap.put(States.GetKey(States.state_i, States.state_a), Transitions.t_i_a);
		transitionMap.put(States.GetKey(States.state_f, States.state_sc), Transitions.t_f_sc);
		transitionMap.put(States.GetKey(States.state_f, States.state_ws), Transitions.t_f_ws);
		transitionMap.put(States.GetKey(States.state_f, States.state_a), Transitions.t_f_a);
		transitionMap.put(States.GetKey(States.state_d, States.state_sc), Transitions.t_d_sc);
		transitionMap.put(States.GetKey(States.state_d, States.state_ws), Transitions.t_d_ws);
		transitionMap.put(States.GetKey(States.state_d, States.state_a), Transitions.t_d_a);
		transitionMap.put(States.GetKey(States.state_a, States.state_f), Transitions.t_a_f);
		transitionMap.put(States.GetKey(States.state_ws, States.state_sc), Transitions.t_ws_sc);
		transitionMap.put(States.GetKey(States.state_sc, States.state_f), Transitions.t_sc_f);
		transitionMap.put(States.GetKey(States.state_f, States.state_d), Transitions.t_f_d);
		transitionMap.put(States.GetKey(States.state_f, States.state_f), Transitions.t_f_f);
	}
	
	public static HashMap<Character, Integer> m_alphabet = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_smallCap = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_vowels   = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_ignore   = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_wLetter  = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_addOp    = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_marks    = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_star     = new HashMap<Character, Integer>();
	
	static {
		for (Character c : c_alphabet)
			m_alphabet.put(c, c_alphabet.indexOf(c));
		for (Character c : c_smallCap)
			m_smallCap.put(c, c_smallCap.indexOf(c));
		for (Character c : c_vowels)
			m_vowels.put(c, c_vowels.indexOf(c));
		for (Character c : c_ignore)
			m_ignore.put(c, c_ignore.indexOf(c));
		for (Character c : c_wLetter)
			m_wLetter.put(c, c_wLetter.indexOf(c));
		for (Character c : c_addOp)
			m_addOp.put(c, c_addOp.indexOf(c));
		for (Character c : c_marks)
			m_marks.put(c, c_marks.indexOf(c));
		for (Character c : c_star)
			m_star.put(c, c_star.indexOf(c));
	}
	
	public static HashMap<String, Token> primitiveLookup = new HashMap<String, Token>();
	public static HashMap<String, String> primitiveAbbrev = new HashMap<String, String>();
	static {
		try {
			primitiveLookup.put("O:", new ParserImpl().parse("U:+A:"));
			primitiveLookup.put("M:", new ParserImpl().parse("S:+B:+T:"));
			primitiveLookup.put("F:", new ParserImpl().parse("U:+A:+S:+B:+T:"));
			primitiveLookup.put("I:", new ParserImpl().parse("E:+U:+A:+S:+B:+T:"));
		}catch (Exception e) {
			System.out.println("mapping failed: " + e.getMessage());
		}
		
		for (String key : primitiveLookup.keySet())
			primitiveAbbrev.put(primitiveLookup.get(key).GetName(), key);
	}
	
	public static HashMap<String, Token> scLookup = new HashMap<String, Token>();
	public static HashMap<String, String> scAbbrev = new HashMap<String, String>();
	static {
		try {		
			scLookup.put("wo.", new ParserImpl().parse("U:U:E:."));
			scLookup.put("wa.", new ParserImpl().parse("U:A:E:."));
			scLookup.put("wu.", new ParserImpl().parse("A:U:E:."));
			scLookup.put("we.", new ParserImpl().parse("A:A:E:."));
			scLookup.put("y.", new ParserImpl().parse("U:S:E:."));
			scLookup.put("o.", new ParserImpl().parse("U:B:E:."));
			scLookup.put("e.", new ParserImpl().parse("U:T:E:."));
			scLookup.put("u.", new ParserImpl().parse("A:S:E:."));
			scLookup.put("a.", new ParserImpl().parse("A:B:E:."));
			scLookup.put("i.", new ParserImpl().parse("A:T:E:."));
			scLookup.put("j.", new ParserImpl().parse("S:U:E:."));
			scLookup.put("g.", new ParserImpl().parse("S:A:E:."));
			scLookup.put("s.", new ParserImpl().parse("S:S:E:."));
			scLookup.put("b.", new ParserImpl().parse("S:B:E:."));
			scLookup.put("t.", new ParserImpl().parse("S:T:E:."));
			scLookup.put("h.", new ParserImpl().parse("B:U:E:."));
			scLookup.put("c.", new ParserImpl().parse("B:A:E:."));
			scLookup.put("k.", new ParserImpl().parse("B:S:E:."));
			scLookup.put("m.", new ParserImpl().parse("B:B:E:."));
			scLookup.put("n.", new ParserImpl().parse("B:T:E:."));
			scLookup.put("p.", new ParserImpl().parse("T:U:E:."));
			scLookup.put("x.", new ParserImpl().parse("T:A:E:."));
			scLookup.put("d.", new ParserImpl().parse("T:S:E:."));
			scLookup.put("f.", new ParserImpl().parse("T:B:E:."));
			scLookup.put("l.", new ParserImpl().parse("T:T:E:."));
		}catch (Exception e) {
			System.out.println("mapping failed: " + e.getMessage());
		}
		
		for (String key : scLookup.keySet())
			scAbbrev.put(scLookup.get(key).GetName(), key);
	}
	
	public static HashMap<Integer, Token> emptyLookup = new HashMap<Integer, Token>();
	static {
		try {
			for (int i = 0; i < 7; i++)
				emptyLookup.put(i, new ParserImpl().parse(getEmptySequence(i)));
		}catch (Exception e) {
			System.out.println("mapping failed: " + e.getMessage());
		}
		
		initDone = true;
	}
	
	public static String getEmptySequence(int l) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("E");
		for (int i = 0; i <= l; i++)
			builder.append(c_marks.get(i));
		return builder.toString();		
	}
	
	public static String MakeParsable(String ieml) {
		
		if (ieml == null) 
			return null;
		
		// handle abbreviation for addition
		for (String abrev : primitiveAbbrev.keySet()) {
			ieml = ieml.replace(abrev, primitiveAbbrev.get(abrev));
		}
		
		// handle abbreviation for multiplication
		for (String abrev : scAbbrev.keySet()) {
			ieml = ieml.replace(abrev, scAbbrev.get(abrev));
		}
		
		// handle trailing empties
		int index = c_marks.indexOf(ieml.charAt(ieml.length()-1)) - 1;
		for (int i = index; i >= 0; i--){
			String empties = emptyLookup.get(i).GenerateSequenceForTable(false) + c_marks.get(i+1);
			//System.out.println(empties);
			for (int j=0;j<3;j++){
				ieml = ieml.replace(empties, c_marks.get(i+1).toString());
			}
		}
		
		return ieml;
	}
	
	protected States currentState;
	protected int counter;
	
	public Tokenizer() {
		currentState = States.state_i;
		counter = 0;
	}
	
	public void Reset() {
		currentState = States.state_i;
		counter = 0;
		reset();
	}
	
	protected void reset() {}
	
	public States GetCurrentState() {
		return currentState;
	}
	
	public int GetCounter(){
		return counter;
	}
	
	public void nextChar(Character charIn) throws Exception {		
		if (m_ignore.containsKey(charIn)) 
			return;
		stateDispatcher(charIn);			
	}
	
	private States stateDispatcher(char c) throws Exception {					
		
        if (m_addOp.containsKey(c)){			
			if (currentState == States.state_f)
				return StateChangeActions(States.state_d, c);
			throw new Exception("cannot process " + c + " in state " + currentState.getFieldDescription());
		}
		else if (m_wLetter.containsKey(c)){
			if (currentState == States.state_i || currentState == States.state_d || currentState == States.state_f) 
				return StateChangeActions(States.state_ws, c);	
			throw new Exception("cannot process " + c + " in state " + currentState.getFieldDescription());
		}		
		else if (m_marks.containsKey(c)){
			if (currentState == States.state_a || currentState == States.state_f || currentState == States.state_sc) 
				return StateChangeActions(States.state_f, c);
			throw new Exception("cannot process " + c + " in state " + currentState.getFieldDescription());
		}		
		else if (m_smallCap.containsKey(c)){
			if (currentState == States.state_i || currentState == States.state_d || currentState == States.state_f || currentState == States.state_ws) 
				return StateChangeActions(States.state_sc, c);
			throw new Exception("cannot process " + c + " in state " + currentState.getFieldDescription());
		}
		else if (m_alphabet.containsKey(c)){
			if (currentState == States.state_i || currentState == States.state_d || currentState == States.state_f) 
				return StateChangeActions(States.state_a, c);
			throw new Exception("cannot process " + c + " in state " + currentState.getFieldDescription());
		}

		//TODO: test case
		throw new Exception("unrecognized " + c);
	}	
			
	private States StateChangeActions(States next, Character c) throws Exception {
		
		String transitionKey = States.GetKey(currentState, next);		
		if (!transitionMap.containsKey(transitionKey))
			//sanity
			throw new Exception("missing transition from "+currentState+" to "+next);
		
		switch (transitionMap.get(transitionKey)){
	
			case t_a_f:				
				a_a_f(c);
				break;
			case t_sc_f:			
				a_sc_f(c);
				break;				
			case t_d_a:				
				a_d_a(c);
				break;
			case t_d_sc:			
				a_d_sc(c);
				break;
			case t_d_ws:			
				a_d_ws(c);
				break;
			case t_f_a:				
				a_f_a(c);			
				break;
			case t_f_sc:			
				a_f_sc(c);
				break;
			case t_f_ws:						
				a_f_ws(c);
				break;
			case t_i_a:				
				a_i_a(c);
				break;
			case t_i_sc:			
				a_i_sc(c);
				break;
			case t_i_ws:			
				a_i_ws(c);
				break;
			case t_f_d:				
				a_f_d(c);
				break;
			case t_f_f:				
				a_f_f(c);
				break;				
			case t_ws_sc:			
				a_ws_sc(c);						
				break;
			default:
				//sanity
				throw new Exception("undefined transition from "+currentState+" to "+next);		
		}
		
		States prev = currentState;
		currentState = next;
		
		return prev;
	}
	
	protected void a_i_ws(Character c) throws Exception{}	
	protected void a_i_sc(Character c) throws Exception{}
	protected void a_i_a(Character c) throws Exception{}	
	protected void a_f_ws(Character c) throws Exception{}
	protected void a_f_sc(Character c) throws Exception{}
	protected void a_f_a(Character c) throws Exception{}
	protected void a_d_ws(Character c) throws Exception{}
	protected void a_d_sc(Character c) throws Exception{}
	protected void a_d_a(Character c) throws Exception{}
	protected void a_f_d(Character c) throws Exception{}
	protected void a_a_f(Character c) throws Exception{}
	protected void a_sc_f(Character c) throws Exception{}
	protected void a_ws_sc(Character c) throws Exception{}	
	protected void a_f_f(Character c) throws Exception{}	
}
