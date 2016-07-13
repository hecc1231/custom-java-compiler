package syntax.generator;

import utils.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hersch on 2016/7/1.
 */
public class Generator {
    public static int CHILD_NUM = 6;
    //非终结符
    public Word nonTerminalWord;
    public List<List<Word>> firstWordList;//每个候选式的First集合
    public List<Word> followWordList;//Follow集合
    public List<List<Word>> childList;//候选式集合

    public Generator() {
        childList = new ArrayList<List<Word>>();
        firstWordList = new ArrayList<List<Word>>();
        followWordList = new ArrayList<Word>();
        for(int i=0;i<CHILD_NUM;i++){
            List<Word>words = new ArrayList<Word>();
            firstWordList.add(words);
            words = new ArrayList<Word>();
            childList.add(words);
        }
    }

    /**
     * 获取产生式左端的非终结符
     * @return
     */
    public Word getNonTerminalWord(){
        return this.nonTerminalWord;
    }

    /**
     * 获取当前产生式左端节点子树的所有非终结符
     * @param nonTerminalWord
     */
    public void setNonTerminalWord(Word nonTerminalWord) {
        this.nonTerminalWord = nonTerminalWord;
    }

    /**
     * 添加First集合
     * @param i
     * @param word
     */
    public void addFirstWordList(int i, Word word) {
        firstWordList.get(i).add(word);
    }

    /**
     * 添加候选式集合
     * @param i
     * @param word
     */
    public void addChildList(int i, Word word) {
        childList.get(i).add(word);
    }

    /**
     * 添加Follow集合
     * @param word
     */
    public void addFollowWordList(Word word) {
        followWordList.add(word);
    }
}
