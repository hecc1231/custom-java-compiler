package syntax;

import utils.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hersch on 2016/7/4.
 * 四元式码表类( , , , )
 */
public class GeneratorTable {
    public List<TreeNode> lists;
    public GeneratorTable(){
        lists = new ArrayList<TreeNode>();
    }
    public void addItem(int i,TreeNode s){
        lists.add(i,s);
    }
    public List<TreeNode> getTable(){
        return lists;
    }
}
