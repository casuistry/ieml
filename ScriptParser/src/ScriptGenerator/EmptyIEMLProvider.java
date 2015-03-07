package ScriptGenerator;

import IEMLInterface.IEMLLang;

public class EmptyIEMLProvider extends BaseIEMLProvider {
	
	public String GetLetter() {
		return IEMLLang.GetEmpty();
	}
	
	public boolean IsCompositeValid() {
		return false;
	}
}
