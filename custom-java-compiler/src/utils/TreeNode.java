package utils;

import org.omg.CORBA.TRANSACTION_MODE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hersch on 2016/7/2.
 * 语法树结点类
 */
public class TreeNode extends Word {
    //标签变量
    public static int labels;
    //父亲节点
    public TreeNode parentNode;
    //子节点
    public List<TreeNode>chlidNodes;
    //终结符节点
    public List<TreeNode>terminalNodes;
    //四元式
    public List<String>generLists;
    //标记号
    public List<Integer>labelNumLists;
    public TreeNode(){}
    public TreeNode(String value){
        setValue(value);
        type = "";
        chlidNodes = new ArrayList<TreeNode>();
        terminalNodes = new ArrayList<TreeNode>();
        labelNumLists = new ArrayList<Integer>();
        generLists = new ArrayList<String>();
    }
    /**
     * 生成新标号
     * @return
     */
    public int newLabel(){
        ++labels;
        labelNumLists.add(labels);
        return labels;
    }

    /**
     * 获取已生成的标号的列表
     * @return
     */
    public List<Integer> getLabelNumLists() {
        return labelNumLists;
    }

    /**
     * 添加子节点
     * @param treeNode
     */
    public void addChildNode(TreeNode treeNode){
        chlidNodes.add(treeNode);
    }
    /**
     * 获取子节点列表
     * @return
     */
    public List<TreeNode> getChildNodes(){
        return chlidNodes;
    }
    /**
     * 设置翻译子程序子集
     * @return
     */
    public List<String> getGenetLists(){
        return generLists;
    }
    /**
     * 添加翻译子程序
     * @param s
     */
    public void addGenerLists(String s){
        generLists.add(s);
    }

    /**
     * 设置子树所有的终结符
     * @param treeNode
     */
    public void setTerminalWordsLists(TreeNode treeNode){
        terminalNodes.add(treeNode);
    }

    /**
     * 获取子树所有的终结符
     * @return
     */
    public List<TreeNode> getTermialWordsLists(){
        return terminalNodes;
    }

    /**
     * 设置父亲节点
     * @param node
     */
    public void setParentNode(TreeNode node){
        this.parentNode = node;
    }

    /**
     * 获取父亲节点
     * @return
     */
    public TreeNode getParentNode() {
        return parentNode;
    }
}
