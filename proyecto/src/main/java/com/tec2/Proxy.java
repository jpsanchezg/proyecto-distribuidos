package com.tec2;
import com.tec2.control.ProxyControl;

	/**
     * Interfaz principal del Proxy
	 * @author Daniel Santiago Morales
     * @author Diego Fernando Sanchez
     * @author Juan Pablo Vera 
     * 
     * Para ejecutar el programa por favor ingrese el siguiente comando:
     * Windows y Linux: mvn exec:java@proxy
	 */

public class Proxy {

    private static ProxyControl control;

    public static void main(String args[]) {
        control = new ProxyControl();
        control.proxy();        
    }

}
