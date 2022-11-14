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
        Thread.sleep(500);
        boolean finalizo = false;
        int opcion = 0;
        while (!finalizo) {
            System.out.println("escoge una de las siguientes opciones");
            System.out.println("1. traer productos de la tienda");
            System.out.println("2. agregar al carrito");
            System.out.println("3. Comprar productos");
            System.out.println("4. salir");
            Scanner  leer = new Scanner(System.in);
            opcion= leer.nextInt();
            if (opcion == 1) {
                control = new ClienteControl(1);
                control.publish();
            }
            if (opcion == 2) {
                control = new ClienteControl(2);
                control.publish();
            }
            if (opcion == 3) {
                control = new ClienteControl(3);
                control.publish();
            }
            if (opcion == 4) {
                finalizo = true;
            }

        }




    }

    public static Boolean checkArgs(String args[]) {
        if (Integer.parseInt(args[0]) < 1 || Integer.parseInt(args[0]) > 3) {
            return false;
        }
        return true;
    }

}
