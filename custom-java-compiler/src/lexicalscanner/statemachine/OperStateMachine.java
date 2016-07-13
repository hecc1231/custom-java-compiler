package lexicalscanner.statemachine;

import lexicalscanner.LexicalScanner;
/**
 * 运算符状态机类
 * @author Hersch
 *
 */

public class OperStateMachine {
	private boolean isPlusOrMinusFlag = false;
	public OperStateMachine() {
		initOperatorList();
		initEndState();
	}
	public void setIsPlusOrMinusFlag(boolean flag){
		this.isPlusOrMinusFlag = flag;
	}
	/**
	 * 初始化终止状态的标记和有效状态的对应属性字
	 */
	public void initEndState(){
		int[] a = new int[]{7,15,30,19,33,32,21,23,28,34,36,37};
		//0x110
		for(int i=0;i<a.length;i++){
			LexicalScanner.endAttributeArray[a[i]] = "0x110";
		}

		LexicalScanner.endAttributeArray[29] = "0x111";//?:
		LexicalScanner.endAttributeArray[27] = "0x112";//||
		LexicalScanner.endAttributeArray[22] = "0x113";//&&
		LexicalScanner.endAttributeArray[9] = "0x114";//|
		LexicalScanner.endAttributeArray[6] = "0x115";//^
		LexicalScanner.endAttributeArray[5] = "0x116";//&
		LexicalScanner.endAttributeArray[20] = "0x117";//==
		LexicalScanner.endAttributeArray[24] = "0x117";//!=
		int[] b = new int[]{8,2,26,17};
		for(int i=0;i<b.length;i++){
			LexicalScanner.endAttributeArray[b[i]] = "0x118";
		}
		LexicalScanner.endAttributeArray[25] = "0x119";//<<
		LexicalScanner.endAttributeArray[18] = "0x119";//>>
		LexicalScanner.endAttributeArray[35] = "0x119";//>>>
		LexicalScanner.endAttributeArray[1] = "0x11a";;//+
		LexicalScanner.endAttributeArray[12] = "0x11a";//-
		LexicalScanner.endAttributeArray[3] = "0x11b";//*
		LexicalScanner.endAttributeArray[14] = "0x11b";// /
		LexicalScanner.endAttributeArray[13] = "0x11b";//%
		LexicalScanner.endAttributeArray[16] = "0x11c";//++
		LexicalScanner.endAttributeArray[31] = "0x11c";//--
		//terminateMap.put(1, 0x11c);+正
		//terminateMap.put(12, 0x11c);//-负
		LexicalScanner.endAttributeArray[4] = "0x11c";//!
		LexicalScanner.endAttributeArray[10] = "0x11c";//~

		//运算符终止状态的判定数组
		LexicalScanner.endStateArray[15] =1;
		LexicalScanner.endStateArray[16] =1;
		LexicalScanner.endStateArray[17] =1;
		LexicalScanner.endStateArray[34] =1;
		LexicalScanner.endStateArray[37] =1;
		LexicalScanner.endStateArray[19] =1;
		LexicalScanner.endStateArray[20] =1;
		LexicalScanner.endStateArray[21] =1;
		LexicalScanner.endStateArray[22] =1;
		LexicalScanner.endStateArray[23] =1;
		LexicalScanner.endStateArray[24] =1;
		LexicalScanner.endStateArray[36] =1;
		LexicalScanner.endStateArray[26] =1;
		LexicalScanner.endStateArray[27] =1;
		LexicalScanner.endStateArray[28] =1;
		LexicalScanner.endStateArray[29] =1;
		LexicalScanner.endStateArray[30] =1;
		LexicalScanner.endStateArray[31] =1;
		LexicalScanner.endStateArray[32] =1;
		LexicalScanner.endStateArray[33] =1;
	}
	/**
	 * 初始化操作符列表
	 */
	public void initOperatorList(){
		LexicalScanner.operaterList.add("+");
		LexicalScanner.operaterList.add("-");
		LexicalScanner.operaterList.add("*");
		LexicalScanner.operaterList.add("<");
		LexicalScanner.operaterList.add("=");
		LexicalScanner.operaterList.add(">");
		LexicalScanner.operaterList.add("|");
		LexicalScanner.operaterList.add("~");
		LexicalScanner.operaterList.add("^");
		LexicalScanner.operaterList.add("/");
		LexicalScanner.operaterList.add("%");
		LexicalScanner.operaterList.add("&");
		LexicalScanner.operaterList.add("!");
		LexicalScanner.operaterList.add("?");
	}
	/**
	 * 更改状态机状态
	 * @param c
	 */
	public void changeState(char c){
		switch (LexicalScanner.currentState) {
		case LexicalScanner.START_STATE:
			if(c=='+'){
				LexicalScanner.currentState = 1;//+
			}
			else if(c=='>'){
				LexicalScanner.currentState = 2;//>
			}
			else if(c=='*'){
				LexicalScanner.currentState = 3;//*
			}
			else if(c=='!'){
				LexicalScanner.currentState = 4;//!
			}
			else if(c=='&'){
				LexicalScanner.currentState = 5;//&
			}
			else if(c=='^'){
				LexicalScanner.currentState = 6;//^
			}
			else if(c=='='){
				LexicalScanner.currentState = 7;//=
			}
			else if(c=='<'){
				LexicalScanner.currentState = 8;//<
			}
			else if(c=='|'){
				LexicalScanner.currentState = 9;//|
			}
			else if(c=='~'){
				LexicalScanner.currentState = 10;//~ end
			}
			else if(c=='?'){
				LexicalScanner.currentState = 11;//?
			}
			else if(c=='-'){
				LexicalScanner.currentState = 12;//-
			}
			else if(c=='%'){
				LexicalScanner.currentState = 13;//%
			}
			else if(c=='/'){
				LexicalScanner.currentState = 14;// /
			}
			else {
				LexicalScanner.currentState = LexicalScanner.START_STATE;
			}
			break;
		case 1://+
			if(c=='='){
				LexicalScanner.currentState = 15;//+= end

			}
			else if(c=='+'){
				LexicalScanner.currentState = 16;//++ end
			}
			else if(LexicalScanner.isNumFirst(c)&&!isPlusOrMinusFlag){
				if(c=='0')
					NumStateMachine.isZeroFirstFlag = 1;
				LexicalScanner.currentState = 99;//转到数字状态机
			}
			else {
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//单词终止重新状态
			}
			break;
		case 2://>
			if(c=='='){
				LexicalScanner.currentState = 17;//>= end
			}
			else if(c=='>'){
				LexicalScanner.currentState = 18;//>>
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//单词终止状态
			}
			break;
		case 3://*
			if(c=='='){
				LexicalScanner.currentState = 19;//*= end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//当前字符应该属于下一个单词
			}
			break;
		case 4://!
			if(c=='='){
				LexicalScanner.currentState = 20;//!= end
			}
			else {
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//当前输入字符后不符合运算符
			}
			break;
		case 5://&
			if(c=='='){
				LexicalScanner.currentState = 21;//&= end
			}
			else if(c=='&'){
				LexicalScanner.currentState = 22;//&& end
			}
			else {
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//& 不合适字符
			}
			break;
		case 6://^
			if(c=='='){
				LexicalScanner.currentState = 23;//^= end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//针对前一种状态为接受状态在输入c后变为拒绝状态
			}
			break;
		case 7://=
			if(c=='='){
				LexicalScanner.currentState = 24;//== end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//= '无效字符'
			}
			break;
		case 8://<
			if(c=='<'){
				LexicalScanner.currentState = 25;//<<
			}
			else if(c=='='){
				LexicalScanner.currentState = 26;//<= end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
			}
			break;
		case 9://|
			if(c=='|'){
				LexicalScanner.currentState = 27;// || end
			}
			else if(c=='='){
				LexicalScanner.currentState = 28;//|= end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
			}
			break;
		case 11://?
			if(c==':'){
				LexicalScanner.currentState = 29;//?： end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
			}
			break;
		case 12://-
			if(c=='='){
				LexicalScanner.currentState = 30;//-= end
			}
			else if(c=='-'){
				LexicalScanner.currentState = 31;//-- end
			}
			else if(LexicalScanner.isNumFirst(c)&&!isPlusOrMinusFlag){
				LexicalScanner.currentState = 99;//转到数字状态机
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
			}
			break;
		case 13://%
			if(c=='='){
				LexicalScanner.currentState = 32;//%= end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
			}
			break;
		case 14:// /
			if(c=='='){
				LexicalScanner.currentState = 33;// /= end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
			}
			break;
		case 18:// >>
			if(c=='='){
				LexicalScanner.currentState = 34;//>>= end
			}
			else if(c=='>'){
				LexicalScanner.currentState = 35;//>>> 
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
			}
			break;
		case 25:// <<
			if(c=='='){
				LexicalScanner.currentState = 36;//<<= end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
			}
			break;
		case 35://>>>
			if(c=='='){
				LexicalScanner.currentState = 37;//>>>= end
			}
			else{
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
			}
			break;
		}
	}


}
