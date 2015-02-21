package IEMLInterface;

import java.util.Arrays;
import java.util.List;

public class IEMLLang {

	public static String[] alphabet = new String[]{"S","B","T","U","A","O","M","I","E","F"};
	
	//layer marks
	public static String[] LM = new String[]{":", ".", "-", "'", ",", "_", ";"};
	
	//layer marks for regex
	public static String[] LM_R = new String[]{":", "\\.", "-", "'", ",", "_", ";"};
	
	
	//convenience to find index of a particular layer mark;
	public static List<String> LMList = Arrays.asList(LM);	
}
