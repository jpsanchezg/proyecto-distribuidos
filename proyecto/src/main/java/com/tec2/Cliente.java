package com.tec2;
import com.tec2.control.ClienteControl;

	/**
     * Interfaz principal del Sensor
	 * @author Daniel Santiago Morales
     * @author Diego Fernando Sanchez
     * @author Juan Pablo Vera
     *
     * Para ejecutar el programa por favor ingrese el siguiente comando:
     * Windows: mvn exec:java@sensor -D"exec.args"="tipo intervalo archivo"
     * Linux: mvn exec:java@sensor -Dexec.args="tipo intervalo archivo"
     *  tipo - 1 temperatura, 2 ph, 3 oxigeno
     *  intervalo - frecuencia en milisegundos con la que el sensor va a generar medidas
     *  archivo - nombre del archivo de configuracion desde el cual el sensor tomara los intervalos correctos
	 */

public class Cliente {

    private static ClienteControl control;

    public static void main(String args[]) throws InterruptedException {
        if (checkArgs(args)) {
            control = new ClienteControl(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2]);
            control.publish();
        }
        System.out.println("Error en los argumentos, uso correcto: mvn exec:java@sensor -D\"exec.args\"=\"tipo intervalo archivo\"");
        System.out.println("tipo debe ser un valor entre 1 y 3");
    }

    public static Boolean checkArgs(String args[]) {
        if (args.length != 3) {
            return false;
        }
        if (Integer.parseInt(args[0]) < 1 || Integer.parseInt(args[0]) > 3) {
            return false;
        }
        return true;
    }

}
