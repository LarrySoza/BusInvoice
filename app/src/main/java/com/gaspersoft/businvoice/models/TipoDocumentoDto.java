package com.gaspersoft.businvoice.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TipoDocumentoDto {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("nombre")
    @Expose
    public String nombre;

    public String toString() {
        return nombre;
    }
}
