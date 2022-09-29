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


    /**
     * A partir de los porcentajes de valores, genera una metrica a partir de una
     * semilla de generacion aleatoria
     */
    public void generarMetrica() {
        float valor;
        float seed = generarValor(0, 100);
        if (seed >= 0 && seed <= correcto) {
            valor = generarValor(min, max);
            cliente.setValor(valor);
        } else if (seed > correcto && seed <= (correcto + incorrecto)) {
            float seedIncorrect = generarValor(0, 100);
            if (seedIncorrect <= 50) {
                valor = generarValor(0, max - 1);
            } else {
                valor = generarValor(1000, 2000);
                valor = generarValor(max + 1, (max * 2));
            }

            cliente.setValor(valor);

        } else if (seed > (correcto + incorrecto) && seed <= (correcto + incorrecto + errores)) {
            valor = generarValor(0, -100);
            cliente.setValor(valor);
        }
    }

    /**
     * A partir del archivo de rangos, inicializa los intervalos de los valores
     * correctos
     *
     * @param tipo el tipo de metrica a la cual estan asociados los intervalos
     */
    public void inicializarRangos(String tipo) {
        String ruta = "res/shared/rangos.txt";
        ArrayList<String> lineas = lecturaArchivo(ruta);
        for (String s : lineas) {
            String[] valores = s.split(" ");
            if (valores[0].compareTo(tipo) == 0) {
                min = Float.parseFloat(valores[1]);
                max = Float.parseFloat(valores[2]);
            }
        }
    }

    /**
     * Lector simple de archivos
     *
     * @param ruta ruta del archivo a leer
     * @return
     */
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
     * captura el tiempo actual, y le da formato como string
     *
     * @return instante de tiempo formateado
     */
    public String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    /**
     * Genera valores aleatorios entre un intervalo dado
     *
     * @param min valor minimo del intervalo
     * @param max valor maximo del intervalo
     * @return numero aleatorio generado
     */
    public float generarValor(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    /**
     * Crea y se conecta al socket de tipo publicador, luego publica la informacion
     * por el mismo.
     *
     * @throws InterruptedException
     */
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
                    String msg = String.valueOf("thisnuts");
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


