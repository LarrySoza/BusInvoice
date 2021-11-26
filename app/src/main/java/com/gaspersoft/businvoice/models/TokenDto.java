package com.gaspersoft.businvoice.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenDto {
    @SerializedName("access_token")
    @Expose
    public String access_token;
}
