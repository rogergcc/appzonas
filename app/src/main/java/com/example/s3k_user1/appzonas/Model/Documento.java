package com.example.s3k_user1.appzonas.Model;

public class Documento {
    private int DocumentoId;

    private String Nombre;

    private String Descripcion;

    private String TipoContrato;
    private String Fecha;
    private String Estado;
    private String Status;

    public Documento() {
    }

    public Documento(int documentoId, String nombre, String descripcion, String tipoContrato, String fecha, String estado, String status) {
        DocumentoId = documentoId;
        Nombre = nombre;
        Descripcion = descripcion;
        TipoContrato = tipoContrato;
        Fecha = fecha;
        Estado = estado;
        Status = status;
    }

    public int getDocumentoId() {
        return DocumentoId;
    }

    public void setDocumentoId(int documentoId) {
        DocumentoId = documentoId;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
