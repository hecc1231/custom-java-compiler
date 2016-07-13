package syntax.generator;

import syntax.SyntaxAnalyser;
import utils.Word;

/**
 * Created by Hersch on 2016/7/1.
 */
public class TGenerator extends Generator {
    public TGenerator() {
        setNonTerminalWord(new Word(SyntaxAnalyser.NONT_T));
        setChildList();
    }
    /**
     * 设置候选式集合
     */
    public void setChildList() {
        addChildList(0, new Word(SyntaxAnalyser.NONT_VC));
        addChildList(0, new Word(SyntaxAnalyser.NONT_P));
        addFirstWordList(0, new Word(SyntaxAnalyser.ID));//标识符
        addFirstWordList(0, new Word(SyntaxAnalyser.CONST_INTEGER));//整数
    }

}
