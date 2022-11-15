package com.tec2;

import com.tec2.control.HealthControl;

//mvn exec:java@health
public class Health {

    private static HealthControl control;

    public static void main(String args[]) throws InterruptedException {
        control = new HealthControl();
        control.replyMonitor();
    }

}
