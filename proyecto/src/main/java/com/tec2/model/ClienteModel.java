package com.tec2.model;


public class ClienteModel {

    private int tipo;
    private int intervalo;
    private String archivo;
    private float valor;
    private String unidad;
    private String id;

    public ClienteModel(int tipo) {
        this.tipo = tipo;
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





}
