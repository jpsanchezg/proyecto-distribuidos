package com.tec2;
import com.tec2.control.ClienteControl;



public class Cliente {

    private static ClienteControl control;

    public static void main(String args[]) throws InterruptedException {
        if (checkArgs(args)) {
            control = new ClienteControl(Integer.parseInt(args[0]), "holacomo");
            control.publish();
        }

    }

    public static Boolean checkArgs(String args[]) {
        if (Integer.parseInt(args[0]) < 1 || Integer.parseInt(args[0]) > 3) {
            return false;
        }
        return true;
    }

}
