package com.tec2;
import com.tec2.control.ProxyControl;

	/**

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
