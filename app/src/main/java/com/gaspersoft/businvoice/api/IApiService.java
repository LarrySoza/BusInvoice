package com.gaspersoft.businvoice.api;

import com.gaspersoft.businvoice.models.BoletoViajeDto;
import com.gaspersoft.businvoice.models.DestinoDto;
import com.gaspersoft.businvoice.models.DniDto;
import com.gaspersoft.businvoice.models.InfoPasajeDto;
import com.gaspersoft.businvoice.models.LoginDto;
import com.gaspersoft.businvoice.models.OrigenDto;
import com.gaspersoft.businvoice.models.RucDto;
import com.gaspersoft.businvoice.models.TipoDocumentoDto;
import com.gaspersoft.businvoice.models.TokenDto;

import java.util.Date;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.Path;

public interface IApiService {
    @POST("/api/token")
    Call<TokenDto> ObtenerToken(@Body LoginDto login);

    @POST("/api/venta-ruta")
    Call<InfoPasajeDto> RegistrarViaje(@Header("Authorization") String token,@Body BoletoViajeDto boleto);

    @GET("/api/tipo-documento-identidad")
    Call<List<TipoDocumentoDto>> ListarTipoDocumentoIdentidad(@Header("Authorization") String token);

    @GET("/api/venta-ruta/top10")
    Call<List<InfoPasajeDto>> GetLiquidacion(@Header("Authorization") String token);

    @GET("/api/origen")
    Call<List<OrigenDto>> ListarOrigenes(@Header("Authorization") String token);

    @GET("/api/destino/{empresaId}/{fecha}/{origenId}")
    Call<List<DestinoDto>> ListarDestinos(@Header("Authorization") String token,@Path("empresaId") int empresaId,@Path("fecha") String fecha, @Path("origenId") String origenId);

    @GET("/api/ruc/{ruc}")
    Call<RucDto> GetEmpresa(@Header("Authorization") String token, @Path("ruc") String ruc);

    @GET("/api/dni/{dni}")
    Call<DniDto> GetPersonaPorDni(@Header("Authorization") String token, @Path("dni") String dni);
}
