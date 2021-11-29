package com.gaspersoft.businvoice.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DestinoDto {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("nombre")
    @Expose
    public String nombre;

    @SerializedName("tarifa")
    @Expose
    public Double tarifa;

    @SerializedName("programacionId")
    @Expose
    public int programacionId;

    @SerializedName("fechaStr")
    @Expose
    public String fechaStr;

    @SerializedName("horaStr")
    @Expose
    public String horaStr;

    @SerializedName("bus")
    @Expose
    public String bus;

    @SerializedName("progitem")
    @Expose
    public int progitem;

    public String toString() {
        return nombre + " -> " + horaStr;
    }
}
