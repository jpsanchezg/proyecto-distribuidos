package com.tec2;

import com.tec2.control.HealthControl;

	/**
     * Interfaz principal del healthCheck
	 * @author Daniel Santiago Morales
     * @author Diego Fernando Sanchez
     * @author Juan Pablo Vera 
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
