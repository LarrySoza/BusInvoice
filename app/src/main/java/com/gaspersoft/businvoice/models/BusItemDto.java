package com.gaspersoft.businvoice.models;

public class BusItemDto {
    /// 0=Asiento
    /// 1=TV
    /// 2=Cafetería,
    /// 3=SSHH
    /// 4=Escalera
    /// 5=Pasadizo
    /// 7=Otros(Colocar como un asiento Ocupado
    public Integer tipo;

    /// si noes asiento colocar 0
    public Integer asiento;

    public Integer fila;

    public Integer columna;

    /// 0: disponible >1: bloqueado
    public Integer estado;

    public Double tarifa;

    public String GetId() {
        String id = "btn" + fila.toString() + "_" + columna.toString();
        return id;
    }
}
