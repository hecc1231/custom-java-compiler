package syntax.generator;

import syntax.SyntaxAnalyser;
import utils.Word;

/**
 * Created by Hersch on 2016/7/1.
 */
public class SGenerator extends Generator {
    public SGenerator() {
        setChildList();
        setNonTerminalWord(new Word(SyntaxAnalyser.NONT_S));
    }
    /**
     * 设置候选式集合
     */
    public void setChildList() {
        addChildList(0, new Word("id"));
        addChildList(0, new Word("="));
        addChildList(0, new Word(SyntaxAnalyser.NONT_Ex));
        addChildList(0, new Word(";"));
        addChildList(0, new Word(SyntaxAnalyser.NONT_S));
        addFirstWordList(0, new Word(SyntaxAnalyser.ID));
        addChildList(1, new Word(""));
        addFollowWordList(new Word("}"));
        addFollowWordList(new Word("#"));
    }

}
