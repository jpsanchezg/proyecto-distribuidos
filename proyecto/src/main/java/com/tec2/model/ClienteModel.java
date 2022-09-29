package com.tec2.model;

	/**
     * Clase que contiene los datos relevantes de un Sensor
     * tipo - valor numerico que representa el tipo de metrica a tomar
     *  1 - Temperatura
     *  2 - PH
     *  3 - Oxigeno
     * intervalo - tiempo en el que se generaran metricas
     * archivo - ruta del archivo de configuracion de los sensores
     * valor - unidad numerica que contendra la metrica a generar
     * unidad - escala de la medida a tomar
     * id - representacion textual del tipo de monitor
     * port: TODO: BORRAR
	 */
public class ClienteModel {

    private int tipo;
    private int intervalo;
    private String archivo;
    private float valor;
    private String unidad;
    private String id;

    public ClienteModel(int tipo, String archivo) {
        this.tipo = tipo;
        this.archivo = archivo;
        switch(tipo){
            case 1:
            this.id = "S1";
            break;
            case 2:
            this.id = "S2";
            break;
            case 3:
            this.id = "S3";
            break;
        }
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public int getTipo() {
        return tipo;
    }



    public void setTipo(int tipo) {
        this.tipo = tipo;
    }



    public int getIntervalo() {
        return intervalo;
    }



    public void setIntervalo(int intervalo) {
        this.intervalo = intervalo;
    }



    public String getArchivo() {
        return archivo;
    }



    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }



    public float getValor() {
        return valor;
    }



    public void setValor(float valor) {
        this.valor = valor;
    }



    public String getUnidad() {
        return unidad;
    }



    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }



    @Override
    public String toString() {
        return "SensorControl [archivo=" + archivo + ", intervalo=" + intervalo + ", tipo=" + tipo + "]";
    }

}
