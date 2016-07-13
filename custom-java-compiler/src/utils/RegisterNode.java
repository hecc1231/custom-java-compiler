package utils;

/**
 * Created by Hersch on 2016/7/6.
 */
public class RegisterNode {
    public int useState = 0;
    public String name = "";
    public int value = 0;
    public RegisterNode(){
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public void setState(int state){
         this.useState = state;
    }
    public int getState(){
        return useState;
    }
    public void setName(String name){
         this.name = name;
    }
    public String getName(){
        return this.name;
    }
}
