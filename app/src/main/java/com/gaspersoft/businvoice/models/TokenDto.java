package com.gaspersoft.businvoice.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenDto {
    @SerializedName("access_token")
    @Expose
    public String access_token;

    @SerializedName("tipo_usuario")
    @Expose
    public String tipo_usuario;

    @SerializedName("permite_conf_boleto")
    @Expose
    public String permite_conf_boleto;
}
