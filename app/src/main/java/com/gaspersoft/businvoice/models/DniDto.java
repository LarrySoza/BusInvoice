package com.gaspersoft.businvoice.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DniDto {
    @SerializedName("dni")
    @Expose
    public String dni;

    @SerializedName("apellido_paterno")
    @Expose
    public String apellido_paterno;

    @SerializedName("apellido_materno")
    @Expose
    public String apellido_materno;

    @SerializedName("nombres")
    @Expose
    public String nombres;

    public String GetNombre() {
        String nombreCompleto = nombres;
        nombreCompleto = nombreCompleto.trim() + " " + apellido_paterno;
        nombreCompleto = nombreCompleto.trim() + " " + apellido_materno;
        return nombreCompleto.trim();
    }
}
