package syntax.masm;

import syntax.GeneratorReduction;
import syntax.GeneratorTable;
import syntax.SyntaxAnalyser;
import utils.AddressNode;
import utils.RegisterNode;
import utils.TreeNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Hersch on 2016/7/6.
 * 四元式生成汇编
 */
public class Masm {
    private final String MOV = "MOV";
    private final String SUB = "SUB";
    private final String JG = "JG";
    private final String JB = "JB";
    private final String ADD = "ADD";
    private final String MUL = "MUL";
    private final String DIV = "DIV";
    private final String CMP = "CMP";
    private final String INC = "INC";
    //true为大于,false为小于
    private boolean moreOrLessFlag = false;
    private int jumpT = 0;
    private final String MASM_PATH = "masm.asm";
    private FileWriter fileWriter;
    private File file;
    private List<String> masmCodeList;
    //记录跳转正确的标号
    //存放寄存器
    List<RegisterNode> registerNodes;
    //存放四元式列表
    List<GeneratorTable> generatorTables;
    //存放当前寄存器与变量的映射关系
    Map<String, RegisterNode> valueRegisterMap;
    //数据段列表
    Map<String, AddressNode> dataSegmentMap;
    private String OP = "";

    public Masm(List<GeneratorTable> list) {
        masmCodeList = new ArrayList<String>();
        dataSegmentMap = new HashMap<String, AddressNode>();
        generatorTables = list;
        valueRegisterMap = new HashMap<String, RegisterNode>();
        initFileWriter();
        initRegister();
        initDataSegments();
        generatorMasm();
        //System.out.println("end start");
        masmCodeList.add("end start");
        outputFileStream();

    }

