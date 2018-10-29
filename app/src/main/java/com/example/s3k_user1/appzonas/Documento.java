package com.example.s3k_user1.appzonas;

public class Documento {
    public String Nombre;

    public String Descripcion;

    public String TipoContrato;
    public String Fecha;
    public String Estado;

    public Documento() {

    }

    public Documento(String nombre, String descripcion, String tipoContrato, String fecha, String estado) {
        Nombre = nombre;
        Descripcion = descripcion;
        TipoContrato = tipoContrato;
        Fecha = fecha;
        Estado = estado;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getTipoContrato() {
        return TipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        TipoContrato = tipoContrato;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }
}
