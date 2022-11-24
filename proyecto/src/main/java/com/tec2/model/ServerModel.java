package com.tec2.model;



public class ServerModel {

    private int tipoMonitor;
    private String id;
    private String archivo;

    public ServerModel(int tipoMonitor) {
        this.tipoMonitor = tipoMonitor;
        switch(tipoMonitor){
            case 1:
            this.id = "S1"; // servidor 1
            break;
            case 2:
            this.id = "S2"; // servidor 2
            break;
            case 3:
            this.id = "S3"; // servidor 3
            break;
        }
    }



    public String getId() {
        return id;
    }



    public void setId(String id) {
        this.id = id;
    }



    public int getTipoMonitor() {
        return tipoMonitor;
    }



    public void setTipoMonitor(int tipoMonitor) {
        this.tipoMonitor = tipoMonitor;
    }



    public String getArchivo() {
        return archivo;
    }



    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

}
