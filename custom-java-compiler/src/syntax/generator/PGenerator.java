package syntax.generator;

import syntax.SyntaxAnalyser;
import utils.Word;

/**
 * Created by Hersch on 2016/7/1.
 */
public class PGenerator extends Generator {
    //P->*VcP|/VcP|空
    public PGenerator() {
        setNonTerminalWord(new Word(SyntaxAnalyser.NONT_P));
        setChildList();
    }

    /**
     * 设置候选式集合
     */
    public void setChildList() {
        addChildList(0,new Word("*"));
        addChildList(0,new Word(SyntaxAnalyser.NONT_VC));
        addChildList(0,new Word(SyntaxAnalyser.NONT_P));
        addFirstWordList(0, new Word("*"));
        //将每个候选式的first集合都放在列表最后一位
        addChildList(1, new Word("/"));
        addChildList(1, new Word(SyntaxAnalyser.NONT_VC));
        addChildList(1, new Word(SyntaxAnalyser.NONT_P));
        addFirstWordList(1, new Word("/"));

        addChildList(2, new Word(""));
        addFollowWordList(new Word("#"));
        addFollowWordList(new Word(";"));
        addFollowWordList(new Word("+"));
        addFollowWordList(new Word("-"));
        //若出现候选式为空，即将FOLLOW(空)集合统一转换成first集合存储，为了方便统一
    }
}
