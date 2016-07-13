package lexicalscanner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lexicalscanner.preprocess.FileFliter;
import lexicalscanner.statemachine.CharStateMachine;
import lexicalscanner.statemachine.LineJudgeMachine;
import lexicalscanner.statemachine.NumStateMachine;
import lexicalscanner.statemachine.OperStateMachine;
import lexicalscanner.statemachine.RegionStateMachine;
import lexicalscanner.statemachine.SpaceStateMachine;
import lexicalscanner.statemachine.StringStateMachine;
import lexicalscanner.statemachine.WordStateMachine;
import utils.Word;

/**
 * 
 * @author Hersch
 *
 */

public class LexicalScanner {
	public static final int BUFSIZE = 512;
	public static int bufSize = BUFSIZE;
	public static final int STATE_NUM = 512;
	public static final String inputPathName = "input.txt";
	public static final String filterPathName = "filter.txt";
	public static final String outputPathName = "output.txt";
	public static final int START_STATE= 0;//开始状态
	public static final int ILLEGAL_STATE = 99999999;//输入字符后不匹配但是可以确定上一个状态为接收状态
	public static final int ERROR_STATE = 1000001;//错误状态

	private int bufNum = 0;//缓冲区的种类

	public static ArrayList<String> keyWordList = new ArrayList<String>();//存放关键字匹配
	public static ArrayList<String> operaterList = new ArrayList<String>();//存放运算符
	public static ArrayList<Word> wordList = new ArrayList<Word>();//存放识别出来的单词
	public static ArrayList<Word> errorWordList = new ArrayList<Word>();//存放识别出来的错误单词
	public static String []endAttributeArray = new String[STATE_NUM];//有效状态属性字
	public static int []endStateArray = new int[STATE_NUM];	//终止状态

	private int lineCount = 1;//行号
	private int numberCount = 1;//总单词个数
	private int endFileFlag = 0;//文件结束标志
	private int exceedFlag = 0;//是否超出当前缓冲区
	public static int currentState = START_STATE;//开始状态
	public static int lastWordState = START_STATE;//上一个单词状态
	public static int preState = START_STATE;//上一个状态
	char[][] buffer = new char[2][BUFSIZE];//缓冲区
	File filterFile;
	File outputFile;
	FileReader filterFileReader;//读取文件
	FileWriter outputFileWriter;//写入文件

	CharStateMachine charStateMachine;//字符常量状态机
	OperStateMachine operStateMachine;//运算符状态机
	WordStateMachine wordStateMachine;//标识符状态机
	RegionStateMachine regionStateMachine;//界限符状态机
	StringStateMachine stringStateMachine;//字符串常量状态机
	SpaceStateMachine spaceStateMachine;//空格状态机
	NumStateMachine numStateMachine;//数值状态机
	LineJudgeMachine lineCountMachine;//换行状态机

