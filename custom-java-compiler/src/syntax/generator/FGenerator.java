package syntax.generator;

import syntax.SyntaxAnalyser;
import utils.Word;

/**
 * Created by Hersch on 2016/7/1.
 */
public class FGenerator extends Generator{
    public FGenerator(){
        setNonTerminalWord(new Word(SyntaxAnalyser.NONT_F));
        setChildList();
    }

    /**
     * 设置候选式集合
     */
    public void setChildList(){
        addChildList(0, new Word("+"));
        addChildList(0, new Word(SyntaxAnalyser.NONT_T));
        addChildList(0, new Word(SyntaxAnalyser.NONT_F));
        addFirstWordList(0, new Word("+"));

        addChildList(1, new Word("-"));
        addChildList(1, new Word(SyntaxAnalyser.NONT_T));
        addChildList(1, new Word(SyntaxAnalyser.NONT_F));
        addFirstWordList(1, new Word("-"));

        addChildList(2, new Word(""));
        addFollowWordList(new Word(";"));
        addFollowWordList(new Word("#"));
    }
}
