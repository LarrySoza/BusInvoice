package com.gaspersoft.businvoice.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoletoViajeDto {
    @SerializedName("pasajeroTipoDocumento")
    @Expose
    public String pasajeroTipoDocumento;

    @SerializedName("pasajeroNumeroDocumento")
    @Expose
    public String pasajeroNumeroDocumento;

    @SerializedName("pasajeroNombre")
    @Expose
    public String pasajeroNombre;

    @SerializedName("origenId")
    @Expose
    public String origenId;

    @SerializedName("destinoId")
    @Expose
    public String destinoId;

    @SerializedName("cpeImporteTotal")
    @Expose
    public Double cpeImporteTotal;

    @SerializedName("cpeTipoDocumentoId")
    @Expose
    public String cpeTipoDocumentoId;

    @SerializedName("pasajeroRuc")
    @Expose
    public String pasajeroRuc;

    @SerializedName("pasajeroRazonSocial")
    @Expose
    public String pasajeroRazonSocial;

    public int empresaId;
    public int progitem;
    public int programacionId;
    public int asiento;
}
