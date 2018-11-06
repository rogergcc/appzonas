package com.example.s3k_user1.appzonas.Model;

public class EstadoProceso {
    public String EstadoProcesoId;

    public String Tipo;

    public String Nombre;
    public String Descripcion;
    public String Estado;
    public String CantidadDocsSegunEstadoProceso;

    public EstadoProceso() {
    }

    public EstadoProceso(String estadoProcesoId, String tipo, String nombre, String descripcion, String estado, String cantidadDocsSegunEstadoProceso) {
        EstadoProcesoId = estadoProcesoId;
        Tipo = tipo;
        Nombre = nombre;
        Descripcion = descripcion;
        Estado = estado;
        CantidadDocsSegunEstadoProceso = cantidadDocsSegunEstadoProceso;
    }

    public String getEstadoProcesoId() {
        return EstadoProcesoId;
    }

    public void setEstadoProcesoId(String estadoProcesoId) {
        EstadoProcesoId = estadoProcesoId;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
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

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getCantidadDocsSegunEstadoProceso() {
        return CantidadDocsSegunEstadoProceso;
    }

    public void setCantidadDocsSegunEstadoProceso(String cantidadDocsSegunEstadoProceso) {
        CantidadDocsSegunEstadoProceso = cantidadDocsSegunEstadoProceso;
    }
}
