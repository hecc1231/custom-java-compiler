package lexicalscanner.statemachine;

import lexicalscanner.LexicalScanner;
/**
 * 字符串常量状态机类
 * @author Hersch
 *
 */

public class StringStateMachine {

	public StringStateMachine() {
		initEndState();
	}
	/**
	 * 初始化终止状态的标记和有效状态的对应属性字
	 */
	public void initEndState(){
		 LexicalScanner.endAttributeArray[78] = "0x109";
	        LexicalScanner.endAttributeArray[88] = "0x109";
	        LexicalScanner.endAttributeArray[90] = "0x109";
	        LexicalScanner.endAttributeArray[93] = "0x109";
	        LexicalScanner.endAttributeArray[95] = "0x109";
	        LexicalScanner.endAttributeArray[98] = "0x109";
	        LexicalScanner.endStateArray[78] = 1;
	        LexicalScanner.endStateArray[88] = 1;
	        LexicalScanner.endStateArray[90] = 1;
	        LexicalScanner.endStateArray[93] = 1;
	        LexicalScanner.endStateArray[95] = 1;
	        LexicalScanner.endStateArray[98] = 1;
	}
	/**
	 * 更改状态机状态
	 * @param c
	 */
	public void changeState(char c){
		switch (LexicalScanner.currentState) {
		case LexicalScanner.START_STATE:
			if(c=='"'){
				LexicalScanner.currentState = 77;//"
			}
			else{
				LexicalScanner.currentState = LexicalScanner.START_STATE;
			}
			break;
		case 77://"
			if(c=='"'){
				LexicalScanner.currentState = 78;//"" end
			}
			else if(c=='\\'){
				LexicalScanner.currentState = 79;//"\
			}
			break;
		case 79://"\
			if(c=='t'){
				LexicalScanner.currentState = 80;//"\t
			}
			else if(c=='f'){
				LexicalScanner.currentState = 81;//"\f
			}
			else if(c=='b'){
				LexicalScanner.currentState= 82;//"\b
			}
			else if(c=='n'){
				LexicalScanner.currentState = 83;//"\n
			}
			else if(c=='\''){
				LexicalScanner.currentState = 84;//"\\
			}
			else if(c=='r'){
				LexicalScanner.currentState = 85;//"\r
			}
			else if(LexicalScanner.isOctalNum(c)&&c<='3'){
				LexicalScanner.currentState = 86;//"\d
			}
			else if(c=='u'){
				LexicalScanner.currentState = 87;// "/u
			}
			else LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			break;
		case 80://"\t
		case 81://"\f
		case 82://"\b
		case 83://"\n
		case 84://"\\
		case 85://"\r
			if(c=='"'){
				LexicalScanner.currentState = 88;//end
			}
			else if(c=='\\'){
				LexicalScanner.currentState = 79;//"\
			}
			break;
		case 86://"\d
			if(LexicalScanner.isOctalNum(c)){
				LexicalScanner.currentState = 89;//"\dd
			}
			else if(c=='"'){
				LexicalScanner.currentState = 90;//"\dd" end
			}
			else if(c=='\\'){
				LexicalScanner.currentState = 79;//"\
			}
			else {
				LexicalScanner.currentState = 77;//"
			}
			break;
		case 87://"/u
			if(LexicalScanner.isHexNum(c)){
				LexicalScanner.currentState = 91;//"/ux
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 89://"\dd
			if(LexicalScanner.isOctalNum(c)){
				LexicalScanner.currentState = 92;//"\ddd
			}
			else if(c=='"'){
				LexicalScanner.currentState = 93;//"\ddd" end
			}
			else if(c=='\\'){
				LexicalScanner.currentState = 79;//"\
			}
			else{
				LexicalScanner.currentState = 77;//"
			}
			break;
		case 91://"/ux
			if(LexicalScanner.isHexNum(c)){
				LexicalScanner.currentState = 94;//"/uxx
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 92://"\ddd
			if(c=='"'){
				LexicalScanner.currentState = 95;//"\ddd" end
			}
			else if(c=='\\'){
                LexicalScanner.currentState = 79;//"\ 
			}
			else{
				LexicalScanner.currentState = 77;//"
			}
			break;
		case 94://"/uxx
			if(LexicalScanner.isHexNum(c)){
				LexicalScanner.currentState = 96;//"/uxxx
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 96://"/uxxx
            if(LexicalScanner.isHexNum(c)){
            	LexicalScanner.currentState = 97;//"/uxxxx
            }
            else{
            	LexicalScanner.currentState=  LexicalScanner.ERROR_STATE;
            }
			break;
		case 97://"/uxxxx
			if(c=='"'){
				LexicalScanner.currentState = 98;//"/uxxxx" end
			}
			else if(c=='\\'){
				LexicalScanner.currentState = 79;//"\
			}
			else{
				LexicalScanner.currentState=  77;//"
			}
			break;
		}
	}

}
