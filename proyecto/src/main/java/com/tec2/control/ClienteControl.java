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


    private String address;

    private ClienteModel cliente;
    private Socket client;
    int id = 0, cantidad = 0;
    String mensaje, msg;
    private static ClienteControl control;

    public ClienteControl() {
        inicializarDireccion();
        // Inicializamos parametros del cliente
    }

    public ClienteControl(int tipo) throws InterruptedException {
        inicializarDireccion();
        // Inicializamos parametros del cliente
        this.cliente = new ClienteModel(tipo);
        if (tipo == 4) {
            this.client.disconnect(address);
        }
    }

    /**
     * Inicializacion de las direcciones ip a partir del archivo de direcciones
     */
    public void inicializarDireccion() {
        String ruta = "res/shared/direcciones.txt";
        ArrayList<String> lista = lecturaArchivo(ruta);
        for (String s : lista) {
            String[] valores = s.split(" ");
            if (valores[0].compareTo("proxy") == 0) {
                this.address = valores[2] + ":" + valores[3];
            }
        }
    }

    public void menu() throws InterruptedException {
        boolean finalizo = false;
        int opcion = 0;

        while (!finalizo) {
            System.out.println("escoge una de las siguientes opciones");
            System.out.println("1. traer productos de la tienda");
            System.out.println("2. Comprar productos");
            System.out.println("3. salir");
            Scanner leer = new Scanner(System.in);
            opcion = leer.nextInt();
            if (opcion == 1) {
                this.cliente = new ClienteModel(opcion);
                publish();
            }
            if (opcion == 2) {
                this.cliente = new ClienteModel(opcion);
                if(mensaje!= null){
                    System.out.println(mensaje);
                    System.out.println("escoge el id del producto que vas a comprar");
                    id = leer.nextInt();
                    System.out.println("di la cantidad que vas a comprar");
                    cantidad = leer.nextInt();
                    msg = "comprar-"+"id-" + id + "-cantidad-" + cantidad;
                    publish();
                }
            }
            if (opcion == 3) {
                String address = "tcp://" + this.address;
                finalizo=true;
                this.client.disconnect(address);
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
            this.client = context.createSocket(SocketType.PAIR);
            String address = "tcp://" + this.address;
            this.client.connect(address);
            Thread.sleep(1000);
            try {
                int tipo = cliente.getTipo();
                if (tipo == 1) {
                    msg = "traer";
                    byte[] reply2 = msg.getBytes(zmq.ZMQ.CHARSET);
                    boolean tests = this.client.send(reply2, 0);
                    byte[] recb = this.client.recv();
                    mensaje = new String(recb, ZMQ.CHARSET);
                    System.out.println("Recibi: " + mensaje);
                    Thread.sleep(1000);
                }
                if (tipo == 2) {
                    byte[] reply2 = msg.getBytes(zmq.ZMQ.CHARSET);
                    boolean tests = this.client.send(reply2, 0);
                    byte[] reply = client.recv();
                    mensaje = new String(reply, ZMQ.CHARSET);
                    System.out.println("Recibi: " + mensaje);
                    Thread.sleep(500);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}



