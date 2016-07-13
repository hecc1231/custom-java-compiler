package lexicalscanner.statemachine;
import lexicalscanner.LexicalScanner;

/**
 * 整型，实型常量状态机类
 *
 * @author Hersch
 */

public class NumStateMachine {
    public static int isZeroFirstFlag = 0;

    public NumStateMachine() {
        initEndState();
    }

    /**
     * 设置第一个数字为0的标记，针对0x的例子
     *
     * @param flag
     */
    public void setZeroFirstFlag(int flag) {
        this.isZeroFirstFlag = flag;
    }

    /**
     * 初始化终止状态的标记和有效状态的对应属性字
     */
    public void initEndState() {
        LexicalScanner.endAttributeArray[99] = "0x107";//整型
        LexicalScanner.endAttributeArray[100] = "0x107";
        LexicalScanner.endAttributeArray[102] = "0x107";
        LexicalScanner.endAttributeArray[104] = "0x108";
        LexicalScanner.endAttributeArray[106] = "0x108";//浮点型
        LexicalScanner.endAttributeArray[107] = "0x108";
        LexicalScanner.endAttributeArray[109] = "0x108";
        LexicalScanner.endAttributeArray[110] = "0x108";
        LexicalScanner.endAttributeArray[111] = "0x107";
        LexicalScanner.endAttributeArray[114] = "0x108";
        LexicalScanner.endAttributeArray[115] = "0x107";

        LexicalScanner.endStateArray[100] = 1;
        LexicalScanner.endStateArray[107] = 1;
        LexicalScanner.endStateArray[110] = 1;
        LexicalScanner.endStateArray[114] = 1;
    }

    /**
     * 更改状态机状态
     *
     * @param c
     */
    public void changeState(char c) {
        switch (LexicalScanner.currentState) {
            case LexicalScanner.START_STATE:
                if (LexicalScanner.isNumFirst(c)) {
                    LexicalScanner.currentState = 99;
                }
                break;
            case 99:
                if (c == 'L') {
                    LexicalScanner.currentState = 100;//end
                } else if ((c == 'X' || c == 'x') && isZeroFirstFlag == 1) {//flag=1代表第一个数字为0，这样才能构成0x
                    LexicalScanner.currentState = 101;
                    isZeroFirstFlag = 0;
                } else if (c == '.') {
                    LexicalScanner.currentState = 103;
                } else if (c == 'e' || c == 'E') {
                    LexicalScanner.currentState = 108;
                } else if (c == 'F' || c == 'f') {
                    LexicalScanner.currentState = 110;//end
                } else if (LexicalScanner.isNumFirst(c)) {
                    if (isZeroFirstFlag == 1) {
                        LexicalScanner.currentState = 115;//01
                    } else {
                        LexicalScanner.currentState = 111;//12
                    }
                } else {
                    LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//end
                }
                break;
            case 101:
                if (LexicalScanner.isNumFirst(c)) {
                    LexicalScanner.currentState = 102;
                } else {
                    LexicalScanner.currentState = LexicalScanner.ERROR_STATE;//end
                }
                break;
            case 102:
                if (LexicalScanner.isNumFirst(c)) {
                    LexicalScanner.currentState = 102;
                }
//			else if(c==' '||c==';'||LexicalScanner.isOperatorFirst(c)){
//				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//end
//			}
                else {
                    LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//end
                }
                break;
            case 103:
                if (LexicalScanner.isNumFirst(c)) {
                    LexicalScanner.currentState = 104;
                }
                else if(c=='e'){
                    LexicalScanner.currentState = 105;//2.e
                }
                else {
                    LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
                }
                break;
            case 104://1.23
                if (LexicalScanner.isNumFirst(c)) {
                    LexicalScanner.currentState = 104;
                } else if (c == 'e' || c == 'E') {
                    LexicalScanner.currentState = 105;
                } else if (c == 'f' || c == 'F') {
                    LexicalScanner.currentState = 107;//end
                }
//			else if(c==' '||c==';'||LexicalScanner.isOperatorFirst(c)){
//				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
//			}
                else {
                    LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
                }
//			else{
//				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
//			}
                break;
            case 105://1.23e
                if (LexicalScanner.isNumFirst(c)) {
                    LexicalScanner.currentState = 106;
                } else if (c == '-'||c=='+') {
                    LexicalScanner.currentState = 112;
                } else {
                    LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
                }
                break;
            case 106:
                if (LexicalScanner.isNumFirst(c)) {
                    LexicalScanner.currentState = 106;
                } else if (c == 'f' || c == 'F') {
                    LexicalScanner.currentState = 114;//终止状态
                }
//			else if(c==' '||c==';'||LexicalScanner.isOperatorFirst(c)){
//				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//end
//			}
                else {
                    LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
                }
//			else{
//				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
//			}
                break;
            case 108://3e
                if (LexicalScanner.isNumFirst(c)) {
                    LexicalScanner.currentState = 109;
                } else if (c == '-') {
                    LexicalScanner.currentState = 113;
                } else {
                    LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
                }
                break;
            case 109://4e5
                if (LexicalScanner.isNumFirst(c)) {
                    LexicalScanner.currentState = 109;
                } else {
                    LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
                }
//			else if(c==' '||c==';'||LexicalScanner.isOperatorFirst(c)){
//				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//end
//			}
//			else{
//				LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
//			}
                break;
            case 111://20
                if (LexicalScanner.isNumFirst(c)) {
                    LexicalScanner.currentState = 111;
                } else if (c == '.') {
                    LexicalScanner.currentState = 103;
                } else if (c == 'e' || c == 'E') {
                    LexicalScanner.currentState = 108;
                } else if (c == 'f' || c == 'F') {
                    LexicalScanner.currentState = 110;
                } else if (c == 'L' || c == 'l') {
                    LexicalScanner.currentState = 100;
                } else {
                    LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;
                }
                break;
            case 112://1.23e-
                if (LexicalScanner.isNumFirst(c)) {
                    LexicalScanner.currentState = 106;
                } else {
                    LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
                }
                break;
            case 113://3e-
                if (LexicalScanner.isNumFirst(c)) {
                    LexicalScanner.currentState = 109;
                } else {
                    LexicalScanner.currentState = LexicalScanner.ERROR_STATE;
                }
                break;
            case 115://01
                if (LexicalScanner.isOctalNum(c)) {
                   LexicalScanner.currentState = 115;//012
                }
                else{
                    LexicalScanner.currentState=LexicalScanner.ILLEGAL_STATE;
                }
                break;
        }

    }
}
