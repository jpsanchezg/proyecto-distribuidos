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

public class ProxyControl {
    private HashMap<String, String> servidor;

    public ProxyControl() {
        servidor = new HashMap<>();
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
            if (valores[0].compareTo("proxy") == 0) {
                String address = "tcp://" + valores[2] + ":" + valores[3];
                this.servidor.put("S1", address);
            }
        }
    }

    public void proxy() {
        System.out.println("iniciando Servidor proxy");
        Runnable s1 = new ProxyThread("S1", servidor);
        new Thread(s1).start();
    }
}


/**
 * Clase auxiliar que se encarga de el levantamiento de monitores.
 */
class ProxyThread implements Runnable {

    private String tipo;
    private HashMap<String, String> servidores;

    public ProxyThread(String tipo, HashMap<String, String> servidores) {
        this.tipo = tipo;
        this.servidores = servidores;
    }

    static Socket frontend;
    static Socket backend;

    public void reply(String tipo, HashMap<String, String> servidores) throws InterruptedException {
        String argRecovery = "";
        Boolean started = false;

        if (tipo.compareTo("S1") == 0) {
            argRecovery = "1";
        }
        try (ZContext context = new ZContext()) {

            Socket server = context.createSocket(SocketType.PAIR);
            Socket publicher = context.createSocket(SocketType.PUB);
            Socket suscriber = context.createSocket(SocketType.SUB);

            System.out.println(servidores.get(tipo));
            server.bind(servidores.get(tipo));

            System.out.println("Conectando con el servidor " + "tcp://" + this.servidores);

            publicher.bind("tcp://10.43.100.229:6666");

            suscriber.subscribe("".getBytes());
            suscriber.connect("tcp://10.43.100.223:6666");

            // System.out.println("recibido del servidor pub = " + suscriber.recvStr());

            System.out.println("enviado");

            while (!Thread.currentThread().isInterrupted()) {
                //Inicialmente no se establece un timeout, en caso de que no se ejecute ningun monitor.
                byte[] reply = server.recv(0);

                server.setReceiveTimeOut(500000);
                String metricaPh = new String(reply, ZMQ.CHARSET);
                System.out.println("recibido " + metricaPh + " ");
                String[] parts = metricaPh.split("-");
                if (metricaPh.equals("traer")) {
                    System.out.println("enviando ok");
                    boolean test2 = publicher.send(metricaPh);
                    System.out.println("test del server sub = " + test2);
                    String mensaje = suscriber.recvStr();
                    System.out.println("recibido del servidor pub = " + mensaje);
                    byte[] reply2 = mensaje.getBytes(ZMQ.CHARSET);
                    boolean tests = server.send(reply2);
                    System.out.println("test server a cliente = " + tests);
                }
                if (parts[0].equals("comprar")) {
                    System.out.println("enviando ok");
                    boolean test2 = publicher.send(metricaPh);
                    System.out.println("test del server sub = " + test2);
                    String mensaje = suscriber.recvStr();
                    System.out.println("recibido del servidor pub = " + mensaje);
                    byte[] reply2 = mensaje.getBytes(ZMQ.CHARSET);
                    boolean tests = server.send(reply2);
                    System.out.println("test server a cliente = " + tests);
                }
            }
            //ZMQ.proxy(publicher, subscriber, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void run() {
        try {
            reply(tipo, servidores);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
