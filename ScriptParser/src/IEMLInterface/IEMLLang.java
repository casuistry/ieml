package IEMLInterface;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class IEMLLang {
	
	public static String[] alphabet = new String[]{"S","B","T","U","A","O","M","I","E","F"};
	
	//layer marks
	public static String[] LM = new String[]{":", ".", "-", "’", ",", "_", ";"};
	
	//layer marks for regex
	public static String[] LM_R = new String[]{":", "\\.", "-", "’", ",", "_", ";"};
	
	//patterns
	public static Pattern[] patternDetector = new Pattern[] { 
		Pattern.compile("(\\w+)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[0]+"(\\+.+?"+IEMLLang.LM_R[0]+")*)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[1]+"(\\+.+?"+IEMLLang.LM_R[1]+")*)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[2]+"(\\+.+?"+IEMLLang.LM_R[2]+")*)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[3]+"(\\+.+?"+IEMLLang.LM_R[3]+")*)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[4]+"(\\+.+?"+IEMLLang.LM_R[4]+")*)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[5]+"(\\+.+?"+IEMLLang.LM_R[5]+")*)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[6]+"(\\+.+?"+IEMLLang.LM_R[6]+")*)")
	};
	public static Pattern[] layerMarkDetectors = new Pattern[] { 
		Pattern.compile("(\\w+"+IEMLLang.LM_R[0]+")"),
		Pattern.compile(".+?"+IEMLLang.LM_R[1]),
		Pattern.compile(".+?"+IEMLLang.LM_R[2]),
		Pattern.compile(".+?"+IEMLLang.LM_R[3]),
		Pattern.compile(".+?"+IEMLLang.LM_R[4]),
		Pattern.compile(".+?"+IEMLLang.LM_R[5]),
		Pattern.compile(".+?"+IEMLLang.LM_R[6])
	};
	
	public static String Addition = "+";
	public static String Multiplication = "*";
	
	//convenience to find index of a particular layer mark;
	public static List<String> LMList = Arrays.asList(LM);	
	public static List<String> AlphabetList = Arrays.asList(alphabet);
	
	public static String GetEmpty(){
		return "E";
	}
	
	public static String GetTerm(){
		return "Z";
	}
	
	public static String GetMorpheme(){
		return "R";
	}
	
	public static String GetWord(){
		return "W";
	}
	
	public static String GetClause1(){
		return "C1";
	}
	
	public static boolean IsEmpty(String s){
		return s.equals("E:") || s.equals("EEE");
	}
	
	public static boolean IsOpcodeValid(String opcode){
		if (opcode != null && (opcode.equals(IEMLLang.Addition) || opcode.equals(IEMLLang.Multiplication))) {
			return true;
		}
		return false;
	}
	
	public static boolean IsLayerValid(int layer){
		if (layer >= 0 && layer <= IEMLLang.LM_R.length) {
			return true;
		}
		return false;
	}
	
	public static boolean IsParamNumberValid(int num, String op) throws Exception{
		
		if (num == 0 && op == null)
			return true;
		
		if (!IsOpcodeValid(op))
			throw new Exception("Unknown operation");
		
		if (op.equals(Addition)){
			return num > 1;
		}
		else {
			return num >= 1 && num <= 3;
		}
	}
}
