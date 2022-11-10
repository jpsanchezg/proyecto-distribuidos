package com.tec2.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.tec2.model.ServerModel;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import zmq.ZMQ;

public class ServerControl {

    private ServerModel monitor;
    private String address, addressHealth;

    float min, max;

    public ServerControl(int tipo) {
        this.monitor = new ServerModel(tipo);
        inicializarDireccion();

        new Thread(() -> {
            requestHealthCheck();
        }).start();
    }

    public void inicializarDireccion() {
        String ruta = "res/shared/direcciones.txt";
        ArrayList<String> lista = lecturaArchivo(ruta);
        for (String s : lista) {
            String[] valores = s.split(" ");
            if (valores[0].compareTo("servidores") == 0) {
                this.address = valores[2] + ":" + valores[3];
            }
            if (valores[0].compareTo(this.monitor.getId()) == 0) {
                this.addressHealth = "*:" + valores[3];
            }
        }
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


    /**
     * Abre un hilo a partir del cual enviara mensajes de request al healthcheck en
     * intervalos de 200 milisegundos
     */
    public void requestHealthCheck() {
        try (ZContext context = new ZContext()) {
            // System.out.println("Enviando mensajes de ok");
            Socket client = context.createSocket(SocketType.REQ);
            String address = "tcp://" + this.addressHealth;

            // System.out.println("requesting to " + addressHealth);
            client.bind(address);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String msg = String.valueOf(this.monitor.getTipoMonitor());
                    client.send(msg.getBytes(ZMQ.CHARSET), 0);
                    //System.out.println("enviando: " + msg);

                    byte[] reply = client.recv();
                    System.out.println("Recibi: " + new String(reply, ZMQ.CHARSET));
                    Thread.sleep(500);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }

    /**
     * Crea y se conecta al socket de tipo suscriptor, luego obtiene la informacion
     * publicada por el mismo.
     * Ademas, determina si un valor obtenido esta o no dentro de los rangos, si no
     * esta genera una alarma y envia la informacion al sistema de calidad.
     *
     * @throws InterruptedException
     */
    public void subscribe() throws InterruptedException {
        try (ZContext context = new ZContext()) {
            System.out.println("iniciando servidor");
            Socket subcriber = context.createSocket(SocketType.
                SUB);
            Socket publicher = context.createSocket(SocketType.PUB);


            String address = "tcp://" + this.address;

            subcriber.subscribe("".getBytes());
            subcriber.connect("tcp://10.43.100.229:6666");

            publicher.bind("tcp://10.43.100.223:6666");
            Thread.sleep(1000);
            Thread.sleep(100);
            System.out.println("listening to " + address);
            String topic = monitor.getId();
            System.out.println("topic: " + monitor.getTipoMonitor());
            while (!Thread.currentThread().isInterrupted()) {
                String mensaje = subcriber.recvStr();
                System.out.println("SUB: " + mensaje);

                if (mensaje.equals("mensajedeprueba2")) {



                    String mensajederegreso = "Holamuybuenas";


                    byte[] send = mensajederegreso.getBytes(ZMQ.CHARSET);


                    String enviando = new String(send, ZMQ.CHARSET);
                    System.out.println("envindo " + enviando);
                    boolean test = publicher.send(send);
                    System.out.println("test: " + test);
                }
            }
        }
    }

}
