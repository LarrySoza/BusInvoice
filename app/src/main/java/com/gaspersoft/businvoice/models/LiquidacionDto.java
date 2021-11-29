package com.gaspersoft.businvoice.models;

import java.util.List;

public class LiquidacionDto {
    public List<LiquidacionItemDto> facturas;
    public List<LiquidacionItemDto> boletas;
    public double importeTotalFacturas;
    public double importeTotalBoletas;
    public double importeTotal;
    public String fechaStr;
    public String usuario;
}
