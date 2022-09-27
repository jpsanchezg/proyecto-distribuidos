package com.tec2;

import java.time.Duration;
import java.time.Instant;

import com.tec2.control.ServerControl;

	/**
     * Interfaz principal del Monitor
	 * @author Daniel Santiago Morales
     * @author Diego Fernando Sanchez
     * @author Juan Pablo Vera
     *
     * Para ejecutar el programa por favor ingrese el siguiente comando:
     * Windows: mvn exec:java@monitor  -D"exec.args"="tipo periodo_falla(Opcional)"
     * Linux: mvn exec:java@monitor  -Dexec.args="tipo periodo_falla(Opcional)"
     *  tipo - 1 temperatura, 2 ph, 3 oxigeno
     *  periodo_falla - valor en milisegundos que indica en cuanto tiempo este componente presentara una falla
	 */

public class Server {

    private static ServerControl control;
    public static void main(String args[]) throws InterruptedException {

        if (checkArgs(args)) {
            if(args.length==2){
                if(Integer.parseInt(args[1])>0){
                    final int tiempo = Integer.parseInt(args[1]);
                    new Thread(() -> {
                        long start, end;
                        start= System.currentTimeMillis();
                        while (true){
                            end = System.currentTimeMillis();
                            long duracion = end-start;
                            if(duracion>=tiempo){
                                System.exit(0);
                            }
                        }
                    }).start();
                }

            }
            control = new ServerControl(Integer.parseInt(args[0]));
            control.subscribe();

        }
        System.out.println("Error en los argumentos, uso correcto: mvn exec:java@monitor -D\"exec.args\"=\"tipo\"");
        System.out.println("tipo debe ser un valor entre 1 y 3");
    }

    public static Boolean checkArgs(String args[]) {
        if (args.length !=1&&args.length !=2) {
            return false;
        }
        if (Integer.parseInt(args[0]) < 1 || Integer.parseInt(args[0]) > 3) {
            return false;
        }
        return true;
    }

}
