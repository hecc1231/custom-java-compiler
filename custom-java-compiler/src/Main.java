import lexicalscanner.LexicalScanner;
import syntax.SyntaxAnalyser;

/**
 * Created by Hersch on 2016/7/2.
 *
 */
public class Main {
    public static void main(String[] args){
        //词法分析
        new LexicalScanner();
        //语法语义分析
        new SyntaxAnalyser();
    }
}
