package lexicalscanner.statemachine;

import lexicalscanner.LexicalScanner;
/**
 * 界限符状态机
 * @author Hersch
 *
 */

public class RegionStateMachine {

	public RegionStateMachine() {
		initEndState();
	}
	/**
	 * 初始化终止状态的标记和有效状态的对应属性字
	 */
	public void initEndState(){
		LexicalScanner.endAttributeArray[39] = "0x121";//{
		LexicalScanner.endAttributeArray[40] = "0x121";//}
		for(int i=41;i<=45;i++){
			LexicalScanner.endAttributeArray[i] = "0x11d";//[] () .
		}
		LexicalScanner.endAttributeArray[46] = "0x122";//;
		LexicalScanner.endAttributeArray[47] = "0x120";//,

		for(int i=39;i<=47;i++){
			LexicalScanner.endStateArray[i] = 1;
		}
	}
	/**
	 * 更改状态机状态
	 * @param c
	 */
	public void changeState(char c){
		switch (LexicalScanner.currentState) {
		case LexicalScanner.START_STATE:
			if(c=='{')
				LexicalScanner.currentState = 39;//{ end
			else if(c=='}'){
				LexicalScanner.currentState = 40;//} end
			}
			else if(c=='['){
				LexicalScanner.currentState = 41;//[ end
			}
			else if(c==']'){
				LexicalScanner.currentState = 42;//] end
			}
			else if(c=='('){
				LexicalScanner.currentState = 43;//( end
			}
			else if(c==')'){
				LexicalScanner.currentState = 44;//) end
			}
			else if(c=='.'){
				LexicalScanner.currentState = 45;//. end
			}
			else if(c==';'){
				LexicalScanner.currentState = 46;//; end	
			}
			else if(c==','){
				LexicalScanner.currentState = 47;//, end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.START_STATE;
			}
		}
	}

}
