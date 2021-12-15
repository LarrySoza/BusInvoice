package com.gaspersoft.businvoice.models;

public class UsuarioDto {
    public String login;
    public String pass;
    public String id_sucursal;
    public String sucursal;
    public String nombre;
    public String estado;
    public String tipo_user;

    public String toString() {
        return sucursal + ": " + nombre;
    }
}
