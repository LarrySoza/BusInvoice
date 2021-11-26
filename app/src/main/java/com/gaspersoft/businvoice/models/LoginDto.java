package com.gaspersoft.businvoice.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginDto {
    @SerializedName("usuario")
    @Expose
    public String usuario;

    @SerializedName("clave")
    @Expose
    public String clave;
}
