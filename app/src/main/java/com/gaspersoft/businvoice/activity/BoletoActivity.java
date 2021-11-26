package com.gaspersoft.businvoice.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.gaspersoft.businvoice.ClsGlobal;
import com.gaspersoft.businvoice.api.ApiClient;
import com.gaspersoft.businvoice.api.IApiService;
import com.gaspersoft.businvoice.models.BoletoViajeDto;
import com.gaspersoft.businvoice.models.DestinoDto;
import com.gaspersoft.businvoice.models.DniDto;
import com.gaspersoft.businvoice.models.InfoPasajeDto;
import com.gaspersoft.businvoice.models.OrigenDto;
import com.gaspersoft.businvoice.models.RucDto;
import com.gaspersoft.businvoice.models.TipoDocumentoDto;
import com.gaspersoft.businvoice.utils.*;
import com.gaspersoft.businvoice.R;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BoletoActivity extends AppCompatActivity {
    private int print_size = 6;
    private int error_level = 3;
    private String tokenStr;

    private Spinner spTipoDocumento;
    private EditText txtNumeroDocumento;
    private EditText txtNombrePasajero;
    private Spinner spOrigenes;
    private Spinner spDestinos;
    private EditText txtTarifa;
    private EditText txtRuc;
    private EditText txtRazonSocial;
    private RadioButton optFactura;
    private RadioButton optBoleta;
    private Button btnConsultarDni;
    private Button btnConsultarRuc;
    private Button btnRegistrarBoleto;
    private ProgressBar waitControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boleto);

        //Inicializamos el servicio de impresion
        PrintHelper.getInstance().initSunmiPrinterService(this);

        spTipoDocumento = findViewById(R.id.spTipoDocumento);
        txtNumeroDocumento = findViewById(R.id.txtNumeroDocumento);
        txtNombrePasajero = findViewById(R.id.txtNombrePasajero);
        spOrigenes = findViewById(R.id.spOrigen);
        spDestinos = findViewById(R.id.spDestino);
        txtTarifa = findViewById(R.id.txtTarifa);
        txtRuc = findViewById(R.id.txtRuc);
        txtRazonSocial = findViewById(R.id.txtRazonSocial);
        optBoleta = findViewById(R.id.optBoleta);
        optFactura = findViewById(R.id.optFactura);
        btnConsultarDni=findViewById(R.id.btnConsultarDni);
        btnConsultarRuc=findViewById(R.id.btnConsultarRuc);
        btnRegistrarBoleto=findViewById(R.id.btnRegistrar);
        waitControl=findViewById(R.id.waitControl);

        optBoleta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    txtRuc.setEnabled(false);
                    txtRazonSocial.setEnabled(false);
                    btnConsultarRuc.setEnabled(false);
                }
            }
        });

        optFactura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtRuc.setEnabled(true);
                    txtRazonSocial.setEnabled(true);
                    btnConsultarRuc.setEnabled(true);
                    txtRuc.requestFocus();
                }
            }
        });

        btnConsultarDni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(txtNumeroDocumento.getText().toString().trim())) {
                    txtNumeroDocumento.setError("Ingrese Dni");
                } else {

                    if (txtNumeroDocumento.getText().toString().trim().length() != 8) {
                        txtNumeroDocumento.setError("Dni no valido");
                    } else {
                        waitControl.setVisibility(View.VISIBLE);
                        ConsultarDni(txtNumeroDocumento.getText().toString().trim());
                    }
                }
            }
        });

        btnConsultarRuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(txtRuc.getText().toString().trim())) {
                    txtRuc.setError("Ingrese Ruc");
                } else {
                    String ruc = txtRuc.getText().toString();

                    if (ClsGlobal.isRUCValid(ruc)) {
                        waitControl.setVisibility(View.VISIBLE);
                        ConsultarRuc(txtRuc.getText().toString().trim());
                    } else {
                        txtRuc.setError("Ruc no valido");
                    }
                }
            }
        });

        btnRegistrarBoleto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean validado = true;
                txtNumeroDocumento.setError(null);
                txtNombrePasajero.setError(null);
                txtRuc.setError(null);
                txtRazonSocial.setError(null);

                TipoDocumentoDto tipoDocumentoDto = (TipoDocumentoDto) spTipoDocumento.getSelectedItem();
                String pasajeroNumeroDocumento = txtNumeroDocumento.getText().toString().trim();
                String pasajeroNombre = txtNombrePasajero.getText().toString();

                if (tipoDocumentoDto.id.equals("-")) {
                    pasajeroNumeroDocumento = "";
                    pasajeroNombre = "*CLIENTES VARIOS*";
                }

                OrigenDto origenDto = (OrigenDto) spOrigenes.getSelectedItem();
                DestinoDto destinoDto = (DestinoDto) spDestinos.getSelectedItem();
                String cpeImporteTotal = txtTarifa.getText().toString();
                String cpeTipoDocumentoId = "03";
                String pasajeroRuc = "";
                String pasajeroRazonSocial = "";

                if (tipoDocumentoDto.id.equals("1")) {
                    if (pasajeroNumeroDocumento.length() != 8) {
                        txtNumeroDocumento.setError("Dni no valido");
                        validado = false;
                    }
                }

                if (pasajeroNombre.length() == 0) {
                    txtNombrePasajero.setError("Ingrese Nombre");
                    validado = false;
                }

                if (cpeImporteTotal.length() == 0) {
                    txtTarifa.setError("Ingrese precio");
                    validado = false;
                } else {
                    if (Double.parseDouble(cpeImporteTotal) == 0) {
                        txtTarifa.setError("Ingrese un valor mayor a 0");
                        validado = false;
                    }
                }


                if (optFactura.isChecked()) {
                    cpeTipoDocumentoId = "01";
                    pasajeroRuc = txtRuc.getText().toString();
                    pasajeroRazonSocial = txtRazonSocial.getText().toString().trim();

                    if (!ClsGlobal.isRUCValid(pasajeroRuc)) {
                        txtRuc.setError("Ruc no valido");
                        validado = false;
                    }

                    if (pasajeroRazonSocial.length() == 0) {
                        txtRazonSocial.setError("Ingrese Razon Social");
                        validado = false;
                    }
                }

                if (validado) {
                    BoletoViajeDto boleto = new BoletoViajeDto();
                    boleto.pasajeroTipoDocumento = tipoDocumentoDto.id;
                    boleto.pasajeroNumeroDocumento = pasajeroNumeroDocumento;
                    boleto.pasajeroNombre = pasajeroNombre;
                    boleto.origenId = origenDto.id;
                    boleto.destinoId = destinoDto.id;
                    boleto.cpeImporteTotal = Double.parseDouble(cpeImporteTotal);
                    boleto.cpeTipoDocumentoId = cpeTipoDocumentoId;
                    boleto.pasajeroRuc = pasajeroRuc;
                    boleto.pasajeroRazonSocial = pasajeroRazonSocial;

                    waitControl.setVisibility(View.VISIBLE);
                    RegistrarBoletoViaje(boleto);
                }
            }
        });

        Limpiar();

        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);

        tokenStr = preferences.getString("token", "");
        if ("".equals(tokenStr)) {
            Intent frmLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(frmLogin);
            finish();
        } else {
            CargarTiposDocumentos();
            CargarOrigenes();
        }
    }

    private void Limpiar() {
        txtNumeroDocumento.setText("");
        txtNombrePasajero.setText("");
        optBoleta.setChecked(true);
        txtRuc.setText("");
        txtRazonSocial.setText("");
        btnConsultarDni.setEnabled(true);

        txtRuc.setEnabled(false);
        txtRazonSocial.setEnabled(false);
        btnConsultarRuc.setEnabled(false);

        spTipoDocumento.setSelection(0);
        txtNumeroDocumento.requestFocus();
    }

    private String GetHeaderToken() {
        return "Bearer " + tokenStr;
    }

    public void CargarTiposDocumentos() {
        ApiClient.GetService().ListarTipoDocumentoIdentidad(GetHeaderToken())
                .enqueue(new Callback<List<TipoDocumentoDto>>() {
                    @Override
                    public void onResponse(Call<List<TipoDocumentoDto>> call, Response<List<TipoDocumentoDto>> response) {
                        try {
                            if (response.isSuccessful()) {
                                ArrayAdapter<TipoDocumentoDto> adapterTiposDocumento = new ArrayAdapter<TipoDocumentoDto>(getApplicationContext(), R.layout.spinner_item, response.body());
                                spTipoDocumento.setAdapter(adapterTiposDocumento);
                                spTipoDocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        TipoDocumentoDto tipoDocumentoDto = (TipoDocumentoDto) spTipoDocumento.getSelectedItem();

                                        if (tipoDocumentoDto.id.equals("1")) {
                                            btnConsultarDni.setEnabled(true);
                                            txtNumeroDocumento.setInputType(InputType.TYPE_CLASS_NUMBER);
                                        } else {
                                            btnConsultarDni.setEnabled(false);
                                            txtNumeroDocumento.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                                        }

                                        if (tipoDocumentoDto.id.equals("-")) {
                                            txtNumeroDocumento.setEnabled(false);
                                            txtNombrePasajero.setEnabled(false);
                                        } else {
                                            txtNumeroDocumento.setEnabled(true);
                                            txtNombrePasajero.setEnabled(true);
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            } else {
                                if (response.code() == 401) {
                                    SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor objEditor = preferences.edit();
                                    objEditor.putString("token", "");
                                    objEditor.commit();

                                    Intent frmLogin = new Intent(getApplicationContext(), BoletoActivity.class);
                                    startActivity(frmLogin);
                                    finish();

                                } else {
                                    Toast.makeText(getApplicationContext(), "El servidor devolvio codigo" + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TipoDocumentoDto>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void RegistrarBoletoViaje(BoletoViajeDto boleto) {
        ApiClient.GetService().RegistrarViaje(GetHeaderToken(), boleto)
            .enqueue(new Callback<InfoPasajeDto>() {
            @Override
            public void onResponse(Call<InfoPasajeDto> call, Response<InfoPasajeDto> response) {
                try {
                    if (response.isSuccessful()) {
                        InfoPasajeDto info = response.body();
                        //ImprimirCpe(info);
                        Limpiar();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al registrar pasaje", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

                waitControl.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<InfoPasajeDto> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void CargarOrigenes() {
        ApiClient.GetService().ListarOrigenes(GetHeaderToken())
                .enqueue(new Callback<List<OrigenDto>>() {
            @Override
            public void onResponse(Call<List<OrigenDto>> call, Response<List<OrigenDto>> response) {
                try {
                    if (response.isSuccessful()) {
                        ArrayAdapter<OrigenDto> adapterOrigenes = new ArrayAdapter<OrigenDto>(getApplicationContext(), R.layout.spinner_item, response.body());
                        spOrigenes.setAdapter(adapterOrigenes);
                        spOrigenes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                OrigenDto origen = (OrigenDto) spOrigenes.getSelectedItem();
                                waitControl.setVisibility(View.VISIBLE);
                                CargarDestinos(origen.id);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        if (response.code() == 401) {
                            SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                            SharedPreferences.Editor objEditor = preferences.edit();
                            objEditor.putString("token", "");
                            objEditor.commit();

                            Intent frmLogin = new Intent(getApplicationContext(), BoletoActivity.class);
                            startActivity(frmLogin);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "El servidor devolvio codigo" + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrigenDto>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ConsultarDni(String dni) {
        ApiClient.GetService().GetPersonaPorDni(GetHeaderToken(), dni)
            .enqueue(new Callback<DniDto>() {
            @Override
            public void onResponse(Call<DniDto> call, Response<DniDto> response) {
                if (response.isSuccessful()) {
                    DniDto dni = response.body();
                    txtNombrePasajero.setText(dni.GetNombre());
                    txtNombrePasajero.setError(null);
                } else {
                    Toast.makeText(getApplicationContext(), "Error al Consultar Dni", Toast.LENGTH_SHORT).show();
                }

                waitControl.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<DniDto> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ConsultarRuc(String ruc) {
        ApiClient.GetService().GetEmpresa(GetHeaderToken(), ruc)
                .enqueue(new Callback<RucDto>() {
                    @Override
                    public void onResponse(Call<RucDto> call, Response<RucDto> response) {
                        if (response.isSuccessful()) {
                            RucDto ruc = response.body();
                            txtRazonSocial.setText(ruc.nombre_o_razon_social);
                            txtRazonSocial.setError(null);
                        } else {
                            Toast.makeText(getApplicationContext(), "Error al Consultar Ruc", Toast.LENGTH_SHORT).show();
                        }

                        waitControl.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<RucDto> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void CargarDestinos(String origenId) {
        ApiClient.GetService().ListarDestinos(GetHeaderToken(), origenId)
                .enqueue(new Callback<List<DestinoDto>>() {
            @Override
            public void onResponse(Call<List<DestinoDto>> call, Response<List<DestinoDto>> response) {
                try {
                    if (response.isSuccessful()) {
                        ArrayAdapter<DestinoDto> adapterDestinos = new ArrayAdapter<DestinoDto>(getApplicationContext(), R.layout.spinner_item, response.body());
                        spDestinos.setAdapter(adapterDestinos);
                        spDestinos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    DestinoDto destino = (DestinoDto) spDestinos.getSelectedItem();
                                    txtTarifa.setText(destino.tarifa.toString());
                                } catch (Exception ex) {
                                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        if (response.code() == 401) {
                            SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                            SharedPreferences.Editor objEditor = preferences.edit();
                            objEditor.putString("token", "");
                            objEditor.commit();

                            Intent frmLogin = new Intent(getApplicationContext(), BoletoActivity.class);
                            startActivity(frmLogin);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "El servidor devolvio codigo" + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

                waitControl.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<DestinoDto>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ImprimirCpe(InfoPasajeDto infoPasaje) {
        if (!BluetoothUtil.isBlueToothPrinter) {
            PrintHelper.getInstance().initPrinter();
            //0=Left  1=Center  2=Right
            PrintHelper.getInstance().setAlign(1);

            //Cabecera del documento
            Bitmap logo = BitmapFactory.decodeResource(this.getResources(), R.drawable.viaunoo);
            PrintHelper.getInstance().printBitmap(logo);
            PrintHelper.getInstance().printLine();
            PrintHelper.getInstance().printText(infoPasaje.empresaNombre + "\n", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.empresaDireccion + "\n", 22, false, false);
            PrintHelper.getInstance().printText("RUC:" + infoPasaje.empresaRuc + "\n", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.cpeNombreDocumento + "\n", 22, true, false);
            PrintHelper.getInstance().printText("N° " + infoPasaje.cpeNumeroDocumento + "\n", 22, true, false);


            if (infoPasaje.cpeTipoDocumentoId.equals("01")) {
                PrintHelper.getInstance().printLineDashed();
                PrintHelper.getInstance().printText("DATOS DE FACTURACION" + "\n", 22, true, false);
                PrintHelper.getInstance().setAlign(0);
                PrintHelper.getInstance().printText("FECHA EMISION: ", 22, true, false);
                PrintHelper.getInstance().printText(infoPasaje.cpeFechaEmision + "\n", 22, false, false);
                PrintHelper.getInstance().printText("RUC: ", 22, true, false);
                PrintHelper.getInstance().printText(infoPasaje.pasajeroRuc + "\n", 22, false, false);
                PrintHelper.getInstance().printText("RAZON SOCIAL: ", 22, true, false);
                PrintHelper.getInstance().printText(infoPasaje.pasajeroRazonSocial + "\n", 22, false, false);
                PrintHelper.getInstance().printText("FORMA PAGO: ", 22, true, false);
                PrintHelper.getInstance().printText(infoPasaje.cpeFormaPago + "\n", 22, false, false);
            }

            PrintHelper.getInstance().printLineDashed();

            //Informacion del pasajero
            PrintHelper.getInstance().setAlign(1);
            PrintHelper.getInstance().printText("INFORMACION DEL PASAJERO" + "\n", 22, true, false);
            PrintHelper.getInstance().setAlign(0);
            PrintHelper.getInstance().printText(infoPasaje.pasajeroTipoDocumento + ": ", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.pasajeroNumeroDocumento + "\n", 22, false, false);
            PrintHelper.getInstance().printText("NOMBRE: ", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.pasajeroNombre + "\n", 22, false, false);
            PrintHelper.getInstance().printText("ORIGEN: ", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.pasajePuntoOrigen + "\n", 22, false, false);
            PrintHelper.getInstance().printText("DESTINO: ", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.pasajePuntoLlegada + "\n", 22, false, false);
            PrintHelper.getInstance().printText("FECHA DE VIAJE: ", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.pasajeFechaViaje + "\n", 22, false, false);
            PrintHelper.getInstance().printText("NUMERO DE ASIENTO: ", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.pasajeNumeroAsiento + "\n", 22, false, false);

            //DESCRIPCION DEL SERVICIO
            int width[] = new int[]{1, 2, 1};
            int align[] = new int[]{1, 0, 2};
            PrintHelper.getInstance().printLineDashed();

            String cabecera[] = new String[]{"CANT.", "DESCRIPCION", "P. UNT."};
            PrintHelper.getInstance().printColumnsString(cabecera, width, align, true);

            PrintHelper.getInstance().printLineDashed();
            String detalle[] = new String[]{"1", infoPasaje.cpeDescripcionServicio, infoPasaje.cpeImporteTotal};
            PrintHelper.getInstance().printColumnsString(detalle, width, align, false);

            //Totales
            width = new int[]{3, 1};
            align = new int[]{2, 2};
            PrintHelper.getInstance().printLineDashed();
            String operacionesExoneradas[] = new String[]{"OP. EXONERADAS: "+ infoPasaje.cpeSimboloMoneda, infoPasaje.cpeTotalOperacionesExoneradas};
            String sumatoriaIGV[] = new String[]{"IGV " + infoPasaje.cpeTasaIgv+"%: "+ infoPasaje.cpeSimboloMoneda, infoPasaje.cpeSumatoriaIgv};
            String importeTotal[] = new String[]{"IMPORTE TOTAL: "+ infoPasaje.cpeSimboloMoneda, infoPasaje.cpeImporteTotal};
            PrintHelper.getInstance().printColumnsString(operacionesExoneradas, width, align, false);
            PrintHelper.getInstance().printColumnsString(sumatoriaIGV, width, align, false);
            PrintHelper.getInstance().printColumnsString(importeTotal, width, align, true);

            //Codigo Qr
            PrintHelper.getInstance().printLineDashed();
            PrintHelper.getInstance().setAlign(1);
            PrintHelper.getInstance().printQr(infoPasaje.cpeResumenQr, print_size, error_level);
            PrintHelper.getInstance().printLineDashed();
            PrintHelper.getInstance().printText(infoPasaje.cpeUrlConsulta + "\n", 22, false, false);
            PrintHelper.getInstance().feedPaper();

        } else {
            Toast.makeText(this, "Error de impresora", Toast.LENGTH_SHORT).show();
        }
    }
}