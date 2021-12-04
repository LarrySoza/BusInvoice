package com.gaspersoft.businvoice.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.appcompat.app.AppCompatActivity;

import com.gaspersoft.businvoice.dialogos.PlanoBusDialog;
import com.gaspersoft.businvoice.ClsGlobal;
import com.gaspersoft.businvoice.R;
import com.gaspersoft.businvoice.api.ApiClient;
import com.gaspersoft.businvoice.models.BoletoViajeDto;
import com.gaspersoft.businvoice.models.ErrorDto;
import com.gaspersoft.businvoice.models.ProgramacionDto;
import com.gaspersoft.businvoice.models.DniDto;
import com.gaspersoft.businvoice.models.InfoPasajeDto;
import com.gaspersoft.businvoice.models.OrigenDto;
import com.gaspersoft.businvoice.models.RucDto;
import com.gaspersoft.businvoice.models.TipoDocumentoDto;
import com.gaspersoft.businvoice.utils.PrintHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentaProgramacionActivity extends AppCompatActivity implements PlanoBusDialog.OnSeleccionarAsientoListener {
    private String tokenStr;

    private Spinner spTipoDocumento;
    private EditText txtNumeroDocumento;
    private EditText txtNombrePasajero;
    private Spinner spOrigenes;
    private Spinner spDestinos;
    private EditText txtTarifa;
    private EditText txtRuc;
    private EditText txtRazonSocial;
    private EditText txtNumeroAsiento;
    private RadioButton optFactura;
    private RadioButton optBoleta;
    private RadioButton optPasaje;
    private Button btnConsultarDni;
    private Button btnConsultarRuc;
    private Button btnRegistrarBoleto;
    private ProgressBar waitControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta_programacion);

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
        optPasaje = findViewById(R.id.optPasaje);
        btnConsultarDni = findViewById(R.id.btnConsultarDni);
        btnConsultarRuc = findViewById(R.id.btnConsultarRuc);
        btnRegistrarBoleto = findViewById(R.id.btnRegistrar);
        waitControl = findViewById(R.id.waitControl);
        txtNumeroAsiento = findViewById(R.id.txtNumeroAsiento);

        optBoleta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtRuc.setEnabled(false);
                    txtRazonSocial.setEnabled(false);
                    btnConsultarRuc.setEnabled(false);
                }
            }
        });

        optPasaje.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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
                        btnConsultarDni.setEnabled(false);
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
                        btnConsultarRuc.setEnabled(false);
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
                txtNumeroAsiento.setError(null);

                TipoDocumentoDto tipoDocumentoDto = (TipoDocumentoDto) spTipoDocumento.getSelectedItem();
                String pasajeroNumeroDocumento = txtNumeroDocumento.getText().toString().trim();
                String pasajeroNombre = txtNombrePasajero.getText().toString();

                if (tipoDocumentoDto.id.equals("-")) {
                    pasajeroNumeroDocumento = "";
                    pasajeroNombre = "*CLIENTES VARIOS*";
                }

                OrigenDto origenDto = (OrigenDto) spOrigenes.getSelectedItem();
                ProgramacionDto programacionDto = (ProgramacionDto) spDestinos.getSelectedItem();
                String cpeImporteTotal = txtTarifa.getText().toString();
                String asiento = txtNumeroAsiento.getText().toString();
                String cpeTipoDocumentoId = "PA"; //por defecto es pasasajes
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

                if (asiento.length() == 0) {
                    txtNumeroAsiento.setError("Ingrese numero de asiento");
                    validado = false;
                }

                if (optBoleta.isChecked()) {
                    cpeTipoDocumentoId = "03";
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
                    boleto.destinoId = programacionDto.id;
                    boleto.cpeImporteTotal = Double.parseDouble(cpeImporteTotal);
                    boleto.cpeTipoDocumentoId = cpeTipoDocumentoId;
                    boleto.pasajeroRuc = pasajeroRuc;
                    boleto.pasajeroRazonSocial = pasajeroRazonSocial;
                    boleto.empresaId = ClsGlobal.getEmpresaId();
                    boleto.progitem = programacionDto.progitem;
                    boleto.programacionId = programacionDto.programacionId;
                    boleto.asiento = Integer.parseInt(asiento);

                    waitControl.setVisibility(View.VISIBLE);
                    btnRegistrarBoleto.setEnabled(false);
                    RegistrarBoletoViaje(boleto);
                }
            }
        });

        txtNumeroAsiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ProgramacionDto programacionDto = (ProgramacionDto) spDestinos.getSelectedItem();
                    showBus(programacionDto.programacionId, programacionDto.progitem);
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void showBus(int programacionId, int progitem) {
        PlanoBusDialog bus = new PlanoBusDialog(tokenStr, programacionId, progitem);
        bus.show(this.getSupportFragmentManager(), "bus");
    }

    private void Limpiar() {
        txtNumeroDocumento.setText("");
        txtNombrePasajero.setText("");
        optPasaje.setChecked(true);
        txtRuc.setText("");
        txtRazonSocial.setText("");
        btnConsultarDni.setEnabled(true);

        txtRuc.setEnabled(false);
        txtRazonSocial.setEnabled(false);
        btnConsultarRuc.setEnabled(false);

        spTipoDocumento.setSelection(0);
        txtNumeroDocumento.requestFocus();

        //Asiento libre
        txtNumeroAsiento.setText("");
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

                                    Intent frmLogin = new Intent(getApplicationContext(), VentaProgramacionActivity.class);
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
                                ClsGlobal.ImprimirCpe(getApplicationContext(), response.body());
                                Limpiar();
                            } else {
                                if (response.errorBody() != null) {
                                    ErrorDto.ShowErrorDto(getApplicationContext(), response.errorBody().charStream());
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error al registrar pasaje", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        waitControl.setVisibility(View.GONE);
                        btnRegistrarBoleto.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<InfoPasajeDto> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
                        waitControl.setVisibility(View.GONE);
                        btnRegistrarBoleto.setEnabled(true);
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
                                        Date date = Calendar.getInstance().getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        String strDate = dateFormat.format(date);

                                        CargarProgramaciones(strDate, origen.id);
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

                                    Intent frmLogin = new Intent(getApplicationContext(), VentaProgramacionActivity.class);
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
                        btnConsultarDni.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<DniDto> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
                        waitControl.setVisibility(View.GONE);
                        btnConsultarDni.setEnabled(true);
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
                        btnConsultarRuc.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<RucDto> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
                        waitControl.setVisibility(View.GONE);
                        btnConsultarRuc.setEnabled(true);
                    }
                });
    }

    public void CargarProgramaciones(String fecha, String origenId) {
        ApiClient.GetService().ListarProgramaciones(GetHeaderToken(), ClsGlobal.getEmpresaId(), fecha, origenId)
                .enqueue(new Callback<List<ProgramacionDto>>() {
                    @Override
                    public void onResponse(Call<List<ProgramacionDto>> call, Response<List<ProgramacionDto>> response) {
                        try {
                            if (response.isSuccessful()) {
                                ArrayAdapter<ProgramacionDto> adapterDestinos = new ArrayAdapter<ProgramacionDto>(getApplicationContext(), R.layout.spinner_item, response.body());
                                spDestinos.setAdapter(adapterDestinos);
                                spDestinos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        try {
                                            ProgramacionDto destino = (ProgramacionDto) spDestinos.getSelectedItem();
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

                                    Intent frmLogin = new Intent(getApplicationContext(), VentaProgramacionActivity.class);
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
                    public void onFailure(Call<List<ProgramacionDto>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
                        waitControl.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void OnSeleccionarAsiento(Integer asiento) {
        txtNumeroAsiento.setText(asiento.toString());
        txtNumeroAsiento.setError(null);
    }

    @Override
    public void OnErrorPlanoBus(String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
    }
}