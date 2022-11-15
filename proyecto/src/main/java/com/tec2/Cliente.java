package com.tec2;

import com.tec2.control.ClienteControl;

import java.util.Scanner;


/**
 * Para ejecutar el programa por favor ingrese el siguiente comando:
 * Windows y Linux: mvn  exec:java@cliente
 */


public class Cliente {

    private static ClienteControl control;

    public static void main(String args[]) throws InterruptedException {
        System.out.println("bienvenido a la tienda virtual");
        control = new ClienteControl();
        control.menu();
        Thread.sleep(500);

    }



}
