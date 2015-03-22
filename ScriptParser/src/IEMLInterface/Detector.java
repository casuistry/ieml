package IEMLInterface;

import java.util.ArrayList;

public class Detector {
	
	protected ArrayList<String> pattern = new ArrayList<String>();
	
	public void Next(String n){		
		pattern.add(n);		
	}
	
	public String GetResult() throws Exception {		
		
		if (isMorpheme())
			return IEMLLang.GetMorpheme();
		else if (isWord()){
			return IEMLLang.GetWord();
		}
		else if (isClause()){
			return IEMLLang.GetClause1();
		}
		else {
			throw new Exception("could not detect: " + getSequence());
		}
	}
	
	protected boolean isMorpheme(){
		
		if (!pattern.get(0).equals(IEMLLang.Addition))
			return false;
		
		for (int i = 1; i < pattern.size(); i++){
			if (!pattern.get(i).equals(IEMLLang.GetTerm()))
				return false;				
		}
		
		return true;
	}
	
	protected boolean isWord(){
		
		String temp;
		
		if (pattern.size() != 4)
			return false;
		
		if (!pattern.get(0).equals(IEMLLang.Multiplication))
			return false;
		
		temp = pattern.get(1);		
		if (!temp.equals(IEMLLang.GetMorpheme()) && !temp.equals(IEMLLang.GetTerm()))
			return false;
		
		temp = pattern.get(2);		
		if (!temp.equals(IEMLLang.GetEmpty()))
			return false;
		
		temp = pattern.get(3);		
		if (!temp.equals(IEMLLang.GetMorpheme()) && !temp.equals(IEMLLang.GetTerm()))
			return false;
		
		return true;
	}
	
	protected boolean isClause(){
		
		String temp;
		
		if (pattern.size() != 4)
			return false;
		
		if (!pattern.get(0).equals(IEMLLang.Multiplication))
			return false;
		
		//Z,R,W only 
		temp = pattern.get(1);		
		if (!temp.equals(IEMLLang.GetMorpheme()) && !temp.equals(IEMLLang.GetTerm()) && !!temp.equals(IEMLLang.GetWord()))
			return false;
		
		temp = pattern.get(2);		
		if (!temp.equals(IEMLLang.GetMorpheme()) && 
				!temp.equals(IEMLLang.GetTerm()) && 
				!temp.equals(IEMLLang.GetWord()) &&
				!temp.equals(IEMLLang.GetEmpty()))
			return false;
		
		temp = pattern.get(3);		
		if (temp.equals(IEMLLang.GetEmpty())){
			if (!pattern.get(2).equals(IEMLLang.GetEmpty()))
				return false;
		}
		else{
			if (!temp.equals(IEMLLang.GetMorpheme()) && !temp.equals(IEMLLang.GetTerm()) && !!temp.equals(IEMLLang.GetWord()))
				return false;
		}
		
		return true;
	}
	
	protected String getSequence() {
		StringBuilder out = new StringBuilder();
		for (String s : pattern)
		{
		  out.append(s);
		}
		return out.toString();
	}
}
