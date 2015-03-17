package IEMLInterface;

import java.util.Arrays;
import java.util.List;

public class IEMLLang {
	
	public static String[] alphabet = new String[]{"S","B","T","U","A","O","M","I","E","F"};
	
	//layer marks
	public static String[] LM = new String[]{":", ".", "-", "’", ",", "_", ";"};
	
	//layer marks for regex
	public static String[] LM_R = new String[]{":", "\\.", "-", "’", ",", "_", ";"};
		
	public static String Addition = "+";
	public static String Multiplication = "*";
	
	//convenience to find index of a particular layer mark;
	public static List<String> LMList = Arrays.asList(LM);	
	public static List<String> AlphabetList = Arrays.asList(alphabet);
	
	public static String GetEmpty(){
		return "E:";
	}
	
	public static boolean IsEmpty(String s){
		return s.equals("E:");
	}
	
	public static boolean IsOpcodeValid(String opcode){
		if (opcode != null && (
				opcode.equals(IEMLLang.Addition) || opcode.equals(IEMLLang.Multiplication))) {
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
}
