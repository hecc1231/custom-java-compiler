package lexicalscanner.statemachine;

import lexicalscanner.LexicalScanner;
/**
 * 字符常量状态机类
 * @author Hersch
 *
 */

public class CharStateMachine {

	public CharStateMachine() {
		initEndState();
	}
	//初始化终止状态的标记和有效状态的对应属性字
	/**
	 * 字符常量赋值,字符常量的终止状态判定数组赋值
	 */
	public void initEndState(){		
        for(int i=60;i<=66;i++){
        	LexicalScanner.endAttributeArray[i]="0x106";
            LexicalScanner.endStateArray[i] = 1;
        }
        LexicalScanner.endAttributeArray[68] = "0x106";
        LexicalScanner.endAttributeArray[71] = "0x106";
        LexicalScanner.endAttributeArray[73] = "0x106";
        LexicalScanner.endAttributeArray[76] = "0x106";
        LexicalScanner.endStateArray[68] = 1;
        LexicalScanner.endStateArray[71] = 1;
        LexicalScanner.endStateArray[73] = 1;
        LexicalScanner.endStateArray[76] = 1;
	}
    /**
     * 字符状态机的状态改变
     * @param c
     */
	public void changeState(char c){
		switch (LexicalScanner.currentState) {
		case LexicalScanner.START_STATE:
			if(c=='\''){
				LexicalScanner.currentState = 49;//'
			}
			else{
				LexicalScanner.currentState = LexicalScanner.START_STATE;
			}
			break;
		case 49:
			if(c=='\\'){
				LexicalScanner.currentState = 50;//'\
			}
			else{
				LexicalScanner.currentState = 51;//其他字符
			}
			break;
		case 50://'\
			if(c=='\''){
				LexicalScanner.currentState = 52;//'\\
			}
			else if(c=='r'){
				LexicalScanner.currentState = 53;//'\r
			}
			else if(c=='n'){
				LexicalScanner.currentState = 54;//'\n
			}
			else if(c=='f'){
				LexicalScanner.currentState = 55;//'\f
			}
			else if(c=='t'){
				LexicalScanner.currentState = 56;//'\t
			}
			else if(c=='b'){
				LexicalScanner.currentState = 57;//'\b
			}
			else if(LexicalScanner.isOctalNum(c)&&c<'4'){
				LexicalScanner.currentState = 58;//'\八进制 
			}
			else if(c=='u'){
				LexicalScanner.currentState = 59;// '/u
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 51://'x
			if(c=='\''){
				LexicalScanner.currentState = 60;//'x' end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 52:
			if(c=='\''){
				LexicalScanner.currentState = 61;// end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 53://'\r
			if(c=='\''){
				LexicalScanner.currentState = 62;//end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 54://'\n
			if(c=='\''){
				LexicalScanner.currentState = 63;//end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 55://'\f
			if(c=='\''){
				LexicalScanner.currentState = 64;//end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 56://'\t
			if(c=='\''){
				LexicalScanner.currentState = 65;//end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 57://'\b
			if(c=='\''){
				LexicalScanner.currentState = 66;//end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 58://'\d
			if(LexicalScanner.isOctalNum(c)){
				LexicalScanner.currentState = 67;//'\dd
			}
			else if(c=='\''){
				LexicalScanner.currentState = 68;//'\d' end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 59:// '/u
			if(LexicalScanner.isHexNum(c)){
				LexicalScanner.currentState = 69;// '/ux
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 67:
			if(LexicalScanner.isOctalNum(c)){
				LexicalScanner.currentState = 70;//'\ddd
			}
			else if(c=='\''){
				LexicalScanner.currentState = 71;//'\dd' end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 69:
			if(LexicalScanner.isHexNum(c)){
				LexicalScanner.currentState = 72;//'/uxx
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 70:
			if(c=='\''){
				LexicalScanner.currentState = 73;//'\ddd' end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 72:
			if(LexicalScanner.isHexNum(c)){
				LexicalScanner.currentState = 74;//'uxxx
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 74:
			if(LexicalScanner.isHexNum(c)){
				LexicalScanner.currentState = 75;//'uxxxx
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 75:
			if(c=='\''){
				LexicalScanner.currentState = 76;//'uxxxx' end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		}
	}

}