    public void initFileWriter() {
        file = new File(MASM_PATH);
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outputFileStream() {
        try {
            for (String str : masmCodeList) {
                fileWriter.write(str + "\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initDataSegments() {
        for (GeneratorTable generatorTable : generatorTables) {
            for (int i = 2; i < generatorTable.getTable().size(); i++) {
                TreeNode treeNode = generatorTable.getTable().get(i);
                if (treeNode.getType().equals(SyntaxAnalyser.ID) ||
                        treeNode.getType().equals(GeneratorReduction.TEMP)) {
                    if (!dataSegmentMap.containsKey(treeNode.getValue())) {
                        dataSegmentMap.put(treeNode.getValue(), new AddressNode());
                    }
                }
            }
        }
        //遍历生成数据段
        //System.out.println(".386");
        masmCodeList.add(".386");
        //System.out.println(".model  flat, stdcall");
        masmCodeList.add(".model  flat, stdcall");
        //System.out.println("option  casemap:none");
        masmCodeList.add("option  casemap:none");
        //System.out.println("includelib \\masm32\\lib\\msvcrt.lib");
        masmCodeList.add("includelib \\masm32\\lib\\msvcrt.lib");
        //System.out.println(".data");
        masmCodeList.add(".data");
        for (Map.Entry<String, AddressNode> entry : dataSegmentMap.entrySet()) {
            //System.out.println(entry.getKey() + "  " + "dd" + " " + 0);
            masmCodeList.add(entry.getKey() + "  " + "dd" + " " + 0);
        }
        //System.out.println(".stack");
        masmCodeList.add(".stack");
        //System.out.println(".code");
        masmCodeList.add(".code");
        //System.out.println("start:");
        masmCodeList.add("start:");
    }

    public void initRegister() {
        registerNodes = new ArrayList<RegisterNode>();
        RegisterNode axRegister = new RegisterNode();
        axRegister.setName("EAX");
        registerNodes.add(axRegister);
        RegisterNode bxRegister = new RegisterNode();
        bxRegister.setName("EBX");
        registerNodes.add(bxRegister);
        RegisterNode cxRegister = new RegisterNode();
        cxRegister.setName("ECX");
        registerNodes.add(cxRegister);
        RegisterNode dxRegister = new RegisterNode();
        dxRegister.setName("EDX");
        registerNodes.add(dxRegister);
    }

    /**
     * 利用四元式生成汇编
     */
    public void generatorMasm() {
        for (GeneratorTable generatorTable : generatorTables) {
            List<TreeNode> treeNodeList = generatorTable.getTable();
            if (treeNodeList.get(1).getValue().equals(">") || treeNodeList.get(0).getValue().equals("<")) {
                handleMoreOrLess(generatorTable);
            } else if (treeNodeList.get(1).getValue().equals("JT") ||
                    treeNodeList.get(1).getValue().equals("JF") ||
                    treeNodeList.get(1).getValue().equals("J")) {
                handleJump(generatorTable);
            } else if (treeNodeList.get(1).getValue().equals("=")) {
                handleEqual(generatorTable);
            } else if (treeNodeList.get(1).getValue().equals("")) {
                //System.out.println(treeNodeList.get(0).getValue());
                masmCodeList.add(treeNodeList.get(0).getValue());
            } else {
                handleCalculate(generatorTable);
            }
        }
    }

    /**
     * 处理条件语句汇编翻译
     */
    public void handleMoreOrLess(GeneratorTable generatorTable) {
        List<TreeNode> treeNodeList = generatorTable.getTable();
        TreeNode firstNode = treeNodeList.get(2);
        TreeNode nextNode = treeNodeList.get(3);
        //System.out.println(treeNodeList.get(0).getValue() + ":");
        masmCodeList.add(treeNodeList.get(0).getValue() + ":");
        if (!judgeConst(firstNode) && !judgeConst(nextNode)) {
            //a>b
            allocateRegister(firstNode);
            allocateRegister(nextNode);
            // System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName()
            //       + ", " + firstNode.getValue());
            masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName()
                    + ", " + firstNode.getValue());
            System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName()
                    + ", " + nextNode.getValue());
            masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName()
                    + ", " + nextNode.getValue());
            System.out.println(CMP + " " + valueRegisterMap.get(firstNode.getValue()).getName() + ", " +
                    valueRegisterMap.get(nextNode.getValue()).getName());
            masmCodeList.add(CMP + " " + valueRegisterMap.get(firstNode.getValue()).getName() + ", " +
                    valueRegisterMap.get(nextNode.getValue()).getName());
            unLockRegister(firstNode);
            unLockRegister(nextNode);
        } else {
            if (!judgeConst(firstNode) && judgeConst((nextNode))) {
                System.out.println(CMP + " " + firstNode.getValue() + ", " +
                        nextNode.getValue());
                masmCodeList.add(CMP + " " + firstNode.getValue() + ", " +
                        nextNode.getValue());
            } else if (!judgeConst(nextNode) && judgeConst(firstNode)) {
                System.out.println(CMP + " " + nextNode.getValue() + ", " +
                        firstNode.getValue());
                masmCodeList.add(CMP + " " + nextNode.getValue() + ", " +
                        firstNode.getValue());
            } else {
                System.out.println(CMP + " " + firstNode.getValue() + ", " + nextNode.getValue());
                masmCodeList.add(CMP + " " + firstNode.getValue() + ", " + nextNode.getValue());
            }
        }
        if (treeNodeList.get(1).getValue().equals(">")) {
            moreOrLessFlag = true;
        } else {
            moreOrLessFlag = false;
        }
    }

    /**
     * 处理跳转语句汇编翻译
     */

    public void handleJump(GeneratorTable generatorTable) {
        String value = generatorTable.getTable().get(1).getValue();
        if (value.equals("JT")) {
            //大于
            if (moreOrLessFlag) {
                System.out.println("JG" + " " + generatorTable.getTable().get(4).getValue());
                masmCodeList.add("JG" + " " + generatorTable.getTable().get(4).getValue());
            } else {
                System.out.println("JB" + " " + generatorTable.getTable().get(4).getValue());
                masmCodeList.add("JB" + " " + generatorTable.getTable().get(4).getValue());
            }
        } else if (value.equals("JF")) {
            if (moreOrLessFlag) {
                System.out.println("JB" + " " + generatorTable.getTable().get(4).getValue());
                masmCodeList.add("JB" + " " + generatorTable.getTable().get(4).getValue());
            } else {
                System.out.println("JG" + " " + generatorTable.getTable().get(4).getValue());
                masmCodeList.add("JG" + " " + generatorTable.getTable().get(4).getValue());
            }
            //找到条件跳转的标号
            for (int i = 0; i < generatorTables.size(); i++) {
                if (generatorTables.get(i).getTable().get(1).getValue().equals("JF")) {
                    jumpT = i + 1;
                    break;
                }
            }
            System.out.println(generatorTables.get(jumpT).getTable().get(0).getValue() + ":");
            masmCodeList.add(generatorTables.get(jumpT).getTable().get(0).getValue() + ":");
        } else {
            System.out.println("JUMP" + " " + generatorTable.getTable().get(4).getValue());
            masmCodeList.add("JUMP" + " " + generatorTable.getTable().get(4).getValue());
        }
    }

    /**
     * 处理赋值语句汇编翻译
     * 临时变量赋值和常量赋值
     */
    public void handleEqual(GeneratorTable generatorTable) {
        List<TreeNode> treeNodeList = generatorTable.getTable();
        TreeNode firstNode = treeNodeList.get(2);
        TreeNode nextNode = treeNodeList.get(4);
        //对于常量赋值
        if (!dataSegmentMap.containsKey(firstNode.getValue())) {
            System.out.println(MOV + " " + nextNode.getValue() + ", " + firstNode.getValue());
            dataSegmentMap.get(nextNode.getValue()).setValue(Integer.parseInt(firstNode.getValue()));
            masmCodeList.add(MOV + " " + nextNode.getValue() + ", " + firstNode.getValue());
        }
        //对于临时变量赋值
        else {
            allocateRegister(firstNode);//为临时变量分配一个寄存器
            System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() + ", " +
                    firstNode.getValue());
            masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() + ", " +
                    firstNode.getValue());
            System.out.println(MOV + " " + nextNode.getValue() + ", " +
                    valueRegisterMap.get(firstNode.getValue()).getName());
            masmCodeList.add((MOV + " " + nextNode.getValue() + ", " +
                    valueRegisterMap.get(firstNode.getValue()).getName()));
            int value = dataSegmentMap.get(firstNode.getValue()).getValue();
            dataSegmentMap.get(nextNode.getValue()).setValue(value);
            //去除占用寄存器的临时变量
            unLockRegister(firstNode);
        }
    }

    /**
     * 处理运算语句汇编翻译
     * 注意乘法除法的特殊处理
     */
    public void handleCalculate(GeneratorTable generatorTable) {
        List<TreeNode> treeNodeList = generatorTable.getTable();
        TreeNode firstNode = treeNodeList.get(2);
        TreeNode nextNode = treeNodeList.get(3);
        TreeNode opNode = treeNodeList.get(1);
        TreeNode temporaryNode = treeNodeList.get(4);
        String op = opNode.getValue();

        if (firstNode.getType().equals(SyntaxAnalyser.CONST_INTEGER)) {
            if (nextNode.getType().equals(SyntaxAnalyser.CONST_INTEGER)) {
                //1+5=T3
                int result = calculate(op, Integer.parseInt(firstNode.getValue()), Integer.parseInt(nextNode.getValue()));
                dataSegmentMap.get(temporaryNode.getValue()).setValue(result);
                System.out.println(MOV + " " + temporaryNode.getValue() + ", " + result);
                masmCodeList.add(MOV + " " + temporaryNode.getValue() + ", " + result);
            } else {
                //1+a=T3
                int result = calculate(op, dataSegmentMap.get(nextNode.getValue()).getValue(),
                        Integer.parseInt(firstNode.getValue()));
                //乘法运算 MOV EAX,NUM1  MUL SRC
                if (OP.equals(MUL)) {
                    //MOV EAX,a
                    valueRegisterMap.put(nextNode.getValue(), registerNodes.get(0));
                    registerNodes.get(0).setState(1);
                    System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    //MUL 1
                    //EAX = result
                    valueRegisterMap.get(nextNode.getValue()).setValue(result);
                    System.out.println(MUL + " " + firstNode.getValue());
                    masmCodeList.add(MUL + " " + firstNode.getValue());
                    //MOV T3,EAX
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(
                            valueRegisterMap.get(nextNode.getValue()).getValue());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    unLockRegister(nextNode);
                }
                // 除法算
                else if (OP.equals(DIV)) {
                    //MOV EAX,a   DIV SRC
                    //MOV EAX,a
                    valueRegisterMap.put(nextNode.getValue(), registerNodes.get(0));
                    registerNodes.get(0).setState(1);
                    System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    //DIV 1
                    //EAX = result
                    valueRegisterMap.get(nextNode.getValue()).setValue(result);
                    System.out.println(MUL + " " + firstNode.getValue());
                    masmCodeList.add(MUL + " " + firstNode.getValue());
                    //MOV T3,EAX   EAX:EDX 商:余数
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(
                            valueRegisterMap.get(nextNode.getValue()).getValue());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    unLockRegister(nextNode);

                } else {
                    //MOV 寄存器,a
                    allocateRegister(nextNode);//将a存入寄存器
                    System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    //ADD register,1
                    valueRegisterMap.get(nextNode.getValue()).setValue(result);
                    System.out.println(OP + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(OP + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    //MOV T3,register
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                                    ", " + valueRegisterMap.get(nextNode.getValue()).getName()
                    );
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(result);
                    unLockRegister(nextNode);
                }
            }
        } else if (firstNode.getType().equals(SyntaxAnalyser.ID) || firstNode.getType().equals(GeneratorReduction.TEMP)) {
            //存在以下几种情况
            //T1+1   T1+a  a+T1 a+1
            if (nextNode.getType().equals(SyntaxAnalyser.CONST_INTEGER)) {
                //a+1=T3
                int result = calculate(op, dataSegmentMap.get(firstNode.getValue()).getValue(),
                        Integer.parseInt(nextNode.getValue()));
                if (OP.equals(MUL)) {
                    //MOV EAX,a MUL SRC
                    valueRegisterMap.put(firstNode.getValue(), registerNodes.get(0));
                    registerNodes.get(0).setState(1);
                    System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    //MUL 1
                    //EAX = a*1
                    valueRegisterMap.get(firstNode.getValue()).setValue(result);
                    System.out.println(MUL + " " + nextNode.getValue());
                    masmCodeList.add(MUL + " " + nextNode.getValue());
                    //MOV T3,EAX
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(
                            valueRegisterMap.get(firstNode.getValue()).getValue());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    //解锁寄存器
                    unLockRegister(firstNode);
                } else if (OP.equals(DIV)) {
                    //MOV EAX,a   DIV SRC
                    //MOV EAX,a
                    valueRegisterMap.put(firstNode.getValue(), registerNodes.get(0));
                    registerNodes.get(0).setState(1);
                    System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    //DIV 1
                    //EAX = a/1
                    valueRegisterMap.get(firstNode.getValue()).setValue(result);
                    System.out.println(MUL + " " + nextNode.getValue());
                    masmCodeList.add(MUL + " " + nextNode.getValue());
                    //MOV T3,EAX   EAX:EDX 商:余数
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(
                            valueRegisterMap.get(firstNode.getValue()).getValue());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    unLockRegister(firstNode);

                } else {
                    //MOV 寄存器,a
                    allocateRegister(firstNode);//将a存入寄存器
                    System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    //ADD register,1
                    valueRegisterMap.get(firstNode.getValue()).setValue(result);
                    System.out.println(OP + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    masmCodeList.add(OP + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    //MOV T3,register
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(result);
                    unLockRegister(firstNode);
                }
            } else {
                //a+b=T3
                int result = calculate(op, dataSegmentMap.get(firstNode.getValue()).getValue(),
                        dataSegmentMap.get(nextNode.getValue()).getValue());
                if (OP.equals(MUL)) {
                    //MOV EAX,a
                    //分配乘法寄存器给a
                    valueRegisterMap.put(firstNode.getValue(), registerNodes.get(0));
                    registerNodes.get(0).setState(1);
                    //分配寄存器
                    allocateRegister(nextNode);
                    System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() + ", " + nextNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() + ", " + nextNode.getValue());
                    //MUL 1
                    //EAX = a*b
                    valueRegisterMap.get(firstNode.getValue()).setValue(result);
                    System.out.println(MUL + " " + valueRegisterMap.get(nextNode.getValue()).getName());
                    masmCodeList.add((MUL + " " + valueRegisterMap.get(nextNode.getValue()).getName()));
                    //MOV T3,EAX
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(
                            valueRegisterMap.get(firstNode.getValue()).getValue());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    unLockRegister(firstNode);
                } else if (OP.equals(DIV)) {
                    //MOV EAX,a   DIV SRC
                    valueRegisterMap.put(firstNode.getValue(), registerNodes.get(0));
                    registerNodes.get(0).setState(1);
                    //分配寄存器
                    allocateRegister(nextNode);
                    System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() + ", " + nextNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() + ", " + nextNode.getValue());
                    //MUL 1
                    //EAX = a/b
                    valueRegisterMap.get(firstNode.getValue()).setValue(result);
                    System.out.println(DIV + " " + valueRegisterMap.get(nextNode.getValue()).getName());
                    masmCodeList.add(DIV + " " + valueRegisterMap.get(nextNode.getValue()).getName());
                    //MOV T3,EAX
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(
                            valueRegisterMap.get(firstNode.getValue()).getValue());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    unLockRegister(firstNode);
                } else {
                    allocateRegister(firstNode);
                    allocateRegister(nextNode);
                    System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    //MOV REG,a
                    System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    System.out.println(OP + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    masmCodeList.add(OP + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(result);
                    unLockRegister(firstNode);
                    unLockRegister(nextNode);
                }
            }
        }
    }

    /**
     * 分配空余寄存器
     *
     * @param treeNode
     */
    public void allocateRegister(TreeNode treeNode) {
        boolean flag = false;
        if (!valueRegisterMap.containsKey(treeNode.getValue())) {
            for (RegisterNode registerNode : registerNodes) {
                if (registerNode.getState() == 0) {
                    valueRegisterMap.put(treeNode.getValue(), registerNode);
                    registerNode.setState(1);
                    valueRegisterMap.get(treeNode.getValue()).setValue(dataSegmentMap.get(treeNode.getValue()).getValue());
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                System.out.println("Allocate register failed!");
            } else {
                valueRegisterMap.get(treeNode.getValue()).setValue(
                        dataSegmentMap.get(treeNode.getValue()).getValue());
            }
        }
    }

    /**
     * 解除
     *
     * @param treeNode
     */
    public void unLockRegister(TreeNode treeNode) {
        valueRegisterMap.get(treeNode.getValue()).setState(0);
        valueRegisterMap.remove(treeNode.getValue());
    }

    /**
     * 计算结果
     *
     * @param op
     * @param a
     * @param b
     * @return
     */
    public int calculate(String op, int a, int b) {
        int result = 0;
        if (op.equals("+")) {
            result = a + b;
            OP = ADD;
        } else if (op.equals("-")) {
            result = a - b;
            OP = SUB;
        } else if (op.equals("*")) {
            result = a * b;
            OP = MUL;
        } else if (op.equals("/")) {
            result = a / b;
            OP = DIV;
        }
        return result;
    }

    /**
     * 判断是否为常数
     *
     * @param treeNode
     * @return
     */
    public boolean judgeConst(TreeNode treeNode) {
        if (treeNode.getType().equals(SyntaxAnalyser.CONST_INTEGER)) {
            return true;
        }
        return false;
    }
}
