package ScriptGenerator;

import java.util.Random;

import IEMLInterface.IEMLLang;

public class BaseIEMLProvider {

	private Random random = new Random();
	
	public String GetLetter() {
		return IEMLLang.alphabet[random.nextInt(IEMLLang.alphabet.length)];
	}
	
	public String GetLayerMark(int layer){
		return IEMLLang.LM[layer];
	}
	
	public boolean IsCompositeValid() {
		return true;
	}
}
