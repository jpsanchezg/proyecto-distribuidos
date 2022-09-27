package com.tec2.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import zmq.ZMQ;

public class HealthControl {
    private HashMap<String,String> monitores;

    public HealthControl() {
        monitores = new HashMap<>();
        inicializarDireccion();
    }

    public ArrayList<String> lecturaArchivo(String ruta) {

        ArrayList<String> lista = new ArrayList<>();
        try {
            File archivo = new File(ruta);
            Scanner lector = new Scanner(archivo);
            while (lector.hasNextLine()) {
                String data = lector.nextLine();
                lista.add(data);
            }
            lector.close();
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado.");
            e.printStackTrace();
        }
        return lista;
    }

    public void inicializarDireccion() {
        String ruta = "res/shared/direcciones.txt";
        ArrayList<String> lista = lecturaArchivo(ruta);
        for (String s : lista) {

            String[] valores = s.split(" ");
            if (valores[0].compareTo("S1") == 0) {
                String address = "tcp://"+ valores[2]+":"+valores[3];
                this.monitores.put("s1",address);
            }
            if (valores[0].compareTo("S2") == 0) {
                String address = "tcp://"+ valores[2]+":"+valores[3];
                this.monitores.put("s2", address);
            }
            if (valores[0].compareTo("S3") == 0) {
                String address = "tcp://"+ valores[2]+":"+valores[3];
                this.monitores.put("s3", address);
            }
        }
    }

    public void replyMonitor(){
        System.out.println("iniciando Servidor de salud");
        Runnable s1 = new HealthThread("s1", monitores);
        Runnable s2 = new HealthThread("s2", monitores);
        Runnable s3 = new HealthThread("s3", monitores);
        new Thread(s1).start();
        new Thread(s2).start();
        new Thread(s3).start();
    }
}


/**
 * Clase auxiliar que se encarga de el levantamiento de monitores.
 */
 class HealthThread implements Runnable{

    private String tipo;
    private HashMap<String,String> monitores;
    public HealthThread(String tipo, HashMap<String,String> monitores) {
        this.tipo=tipo;
        this.monitores =monitores;
    }
    /**
     * Metodo principal, inicialmente se conecta a un socket particular dependiendo el tipo de monitor.
     * Luego envia mensajes de respuesta a partir de los requests de los monitores, si uno de ellos tarda mas de un segundo en enviar respuesta, asume que el proceso fallo y levanta un monitor replica.
     * @param tipo
     * @param monitores
     * @throws InterruptedException
     */
    public void reply(String tipo, HashMap<String,String> monitores) throws InterruptedException {
        String argRecovery="";
        Boolean started = false;
        if(tipo.compareTo("temp")==0){
            argRecovery = "1";
        }
        else if(tipo.compareTo("ph")==0){
            argRecovery = "2";
        }
        else if(tipo.compareTo("oxi")==0){
            argRecovery = "3";
        }
        try (ZContext context = new ZContext()) {

            Socket server = context.createSocket(SocketType.REP);
            System.out.println(monitores.get(tipo));
            server.connect(monitores.get(tipo));

            while (!Thread.currentThread().isInterrupted()) {
                //Inicialmente no se establece un timeout, en caso de que no se ejecute ningun monitor.
                byte[] reply = server.recv(0);
                server.setReceiveTimeOut(1000);


                if (reply == null) {
                    Process recovery = null;
                    System.out.println("se rompio el monitor de "+tipo+",creando uno nuevo");
                    final String threadArg = argRecovery;
                    new Thread(() -> {
                        recoverMonitor(threadArg);
                    }).start();
                    server.setReceiveTimeOut(-1);
                }
                else {
                    String metricaPh = new String(reply, ZMQ.CHARSET);
                    System.out.println("recibido " + metricaPh+" ");
                    System.out.println("enviando ok");
                    boolean test = server.send("ok");
                    System.out.println("test = "+test);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * Metodo que levanta un monitor replica
     * @param arg cadena que contiene los argumentos necesarios para levantar un monitor
     */
    public void recoverMonitor(String arg){
        Process recovery = null;
        try{
            recovery = Runtime.getRuntime().exec("cmd.exe /c start mvn exec:java@monitor -D\"exec.args\"=\""+arg+"\"");
            Thread.sleep(20000);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            reply(tipo,monitores);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
