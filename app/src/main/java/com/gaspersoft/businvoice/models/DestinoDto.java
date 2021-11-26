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

    public String toString() {
        return nombre;
    }
}
