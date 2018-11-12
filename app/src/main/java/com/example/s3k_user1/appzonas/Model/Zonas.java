package com.example.s3k_user1.appzonas.Model;



public class Zonas {


    private String ZonaTrabajoId;

    private String Descripcion;

    private String UsuarioID;
    private String Direccion;


    private String Latitud;


    private String Longitud;

    private String Radio;

    private String DentroZona;

    private String Estado;

    public Zonas() {
    }

    public Zonas(String zonaTrabajoId, String descripcion, String usuarioID, String direccion, String latitud, String longitud, String radio, String dentroZona, String estado) {
        ZonaTrabajoId = zonaTrabajoId;
        Descripcion = descripcion;
        UsuarioID = usuarioID;
        Direccion = direccion;
        Latitud = latitud;
        Longitud = longitud;
        Radio = radio;
        DentroZona = dentroZona;
        Estado = estado;
    }

    public String getZonaTrabajoId() {
        return ZonaTrabajoId;
    }

    public void setZonaTrabajoId(String zonaTrabajoId) {
        ZonaTrabajoId = zonaTrabajoId;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getUsuarioID() {
        return UsuarioID;
    }

    public void setUsuarioID(String usuarioID) {
        UsuarioID = usuarioID;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getLatitud() {
        return Latitud;
    }

    public void setLatitud(String latitud) {
        Latitud = latitud;
    }

    public String getLongitud() {
        return Longitud;
    }

    public void setLongitud(String longitud) {
        Longitud = longitud;
    }

    public String getRadio() {
        return Radio;
    }

    public void setRadio(String radio) {
        Radio = radio;
    }

    public String getDentroZona() {
        return DentroZona;
    }

    public void setDentroZona(String dentroZona) {
        DentroZona = dentroZona;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }
}

