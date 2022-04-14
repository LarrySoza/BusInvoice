package com.gaspersoft.businvoice.api;

import com.gaspersoft.businvoice.models.BoletoViajeDto;
import com.gaspersoft.businvoice.models.BusDto;
import com.gaspersoft.businvoice.models.DestinoDto;
import com.gaspersoft.businvoice.models.ExcesoDto;
import com.gaspersoft.businvoice.models.InfoExcesoDto;
import com.gaspersoft.businvoice.models.ProgramacionDto;
import com.gaspersoft.businvoice.models.DniDto;
import com.gaspersoft.businvoice.models.InfoPasajeDto;
import com.gaspersoft.businvoice.models.LiquidacionDto;
import com.gaspersoft.businvoice.models.LoginDto;
import com.gaspersoft.businvoice.models.OrigenDto;
import com.gaspersoft.businvoice.models.RucDto;
import com.gaspersoft.businvoice.models.TipoDocumentoDto;
import com.gaspersoft.businvoice.models.TokenDto;
import com.gaspersoft.businvoice.models.UsuarioDto;

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

    @POST("/api/exceso")
    Call<InfoExcesoDto> RegistrarExceso(@Header("Authorization") String token, @Body ExcesoDto exceso);

    @GET("/api/tipo-documento-identidad")
    Call<List<TipoDocumentoDto>> ListarTipoDocumentoIdentidad(@Header("Authorization") String token);

    @GET("/api/venta-ruta/{programacionId}")
    Call<List<InfoPasajeDto>> GetVentasProgramacion(@Header("Authorization") String token,@Path("programacionId") Integer programacionId);

    @GET("/api/origen")
    Call<List<OrigenDto>> ListarOrigenes(@Header("Authorization") String token);

    @GET("/api/destino/{origenId}")
    Call<List<DestinoDto>> ListarDestinos(@Header("Authorization") String token, @Path("origenId") String origenId);

    @GET("/api/bus/{programacionId}/{origenId}/{destinoId}")
    Call<BusDto> GetMapaBus(@Header("Authorization") String token, @Path("programacionId") Integer programacionId, @Path("origenId") String origenId, @Path("destinoId") String destinoId);

    @GET("/api/usuario")
    Call<List<UsuarioDto>> GetUsuarios(@Header("Authorization") String token);

    @GET("/api/programacion/{empresaId}/{origenId}/{destinoId}/{fecha}")
    Call<List<ProgramacionDto>> ListarProgramaciones(@Header("Authorization") String token, @Path("empresaId") int empresaId, @Path("origenId") String origenId, @Path("destinoId") String destinoId, @Path("fecha") String fecha);

    @GET("/api/ruc/{ruc}")
    Call<RucDto> GetEmpresa(@Header("Authorization") String token, @Path("ruc") String ruc);

    @GET("/api/liquidacion/{fecha}")
    Call<LiquidacionDto> GetLiquidacion(@Header("Authorization") String token, @Path("fecha") String fecha);

    @GET("/api/liquidacion/{fecha}/{usuario}")
    Call<LiquidacionDto> GetLiquidacion(@Header("Authorization") String token, @Path("fecha") String fecha,@Path("usuario") String usuario);

    @GET("/api/dni/{dni}")
    Call<DniDto> GetPersonaPorDni(@Header("Authorization") String token, @Path("dni") String dni);
}
