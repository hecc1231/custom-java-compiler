package lexicalscanner.statemachine;

import lexicalscanner.LexicalScanner;
/**
 * 空格状态机类
 * @author Hersch
 * 
 */

public class SpaceStateMachine {
    public static final int SPACE_STATE = 48;
	public SpaceStateMachine() {
		initEndState();
	}
	/**
	 * 初始化终止状态的标记和有效状态的对应属性字
	 */
	public void initEndState(){
		LexicalScanner.endAttributeArray[48] = "0x102";
		LexicalScanner.endStateArray[48] =1;
	}
	public void changeState(char c){
		switch (LexicalScanner.currentState) {
		case LexicalScanner.START_STATE:
			if(c==' '){
				LexicalScanner.currentState = SPACE_STATE;//空格 end
			}
			break;
		}
	}
}
