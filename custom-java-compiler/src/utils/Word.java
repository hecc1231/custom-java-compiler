package utils;

import syntax.SyntaxAnalyser;

/**
 * 属性字符流类
 */
public class Word {
	//所在行数
    public int line;
	//属性字的种类
    public String type;
	//属性字所在行的顺序值
    public int number;
    //属性字的值
    public String value;
	//属性字代表的状态值
	public int state;
	public Word(){}
	public Word(String value){
		if(value!= SyntaxAnalyser.ID&&value!= SyntaxAnalyser.CONST_INTEGER){
			type = SyntaxAnalyser.OTHER;
		}
		else{
			type = value;
		}
		this.value = value;
	}
	/**
	 * 获取属性字的状态
	 * @return
	 */
	public int getState() {
		return state;
	}
	/**
	 * 设置属性字的状态
	 * @param state
	 */
	public void setState(int state) {
		this.state = state;
	}
	/**
	 * 获取属性字所在的行数
	 * @return
	 */
	public int getLine() {
		return line;
	}
	/**
	 * 设置属性字的行号
	 * @param line
	 */
	public void setLine(int line) {
		this.line = line;
	}
	/**
	 * 获取属性字的种类
	 * @return
	 */
	public String getType() {
		return type;
	}
	/**
	 * 获取属性字的种类
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 获取属性字在其所在行的顺序索引值
	 * @return
	 */
	public int getNumber() {
		return number;
	}
	/**
	 * 设置属性字在其所在行的顺序索引值
	 * @param number
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	/**
	 * 获取属性字的值
	 * @return
	 */
	public String getValue() {
		return value;
	}
	/**
	 * 设置属性字的值
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