	public LexicalScanner(){

		initEndState();//初始化状态数组

		new FileFliter(inputPathName);//过滤注释

		charStateMachine = new CharStateMachine();
		operStateMachine = new OperStateMachine();
		wordStateMachine = new WordStateMachine();
		stringStateMachine = new StringStateMachine();
		regionStateMachine = new RegionStateMachine();
		spaceStateMachine = new SpaceStateMachine();
		numStateMachine = new NumStateMachine();
		lineCountMachine = new LineJudgeMachine();

		filterFile = new File(filterPathName);
		if(!filterFile.exists()){
			System.out.println("filterFile not exists!\n");
		}
		outputFile = new File(outputPathName);
		if(!outputFile.exists()){
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("outputFile not exist!\n");
		}
		analyzerContent();//词法分析文本
	}
	public void initEndState(){
		for(int i=0;i<endAttributeArray.length;i++){
			endAttributeArray[i] = "";
		}
		for(int i=0;i<endStateArray.length;i++){
			endStateArray[i] = 0;
		}
	}
	public void analyzerContent(){
		int lineNumCount = 0;

		try {
			filterFileReader = new FileReader(filterFile);
			outputFileWriter = new FileWriter(outputFile);
			outputFileWriter.write("<tokens>\r\n");
			addBuffer(bufNum);//填充缓冲区
			int bp = 0;//尾指针
			int hp = 0;//头指针
			//文件未结束
			while(endFileFlag==0){
				//当前指针位置未到达BUFSIZE
				while(hp<bufSize){
					char c = buffer[bufNum][hp];
					preState = currentState;//记录上一次的状态对于ILLEGAL有用
					//changeState(c);
					transferState(c);
					if(currentState==START_STATE){
						hp++;//移动前指针
						bp = hp;//后指针赋值
					}
					else if(currentState==ILLEGAL_STATE){
						lastWordState = preState;
						currentState = START_STATE;
						String a = getString(bufNum, bp, hp-1);//hp-1回退一个字符

						//标识符+关键字+布尔常量属性值为38

						if(preState==wordStateMachine.WORD_STATE){
							int flag = 0;
							for(int i =0;i<keyWordList.size();i++){
								if(a.equals(keyWordList.get(i))){
									Word w =new Word(a);
									w.setLine(lineCount);
									w.setState(preState);
									w.setValue(a);
									w.setNumber(numberCount++);
									w.setType("0x103");
									outputWord(w);//关键字输出到文件
									lineNumCount++;//每行单词个数自加
									wordList.add(w);//添加到word单词列表中
									flag = 1;
									break;
								}
								else if(a.equals("true")||a.equals("false")){
									Word w =new Word(a);
									w.setLine(lineCount);
									w.setValue(a);
									w.setState(preState);
									w.setNumber(numberCount++);
									lineNumCount++;//每行单词个数自加
									w.setType("0x105");
									outputWord(w);//输出到文件
									wordList.add(w);//添加到word单词列表中
									System.out.println(a+" "+"<"+"0x105"+">"+"\r\n");
									flag = 1;
									break;
								}
							}
							if(flag == 0){
								Word w =new Word(a);
								w.setLine(lineCount);
								w.setValue(a);
								w.setState(preState);
								w.setNumber(numberCount++);
								lineNumCount++;//每行单词个数自加
								w.setType(endAttributeArray[preState]);//标识符
								outputWord(w);//输出到文件
								wordList.add(w);//添加到word单词列表中
								System.out.println(a+" "+"<"+endAttributeArray[preState]+">"+"\r\n");
							}
						}
						//其他单词属性
						else{
							Word w =new Word(a);
							w.setLine(lineCount);
							w.setValue(a);
							w.setState(preState);
							w.setNumber(numberCount++);
							lineNumCount++;//每行单词个数自加
							w.setType(endAttributeArray[preState]);
							outputWord(w);//输出到文件
							wordList.add(w);//添加到word单词列表中
							System.out.println(a+" "+"<"+endAttributeArray[preState]+">"+"\r\n");
						}
						bp = hp;
					}
					else if(currentState==ERROR_STATE){
						String a = getString(bufNum, bp, hp);
						Word w =new Word(a);
						w.setLine(lineCount);
						w.setValue(a);
						w.setState(ERROR_STATE);
						w.setNumber(numberCount++);
						lineNumCount++;//每行单词个数自加
						w.setType("0x101");
						outputWord(w);//输出到文件
						wordList.add(w);//添加到word单词列表中
						errorWordList.add(w);
						hp++;
						bp = hp;
						currentState = START_STATE;
					}
					else{
						if(judgeState(currentState)){
							//排除空格单词
							if(currentState!=lineCountMachine.LINE_COUNT_STATE){
								if(currentState!=spaceStateMachine.SPACE_STATE){
									lastWordState = currentState;//上一个单词状态
									String a = getString(bufNum, bp, hp);//获取单词
									Word w =new Word(a);
									w.setLine(lineCount);
									w.setValue(a);
									w.setState(currentState);
									w.setNumber(numberCount++);
									lineNumCount++;//每行单词个数自加
									w.setType(endAttributeArray[currentState]);
									outputWord(w);//输出到文件
									wordList.add(w);//添加到word单词列表中
									System.out.println(a+" "+"<"+endAttributeArray[currentState]+">"+"\r\n");
								}
							}
							//换行
							else{
								outputFileWriter.write("-------Line:"+lineCount+" Words Count: "+lineNumCount+"\r\n");
								lineCount++;
								lineNumCount=0;
							}
							hp++;
							bp = hp;
							currentState = START_STATE;
						}
						else{
							hp++;
						}
					}
				}
				//单词还未读完加入缓冲区
				if(currentState!=START_STATE&&!judgeState(currentState)){
					bufNum = 1-bufNum;
					exceedFlag = 1;
					addBuffer(bufNum);
					hp=0;
				}
				else{
					hp = 0;
					bp = 0;
					clearBuffer(bufNum);
					addBuffer(bufNum);
				}
			}
			outputFileWriter.write("</tokens>"+"\r\n");

            if(errorWordList.size()>0){
				for(Word w:errorWordList){
					outputErrorWord(w);
				}
			}
			//关闭文件读写
			filterFileReader.close();
			outputFileWriter.flush();
			outputFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	/*
	 * 选择状态机函数
	 */
	public void transferState(char c){
		//最开始需要根据输入的字符选择哪个状态机
		if(currentState == START_STATE){
			if(isWordFirst(c)){
				wordStateMachine.changeState(c);
			}//标识符状态机
			else if(isOperatorFirst(c)){
				operStateMachine.setIsPlusOrMinusFlag(isPlusOrMinus());
				operStateMachine.changeState(c);
			}//运算符状态机
			else if(isRegionFirst(c)){//
				regionStateMachine.changeState(c);
			}//界限符状态机
			else if(isSpaceFirst(c)){
				spaceStateMachine.changeState(c);
			}//空格状态机
			else if(isCharFirst(c)){
				//charState(c);
				charStateMachine.changeState(c);
			}//字符常量状态机
			else if(isStringFirst(c)){
				stringStateMachine.changeState(c);
			}
			else if(isNumFirst(c)){
				if(c=='0'){
					numStateMachine.setZeroFirstFlag(1);
				}
				numStateMachine.changeState(c);
				//numStateMachine.setZeroFirstFlag(0);//恢复初始值
			}
			else if(isLineCountFirst(c)){
				lineCountMachine.changeState(c);
			}
			//			else{
			//				currentState = START_STATE;
			//			}
		}
		else{		//分析过程当中根据当前的状态来选择哪个状态机继续执行
			if(currentState>=1&&currentState<=37){
				operStateMachine.changeState(c);
			}//运算符状态机
			else if(currentState==38){
				wordStateMachine.changeState(c);
			}//标识符状态机
			else if(currentState>=39&&currentState<=47){
				regionStateMachine.changeState(c);
			}//界限符状态机
			else if(currentState==48){
				spaceStateMachine.changeState(c);
			}//空格状态机
			else if(currentState>=49&&currentState<=76){
				//charState(c);
				charStateMachine.changeState(c);
			}//字符常量状态机
			else if(currentState>=77&&currentState<=98){
				stringStateMachine.changeState(c);
			}//字符串常量状态机
			else if(currentState>=99&&currentState<=115){
				numStateMachine.changeState(c);
			}//整型状态机
			else if(currentState==116){
				lineCountMachine.changeState(c);
			}
			else{//处于错误状态
				currentState = ERROR_STATE;
			}
		}
	}
	/**
	 * 判断上一个状态对当前状态的符号影响是正负还是加减
	 */
	public boolean isPlusOrMinus(){
		//为标识符或者字符常量或者字符串常量或者数字则代表为加减号
		if(lastWordState==38||(lastWordState>=49&&lastWordState<=115)){
			return true;
		}
		return false;
	}
	/**
	 * 输出到文件
	 * @param w
	 */
	public void outputWord(Word w){
		try {
			outputFileWriter.write("  "+"<token>\r\n");
			outputFileWriter.write("    "+"<number> "+w.getNumber()+" </number>\r\n");
			outputFileWriter.write("    "+"<value> "+w.getValue()+" </value>\r\n");
			outputFileWriter.write("    "+"<type> "+w.getType()+" </type>\r\n");
			outputFileWriter.write("    "+"<line> "+w.getLine()+" </line>\r\n");
			outputFileWriter.write("  "+"</token>\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//关键字属性
	}
	public void outputErrorWord(Word w){
		try {
			outputFileWriter.write("<Error Info>"+"\r\n");
			outputFileWriter.write("  "+"<value> "+w.getValue()+" </value>\r\n");
			outputFileWriter.write("  "+"<type> Error Word </type>"+"\r\n");
			outputFileWriter.write("  "+"<line> "+w.getLine()+" </line>\r\n");
			outputFileWriter.write("</Error Info>"+"\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 功能:检测第一个输入字符为运算符的范畴
	 */
	public static boolean isOperatorFirst(char c){
		String str = ""+c;
		for(int i =0;i<operaterList.size();i++){
			if(operaterList.get(i).equals(str)){
				return true;
			}
		}
		return false;
	}
	/*
	 * 功能:检测第一个输入的字符为换行的'\r'
	 */
	public static boolean isLineCountFirst(char c){
		if(c=='\r'){
			return true;
		}
		return false;
	}
	/*
	 * 功能:检测第一个输入的字符为数值常量的范畴
	 * 
	 */
	public static boolean isNumFirst(char c){
		if(c>='0'&&c<='9'){
			return true;
		}
		return false;
	}
	/*
	 * 功能:检测第一个输入字符为字符常量的范畴
	 */
	public static boolean isCharFirst(char c){
		if(c=='\''){
			return true;
		}
		return false;
	}
	/*
	 * 功能:检测第一个输入字符是否为字符串常量的范畴
	 */
	public static boolean isStringFirst(char c){
		if(c=='"'){
			return true;
		}
		return false;
	}
	/**
	 * 功能:检测第一个输入字符为标识符的范畴
	 */
	public static boolean isWordFirst(char c){
		if(isWord(c)||c=='$'||c=='_')
			return true;
		return false;
	}
	/*
	 * 功能:检测第一个输入字符为界限符的范畴
	 */
	public boolean isRegionFirst(char c){
		if(c=='{' || c=='}' || c==',' || c==';'
				||c=='('||c==')'||c=='['||c==']'||c=='.'){
			return true;
		}
		return false;
	}
	/*
	 * 检测第一个为空格
	 */
	public boolean isSpaceFirst(char c){
		if(c==' '){
			return true;
		}
		return false;
	}
	/*
	 * 是否为八进制数
	 */
	public static boolean isOctalNum(char c){
		if(c>='0'&&c<='7'){
			return true;
		}
		return false;
	}

	/*
	 *是否为十六进制数
	 */
	public static boolean isHexNum(char c){
		if((c>='0'&&c<='9')||(c>='A'&&c<='F')||(c>='a'&&c<='f')){
			return true;
		}
		return false;
	}
	/*
	 * 是否为单词
	 */
	public static boolean  isWord(char c){
		if((c>='a'&&c<='z')||(c>='A'&&c<='Z')){
			return true;
		}
		else return false;
	}
	/**
	 * 截断当前单词
	 * @param bufNum 缓冲区的编号
	 * @param bp 单词的起始位置
	 * @param hp 单词的结束位置
	 * @return
	 */
	public String getString(int bufNum,int bp,int hp){
		String a = "";
		//两个缓冲区相连
		if(exceedFlag==1){//需要使用两个缓冲区
			for(int i = bp;i<BUFSIZE;i++){
				a+=buffer[1-bufNum][i];//获取上个缓冲区的部分单词
			}
			for(int i=0;i<=hp;i++){
				a += buffer[bufNum][i];//连接本次缓冲区的代偿
			}
			exceedFlag = 0;
		}
		else{
			for(int i=bp;i<=hp;i++){
				a+=buffer[bufNum][i];
			}
		}
		return a;
	}
	/**
	 * 补入缓冲区
	 * @param bufNum
	 */
	public void addBuffer(int bufNum){
		try {
			int c;
			int bufCount = 0;
			while(bufCount<BUFSIZE&&(c=filterFileReader.read())!=-1){
				buffer[bufNum][bufCount++] = (char)c;
			}
			bufSize = bufCount;
			if(bufSize == 0){
				endFileFlag = 1;//判断文件是否读完
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 功能:清空bufNum对应的缓冲区
	 * @param bufNum
	 */
	public void clearBuffer(int bufNum){
		for(int i=0;i<BUFSIZE;i++){
			buffer[bufNum][i] = ' ';
		}
	}
	public boolean judgeState(int state){
		//去除了前缀的那些终态，因为那些被包含在ILLEGAL_STATE中
		if(endStateArray[state]==1)
			return true;
		return false;
	}
}
