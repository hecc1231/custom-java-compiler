package syntax.generator;

import syntax.SyntaxAnalyser;
import utils.Word;

/**
 * Created by Hersch on 2016/7/1.
 */
public class VcGeneratr extends Generator {
    public VcGeneratr(){
        setNonTerminalWord(new Word(SyntaxAnalyser.NONT_VC));
        setChildList();
    }

    /**
     * 设置候选集合
     */
    public void setChildList(){
        addChildList(0, new Word(SyntaxAnalyser.ID));
        addFirstWordList(0, new Word(SyntaxAnalyser.ID));
        addChildList(1, new Word(SyntaxAnalyser.CONST_INTEGER));
        addFirstWordList(1, new Word(SyntaxAnalyser.CONST_INTEGER));
    }
}
