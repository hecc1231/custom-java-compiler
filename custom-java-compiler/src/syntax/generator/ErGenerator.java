package syntax.generator;

import syntax.SyntaxAnalyser;
import utils.Word;

/**
 * Created by Hersch on 2016/7/1.
 */
public class ErGenerator extends Generator {
    //Er->Vc<Vc|Vc>Vc
    public ErGenerator() {
        setNonTerminalWord(new Word(SyntaxAnalyser.NONT_ER));
        setChildList();
    }

    /**
     * 设置候选式集合
     */
    public void setChildList() {
        addChildList(0, new Word(SyntaxAnalyser.NONT_VC));
        addChildList(0, new Word(SyntaxAnalyser.NONT_C));
        addFirstWordList(0, new Word(SyntaxAnalyser.ID));
        addFirstWordList(0, new Word(SyntaxAnalyser.CONST_INTEGER));
    }
}
