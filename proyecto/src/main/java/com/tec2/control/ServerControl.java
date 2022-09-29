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
     * Verifica que un valor dado este dentro del intervalo de valores aceptables
     *
     * @param valor cantidad a validar
     * @return true si esta dentro del intervalo establecido, false de otra manera
     */
    public Boolean checkValor(float valor) {
        if (valor < min || valor > max) {
            return false;
        }
        return true;
    }

    public void escribirArchivo(String msg) {
        try {
            FileWriter lector = new FileWriter("res/monitores/registro.txt", true);
            lector.write(msg + "\n");
            lector.close();
        } catch (IOException e) {
            System.out.println("Error en la escritura del fichero.");
            e.printStackTrace();
        }

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
            Socket subcriber = context.createSocket(SocketType.SUB);
            String address = "tcp://" + this.address;
            subcriber.connect(address);

            System.out.println("listening to " + address);
            String topic = monitor.getId();
            System.out.println("topic: " + monitor.getTipoMonitor());
            //subcriber.subscribe(topic.getBytes(ZMQ.CHARSET));
            byte[] repl = subcriber.recv(0);
            System.out.println("Received " + ": [" + new String(repl, ZMQ.CHARSET) + "]");
            //System.out.println("SUB: " + subcriber.recvStr());
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("SUB: " + subcriber.recvStr());
            }
        }
    }

}
