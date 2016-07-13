package syntax;

import utils.TreeNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Hersch on 2016/7/3.
 * 语义子程序翻译
 */
public class SyntaxTranslation {
    //翻译子程序的常量
    private final String IF = "if";
    private final String GOTO = "goto";
    private final String LABEL = "L";
    private final String COLON = ":";
    public static final String TRANSLATION_PATH = "translation.txt";

    public SyntaxTranslation(TreeNode treeNode) {
        sWAction(treeNode);
        File outputFile = new File(TRANSLATION_PATH);
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(outputFile);
            fileWriter.write("Translation Code"+"\n");
            for (String string : treeNode.getGenetLists()) {
                fileWriter.write(string);
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * |___S.next=B.false__|
     * |_______B.true______|
     * |_______begin_______|labelList
     *
     * @param treeNode
     */
    public void sWAction(TreeNode treeNode) {
        int begin = treeNode.newLabel();
        int trueLabel = treeNode.newLabel();//B.true
        int falseLabel = treeNode.newLabel();
        //label begin
        treeNode.addGenerLists(LABEL + begin + ":");
        //B.code
        eRAction(treeNode.getChildNodes().get(2));
        copyGenerList(treeNode, treeNode.getChildNodes().get(2));
        //label 1
        treeNode.addGenerLists("\n" + LABEL + trueLabel + COLON);
        //S.code
        sAction(treeNode.getChildNodes().get(5));
        copyGenerList(treeNode, treeNode.getChildNodes().get(5));
        //goto begin
        treeNode.addGenerLists("\n" + GOTO + " " + LABEL + begin);
        //S.next
        treeNode.addGenerLists("\n" + LABEL + treeNode.getLabelNumLists().get(2) + COLON + "\n");
    }

    /**
     * 执行Er->VcC的动作
     *
     * @param treeNode
     */
    public void eRAction(TreeNode treeNode) {
        String str = "";
        for (int i = 0; i < treeNode.getTermialWordsLists().size(); i++) {
            str += treeNode.getTermialWordsLists().get(i).getValue();
        }
        treeNode.addGenerLists(IF + " " + str + " " + GOTO + " " + LABEL +
                treeNode.getParentNode().getLabelNumLists().get(1));
        treeNode.addGenerLists("\n" + GOTO + " " + LABEL + treeNode.getParentNode().getLabelNumLists().get(2));
    }

    /**
     * S->id=Ex;S
     *
     * @param treeNode
     */
    public void sAction(TreeNode treeNode) {
        if (treeNode.getChildNodes().size() == 5) {
            treeNode.addGenerLists("\n");
            int label = treeNode.newLabel();
            treeNode.addGenerLists(treeNode.getChildNodes().get(0).getValue() + "=");
            eXAction(treeNode.getChildNodes().get(2));
            copyGenerList(treeNode, treeNode.getChildNodes().get(2));
            sAction(treeNode.getChildNodes().get(4));
            copyGenerList(treeNode, treeNode.getChildNodes().get(4));
        }
    }

    /**
     * Ex->TF
     *
     * @param treeNode
     */
    public void eXAction(TreeNode treeNode) {
        String s = "";
        for (TreeNode childNode : treeNode.getTermialWordsLists()) {
            s += childNode.getValue();
        }
        treeNode.addGenerLists(s);
    }

    /**
     * 传递四元式
     *
     * @param fatherNode
     * @param childNode
     */
    public void copyGenerList(TreeNode fatherNode, TreeNode childNode) {
        if (fatherNode.getGenetLists() != null && childNode.getGenetLists() != null) {
            for (String str : childNode.getGenetLists()) {
                fatherNode.getGenetLists().add(str);
            }
        } else {
            System.out.println("Generlists is null!");
        }
    }
}
