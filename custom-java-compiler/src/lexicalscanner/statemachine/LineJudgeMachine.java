package lexicalscanner.statemachine;

import lexicalscanner.LexicalScanner;
/**
 * 换行状态机类
 * @author Hersch
 *
 */

public class LineJudgeMachine {
	public static final int LINE_COUNT_STATE = 117;
    public LineJudgeMachine(){
    	LexicalScanner.endStateArray[LINE_COUNT_STATE] = 1;//初始化有效状态的标识
    }
    /**
     * 换行状态机的状态改变
     * @param ch
     */
	public void changeState(char ch){
		switch (LexicalScanner.currentState) {
		case LexicalScanner.START_STATE:
			if(ch =='\r')
				LexicalScanner.currentState = 116;//\r
			break;
		case 116:
			if(ch=='\n'){
				LexicalScanner.currentState = LINE_COUNT_STATE;//\r\n end 
			}
		break;
		}
	}
}
