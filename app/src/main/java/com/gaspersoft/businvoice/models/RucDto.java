package com.gaspersoft.businvoice.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RucDto {
    @SerializedName("ruc")
    @Expose
    public String ruc;

    @SerializedName("nombre_o_razon_social")
    @Expose
    public String nombre_o_razon_social;
}
