package com.tec2;

import com.tec2.control.HealthControl;


public class Health {

    private static HealthControl control;

    public static void main(String args[]) throws InterruptedException {
        control = new HealthControl();
        control.replyMonitor();
    }

}
