package com.tec2.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ;

public class ProxyControl {

    private String servidores;
    private String clientes;

    public ProxyControl() {
        inicializarDireccion();
    }

    public void inicializarDireccion(){
        String ruta = "res/shared/direcciones.txt";
        ArrayList<String> lista = lecturaArchivo(ruta);
        for(String s: lista){
            String[] valores = s.split(" ");
            if(valores[0].compareTo("clientes")==0){
                this.clientes ="*:"+valores[3];
            }
            else if(valores[0].compareTo("servidores")==0){
                this.servidores = "*:"+valores[3];
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
     * Mediante sockets XSUB/XPUB se enruta la informacion ofrecida por distintos publicadores hacia los suscriptores adecuados.
     */

    public void proxy(){
        try (ZContext context = new ZContext()) {
            System.out.println("Iniciando Proxy...");
            Socket entrada = context.createSocket(SocketType.XSUB);
            entrada.bind("tcp://"+this.clientes);
            Socket salida = context.createSocket(SocketType.XPUB);
            salida.bind("tcp://"+this.servidores);
            ZMQ.proxy(entrada, salida, null);

        }
    }

}
