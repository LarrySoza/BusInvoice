package com.gaspersoft.businvoice.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InfoPasajeDto {
    public String toString() {
        return cpeNumeroDocumento + "\n" + pasajeroNombre + "\n" + pasajePuntoOrigen + " -> " + pasajePuntoLlegada + "\n" + cpeSimboloMoneda + " " + cpeImporteTotal;
    }

    public String id;

    @SerializedName("empresaNombre")
    @Expose
    public String empresaNombre;

    @SerializedName("empresaDireccion")
    @Expose
    public String empresaDireccion;

    @SerializedName("empresaRuc")
    @Expose
    public String empresaRuc;

    @SerializedName("cpeTipoDocumentoId")
    @Expose
    public String cpeTipoDocumentoId;

    @SerializedName("cpeNombreDocumento")
    @Expose
    public String cpeNombreDocumento;

    @SerializedName("cpeNumeroDocumento")
    @Expose
    public String cpeNumeroDocumento;

    @SerializedName("cpeFechaEmision")
    @Expose
    public String cpeFechaEmision;

    @SerializedName("cpeTotalOperacionesGravadas")
    @Expose
    public String cpeTotalOperacionesGravadas;

    @SerializedName("cpeTotalOperacionesExoneradas")
    @Expose
    public String cpeTotalOperacionesExoneradas;

    @SerializedName("cpeTotalOperacionesInafectas")
    @Expose
    public String cpeTotalOperacionesInafectas;

    @SerializedName("cpeTasaIgv")
    @Expose
    public String cpeTasaIgv;

    @SerializedName("cpeSumatoriaIgv")
    @Expose
    public String cpeSumatoriaIgv;

    @SerializedName("cpeDescripcionServicio")
    @Expose
    public String cpeDescripcionServicio;

    @SerializedName("cpeImporteServicio")
    @Expose
    public String cpeImporteServicio;

    @SerializedName("cpeImporteTotal")
    @Expose
    public String cpeImporteTotal;

    @SerializedName("cpeFormaPago")
    @Expose
    public String cpeFormaPago;

    @SerializedName("cpeSimboloMoneda")
    @Expose
    public String cpeSimboloMoneda;

    @SerializedName("cpeUrlConsulta")
    @Expose
    public String cpeUrlConsulta;

    @SerializedName("cpeResumenQr")
    @Expose
    public String cpeResumenQr;

    @SerializedName("cpeCajero")
    @Expose
    public String cpeCajero;

    @SerializedName("pasajeroTipoDocumento")
    @Expose
    public String pasajeroTipoDocumento;

    @SerializedName("pasajeroNumeroDocumento")
    @Expose
    public String pasajeroNumeroDocumento;

    @SerializedName("pasajeroNombre")
    @Expose
    public String pasajeroNombre;

    @SerializedName("pasajeroRuc")
    @Expose
    public String pasajeroRuc;

    @SerializedName("pasajeroRazonSocial")
    @Expose
    public String pasajeroRazonSocial;

    @SerializedName("pasajePuntoOrigen")
    @Expose
    public String pasajePuntoOrigen;

    @SerializedName("pasajePuntoLlegada")
    @Expose
    public String pasajePuntoLlegada;

    @SerializedName("pasajeFechaViaje")
    @Expose
    public String pasajeFechaViaje;

    @SerializedName("pasajeNumeroAsiento")
    @Expose
    public String pasajeNumeroAsiento;
}
