package com.tec2.control;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private ArrayList<String> basededatos;

    float min, max;

    String id, cantidad;

    public ServerControl(int tipo) {
        this.monitor = new ServerModel(tipo);
        inicializarDireccion();

        new Thread(() -> {
            requestHealthCheck();
        }).start();
    }

    public void leerbasededatos() {
        String ruta = "res/basededatos/registro.txt";
        ArrayList<String> lista = lecturaArchivo(ruta);
        this.basededatos = lista;
    }

    public void escribirenlabasededatos() {
        String ruta = "res/basededatos/registro.txt";
        ArrayList<String> lista = lecturaArchivo(ruta);
        ArrayList<String> newlista = new ArrayList<>();
        int contador = 0;
        for (String s : lista) {

            String[] valores = s.split(" ");

            if (valores[0].equals(id)) {
                int cantidad1 = 0, cantidad2 = 0, cantidadfinal = 0;

                cantidad1 = Integer.parseInt(valores[2]);
                cantidad2 = Integer.parseInt(cantidad);
                cantidadfinal = cantidad1 - cantidad2;
                valores[2] = String.valueOf(cantidadfinal);
            }
            System.out.println(valores[2]);
            newlista.add(valores[0] + " " + valores[1] + " " + valores[2] + " " + valores[3]);
            contador++;
        }
        escrituraarchivo(newlista);
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

    public void escrituraarchivo(ArrayList<String> Nlista) {
        String ruta = "res/basededatos/registro.txt";
        try {
            File archivo = new File(ruta);
            archivo.delete();
            FileWriter escribir = new FileWriter(archivo, true);
            for (int i = 0; i < Nlista.size(); i++) {
                System.out.println(Nlista.get(i));
                escribir.write(Nlista.get(i));
                escribir.write("\r\n");
            }
            escribir.close();
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado.");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                    String mensaje =new String(reply, ZMQ.CHARSET);
                    if(!mensaje.equals("ok")){
                        System.out.println("algopaso");
                    }
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
                String[] parts = mensaje.split("-");
                System.out.println(parts[0]);
                if (mensaje.equals("traer")) {
                    leerbasededatos();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(baos);
                    for (String element : this.basededatos) {
                        out.writeUTF(element);
                    }
                    byte[] bytes = baos.toByteArray();

                    boolean test = publicher.send(bytes);
                    System.out.println("test: " + test);
                }
                if (parts[0].equals("comprar")) {
                    leerbasededatos();
                    id = parts[2];
                    cantidad = parts[4];
                    escribirenlabasededatos();


                    String msg = "se realizo la compra";
                    byte[] bytes = msg.getBytes();

                    boolean test = publicher.send(bytes);
                    System.out.println("test: " + test);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
