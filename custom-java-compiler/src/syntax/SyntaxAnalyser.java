package syntax;

import syntax.generator.*;
import lexicalscanner.LexicalScanner;
import syntax.masm.Masm;
import utils.TreeNode;
import utils.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 语法分析主类
 */
public class SyntaxAnalyser {
    //终结符名称
    public static final String NONT_S = "S";
    public static final String NONT_SW = "Sw";
    public static final String NONT_ER ="Er";
    public static final String NONT_VC = "Vc";
    public static final String NONT_T = "T";
    public static final String NONT_F = "F";
    public static final String NONT_Ex = "Ex";
    public static final String NONT_P = "P";
    public static final String NONT_C = "C";
    //标识符0x104   常数0x107
    public static final String CONST_INTEGER = "const";
    public static final String ID = "id";
    public static final String OTHER = "other";
    //产生式集合
    public static List<Generator> generators;
    //语法分析栈
    public Stack<TreeNode> analyserStack;
    //余留输入字符串
    public List<Word> remainList;
    //根节点
    public TreeNode rootNode;
    //当前语法节点
    public TreeNode currentNode;

    public SyntaxAnalyser() {
        //设置产生式集合
        setGenerators();
        analyserStack = getInitAnalyserStack();
        remainList = getRemainList(LexicalScanner.wordList);
        run();
        //自下而上获得综合属性
        getAttrFromChild(rootNode);
        //生成语义翻译子程序
        new SyntaxTranslation(rootNode);
        //生成四元式
        GeneratorReduction generatorReduction = new GeneratorReduction();
        generatorReduction.searchGenOfSw(rootNode);
        generatorReduction.outputGenerator();
        //生成汇编
        new Masm(generatorReduction.getGeneratorTable());
    }

    /**
     * 遍历语法树获取综合属性
     * @param treeNode
     */
    public void getAttrFromChild(TreeNode treeNode){
        if(treeNode.getChildNodes().size()==0){
            treeNode.setTerminalWordsLists(treeNode);
            return;
        }
        for(TreeNode node:treeNode.getChildNodes()){
            getAttrFromChild(node);
            for(TreeNode terminalNode:node.getTermialWordsLists()){
                if(!terminalNode.value.equals("")) {
                    treeNode.setTerminalWordsLists(terminalNode);
                }
            }
        }
    }

    /**
     * 语法分析主函数
     */
    public void run() {
        rootNode = analyserStack.lastElement();
        while (analyserStack.size() > 1) {
            String aStr = analyserStack.lastElement().getValue();
            String rStr = remainList.get(0).getValue();
            String type = remainList.get(0).getType();
            currentNode = analyserStack.lastElement();
            //First集合为常数或者是变量名匹配种类即可
            if (aStr.equals(type) || aStr.equals(rStr)) {
                //将非终结符的值赋给对应语法树结点的值
                currentNode.setValue(rStr);
                currentNode.setType(remainList.get(0).getType());
                currentNode.setNumber(remainList.get(0).getNumber());
                currentNode.setLine(remainList.get(0).getLine());
                analyserStack.pop();
                remainList.remove(0);
            } else {
                reductGenerator();//推导
            }
        }
        return;
    }

    /**
     * 进行余留栈和分析栈的推导以及移进移出
     */
    public void reductGenerator() {
        int generatorIndex = searchGenerator();
        searchFirstWord(generatorIndex);
    }

    /**
     * 查找当前分析栈顶端非终结符对应的产生式的索引值
     *
     * @return
     */
    public int searchGenerator() {
        int index = 0;
        for (int i = 0; i < generators.size(); i++) {
            Word currentNonTerminalWord = analyserStack.lastElement();
            if (generators.get(i).getNonTerminalWord().getValue().equals(currentNonTerminalWord.getValue())) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 将推导产生式移进分析栈
     *
     * @param words
     */
    public void intoAnalyserStack(List<Word> words) {
        //产生式建立子树
        for (int i = 0; i < words.size(); i++) {
            Word word = words.get(i);
            TreeNode treeNode = new TreeNode(word.getValue());
            treeNode.setParentNode(currentNode);
            currentNode.addChildNode(treeNode);
        }
        //产生式入栈
        for (int i = currentNode.getChildNodes().size() - 1; i >= 0; i--) {
            analyserStack.add(currentNode.getChildNodes().get(i));
        }
    }

    /**
     * 搜索当前匹配终结符所在的候选式索引
     *
     * @param index
     */
    public void searchFirstWord(int index) {
        //先找到对应非终结符的产生式集合
        int num = 0;
        Generator generator = generators.get(index);
        for (int i = 0; i < generator.firstWordList.size(); i++) {
            List<Word> list = generator.firstWordList.get(i);
            for (int j = 0; j < list.size(); j++) {
                //对于数字和标识符需要以种类判断
                String value = remainList.get(0).getValue();
                String type = remainList.get(0).getType();
                if (value.equals(list.get(j).getValue()) || type.equals(list.get(j).getValue())) {
                    //先移除分析栈的顶部的非终结符
                    analyserStack.pop();
                    intoAnalyserStack(generator.childList.get(i));
                    return;
                }
            }
        }
        //候选式存在空集查看follow集合
        for (Word word : generator.followWordList) {
            if (word.getValue().equals(remainList.get(0).getType()) ||
                    word.getValue().equals(remainList.get(0).getValue())) {
                analyserStack.pop();
                TreeNode treeNode = new TreeNode("");
                currentNode.addChildNode(treeNode);
                return;
            }
        }
    }


    /**
     * 初始化预留栈
     *
     * @param list
     * @return
     */
    public List<Word> getRemainList(List<Word> list) {
        List<Word>wordList = new ArrayList<Word>();
        for (Word word :list) {
            transferType(word);
            wordList.add(word);
        }
        return wordList;
    }

    /**
     * 初始化分析栈
     * @return
     */
    public Stack<TreeNode> getInitAnalyserStack() {
        Stack<TreeNode> stack = new Stack<TreeNode>();
        stack.push(new TreeNode("#"));
        stack.push(new TreeNode(NONT_SW));
        return stack;
    }

    /**
     * 初始化产生式集合
     */
    public void setGenerators() {
        if (generators == null) {
            generators = new ArrayList<Generator>();
        }
        generators.add(new SwGenerator());
        generators.add(new SGenerator());
        generators.add(new ErGenerator());
        generators.add(new CGenerator());
        generators.add(new VcGeneratr());
        generators.add(new ExGenerator());
        generators.add(new TGenerator());
        generators.add(new FGenerator());
        generators.add(new PGenerator());
    }

    /**
     * 将词法分析列表的单词属性
     * @param word
     */
    public void transferType(Word word) {
        if (word.getType().equals("0x104")) {
            word.setType(ID);
        } else if (word.getType().equals("0x107")) {
            word.setType(CONST_INTEGER);
        } else {
            word.setType(OTHER);
        }
    }
}
