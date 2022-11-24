package com.tec2;

import com.tec2.control.HealthControl;

/**
 *
 * Para ejecutar el programa por favor ingrese el siguiente comando:
 * Windows y Linux: mvn exec:java@health
 */

public class Health {

    private static HealthControl control;

    public static void main(String args[]) throws InterruptedException {
        control = new HealthControl();
        control.replyMonitor();
    }

}
