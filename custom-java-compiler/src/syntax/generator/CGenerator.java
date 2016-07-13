package syntax.generator;

import syntax.SyntaxAnalyser;
import utils.Word;

/**
 * Created by Hersch on 2016/7/2.
 */
public class CGenerator extends Generator {
    public CGenerator(){
        setNonTerminalWord(new Word(SyntaxAnalyser.NONT_C));
        setChildList();
    }

    /**
     * 设置候选式集合
     */
    public void setChildList(){
        addChildList(0, new Word(">"));
        addChildList(0,new Word(SyntaxAnalyser.NONT_VC));
        addFirstWordList(0, new Word(">"));
        addChildList(1, new Word("<"));
        addChildList(1, new Word(SyntaxAnalyser.NONT_VC));
        addFirstWordList(1, new Word("<"));
    }
}
