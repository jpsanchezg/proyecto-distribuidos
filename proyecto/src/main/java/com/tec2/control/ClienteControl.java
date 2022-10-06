package com.tec2.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import com.tec2.model.ClienteModel;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

public class ClienteControl {

    private float min, max;
    private float correcto, incorrecto, errores;
    private String address;

    private ClienteModel cliente;

    public ClienteControl() {
    }

    public ClienteControl(int tipo, String ruta) throws InterruptedException {
        inicializarDireccion();
        // Inicializamos parametros del cliente
        this.cliente = new ClienteModel(tipo, ruta);
    }

    /**
     * Inicializacion de las direcciones ip a partir del archivo de direcciones
     */
    public void inicializarDireccion() {
        String ruta = "res/shared/direcciones.txt";
        ArrayList<String> lista = lecturaArchivo(ruta);
        for (String s : lista) {
            String[] valores = s.split(" ");
            if (valores[0].compareTo("clientes") == 0) {
                this.address = valores[2] + ":" + valores[3];
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


    public void publish() throws InterruptedException {

        try (ZContext context = new ZContext()) {
            boolean inicializar = false;
            System.out.println("iniciando cliente");
            //System.out.println("Enviando mensajes de ok");
            Socket client = context.createSocket(SocketType.REQ);
            String address = "tcp://" + this.address;
            // System.out.println("requesting to " + addressHealth);
            client.bind(address);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String msg = cliente.getArchivo();
                    client.send(msg.getBytes(ZMQ.CHARSET), 0);
                    // System.out.println("enviando: " + msg);

                    byte[] reply = client.recv();
                    System.out.println("Recibi: " + new String(reply, ZMQ.CHARSET));
                    Thread.sleep(500);
                    //client.recv(0)
                    //System.out.println();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


    /*
     * public void subscribe() throws InterruptedException {
     * try (ZContext context = new ZContext()) {
     * Socket subcriber = context.createSocket(SocketType.SUB);
     * String address = "tcp://" + this.address;
     * subcriber.connect(address);
     * System.out.println("listening to " + address);
     * String topic = monitor.getId();
     * System.out.println("topic: " + topic);
     * subcriber.subscribe(topic.getBytes(ZMQ.CHARSET));
     * while (!Thread.currentThread().isInterrupted()) {
     * String msg = subcriber.recvStr(0);
     * String a[] = msg.split(" ");
     * float valor = Float.parseFloat(a[1]);
     * if (checkValor(valor)) {
     * System.out.println(msg);
     * escribirArchivo(msg);
     * } else {
     * new Thread(() -> {
     * System.out.println("El valor " + valor +
     * " esta fuera de rango, enviando alarma");
     * }).start();
     *
     * }
     *
     * }
     * }
     * }
     */


