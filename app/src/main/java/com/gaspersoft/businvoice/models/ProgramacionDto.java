package com.gaspersoft.businvoice.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProgramacionDto {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("nombre_destino")
    @Expose
    public String nombre_destino;

    @SerializedName("nombre_origen")
    @Expose
    public String nombre_origen;

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
        return bus + "-" + nombre_origen + "-" + nombre_destino + " -> " + horaStr;
    }
}
