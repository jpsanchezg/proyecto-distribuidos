package com.tec2;

import java.time.Duration;
import java.time.Instant;

import com.tec2.control.ServerControl;


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
