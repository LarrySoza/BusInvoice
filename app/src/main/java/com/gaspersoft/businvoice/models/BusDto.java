package com.gaspersoft.businvoice.models;

import java.util.List;

public class BusDto {
    public Integer totalPisos;
    public Integer totalFilasPrimerPiso;
    public Integer totalFilasSegundoPiso;
    public String placa;
    public String codigo;

    public List<BusItemDto> itemsPrimerPiso;
    public List<BusItemDto> itemsSegundoPiso;
}
