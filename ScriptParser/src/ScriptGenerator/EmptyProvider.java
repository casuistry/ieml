package ScriptGenerator;

import IEMLInterface.IEMLLang;

public class EmptyProvider extends BaseProvider {
	
	public String GetLetter() {
		return IEMLLang.GetEmpty();
	}
	
	public boolean IsCompositeValid() {
		return false;
	}
}
