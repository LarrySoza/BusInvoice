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
import android.widget.CheckBox;
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
import com.gaspersoft.businvoice.models.DestinoDto;
import com.gaspersoft.businvoice.models.ErrorDto;
import com.gaspersoft.businvoice.models.ExcesoDto;
import com.gaspersoft.businvoice.models.InfoExcesoDto;
import com.gaspersoft.businvoice.models.ProgramacionDto;
import com.gaspersoft.businvoice.models.DniDto;
import com.gaspersoft.businvoice.models.InfoPasajeDto;
import com.gaspersoft.businvoice.models.OrigenDto;
import com.gaspersoft.businvoice.models.RucDto;
import com.gaspersoft.businvoice.models.TipoDocumentoDto;
import com.gaspersoft.businvoice.utils.PrintHelper;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExcesoActivity extends AppCompatActivity {

    private String tokenStr;

    private Spinner spTipoDocumento;
    private EditText txtNumeroDocumento;
    private EditText txtNombrePasajero;
    private Spinner spOrigenes;
    private Spinner spDestinos;
    private EditText txtRuc;
    private EditText txtRazonSocial;
    private RadioButton optFactura;
    private RadioButton optBoleta;
    private RadioButton optPasaje;
    private Button btnConsultarDni;
    private Button btnConsultarRuc;
    private Button btnRegistrarBoleto;
    private ProgressBar waitControl;
    private CheckBox chkExceso;
    private EditText txtMontoExceso;
    private EditText txtDescripcionExceso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exceso);

        //Inicializamos el servicio de impresion
        PrintHelper.getInstance().initSunmiPrinterService(this);

        spTipoDocumento = findViewById(R.id.spTipoDocumentoPasajero);
        txtNumeroDocumento = findViewById(R.id.txtNumeroDocumento);
        txtNombrePasajero = findViewById(R.id.txtNombrePasajero);
        spOrigenes = findViewById(R.id.spOrigen);
        spDestinos = findViewById(R.id.spDestino);
        txtRuc = findViewById(R.id.txtRuc);
        txtRazonSocial = findViewById(R.id.txtRazonSocial);
        optBoleta = findViewById(R.id.optBoleta);
        optFactura = findViewById(R.id.optFactura);
        optPasaje = findViewById(R.id.optPasaje);
        btnConsultarDni = findViewById(R.id.btnConsultarDni);
        btnConsultarRuc = findViewById(R.id.btnConsultarRuc);
        btnRegistrarBoleto = findViewById(R.id.btnRegistrar);
        waitControl = findViewById(R.id.waitControl);
        chkExceso = findViewById(R.id.chkExceso);
        txtMontoExceso = findViewById(R.id.txtMontoExceso);
        txtDescripcionExceso = findViewById(R.id.txtDescripcionExceso);

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

        chkExceso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                txtMontoExceso.setEnabled(isChecked);
                txtDescripcionExceso.setEnabled(isChecked);
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
                txtMontoExceso.setError(null);
                txtDescripcionExceso.setError(null);

                TipoDocumentoDto tipoDocumentoDto = (TipoDocumentoDto) spTipoDocumento.getSelectedItem();
                String pasajeroNumeroDocumento = txtNumeroDocumento.getText().toString().trim();
                String pasajeroNombre = txtNombrePasajero.getText().toString();

                if (tipoDocumentoDto.id.equals("-")) {
                    pasajeroNumeroDocumento = "";
                    pasajeroNombre = "*CLIENTES VARIOS*";
                }

                OrigenDto origenDto = (OrigenDto) spOrigenes.getSelectedItem();
                DestinoDto destinoDto = (DestinoDto) spDestinos.getSelectedItem();
                String cpeTipoDocumentoId = "PA"; //por defecto es pasasajes
                String pasajeroRuc = "";
                String pasajeroRazonSocial = "";
                String importeExceso = txtMontoExceso.getText().toString();
                String descripcionExceso = txtDescripcionExceso.getText().toString();

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

                if (chkExceso.isChecked()) {
                    if (importeExceso.length() == 0) {
                        txtMontoExceso.setError("Ingrese precio");
                        validado = false;
                    } else {
                        if (Double.parseDouble(importeExceso) == 0) {
                            txtMontoExceso.setError("Ingrese un valor mayor a 0");
                            validado = false;
                        }
                    }

                    if (descripcionExceso.length() == 0) {
                        txtDescripcionExceso.setError("Ingrese Descripcion");
                        validado = false;
                    }
                }

                if (validado) {
                    waitControl.setVisibility(View.VISIBLE);
                    btnRegistrarBoleto.setEnabled(false);

                    ExcesoDto exceso = new ExcesoDto();
                    exceso.remitenteTipoDocumento = tipoDocumentoDto.id;
                    exceso.remitenteNumeroDocumento = pasajeroNumeroDocumento;
                    exceso.remitenteNombre = pasajeroNombre;
                    exceso.consignadoTipoDocumento = tipoDocumentoDto.id;
                    exceso.consignadoNumeroDocumento = pasajeroNumeroDocumento;
                    exceso.consignadoNombre = pasajeroNombre;

                    if (cpeTipoDocumentoId.equals("01")) {
                        exceso.remitenteTipoDocumento = "6";
                        exceso.remitenteNumeroDocumento = pasajeroRuc;
                        exceso.remitenteNombre = pasajeroRazonSocial;
                    }

                    exceso.origenId = origenDto.id;
                    exceso.destinoId = destinoDto.id;
                    exceso.cpeImporteTotal = Double.parseDouble(txtMontoExceso.getText().toString());
                    exceso.cpeTipoDocumento_id = cpeTipoDocumentoId;
                    exceso.cpeDescripcion = txtDescripcionExceso.getText().toString();
                    exceso.empresaId = ClsGlobal.getEmpresaId();

                    RegistrarExceso(exceso);
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
        optPasaje.setChecked(true);

        txtRuc.setText("");
        txtRazonSocial.setText("");
        btnConsultarDni.setEnabled(true);

        txtRuc.setEnabled(false);
        txtRazonSocial.setEnabled(false);
        btnConsultarRuc.setEnabled(false);

        spTipoDocumento.setSelection(0);
        txtNumeroDocumento.requestFocus();

        txtMontoExceso.setText("");
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

    public void RegistrarExceso(ExcesoDto exceso) {
        ApiClient.GetService().RegistrarExceso(GetHeaderToken(), exceso)
                .enqueue(new Callback<InfoExcesoDto>() {
                    @Override
                    public void onResponse(Call<InfoExcesoDto> call, Response<InfoExcesoDto> response) {
                        try {
                            if (response.isSuccessful()) {
                                ClsGlobal.ImprimirExceso(getApplicationContext(), response.body());
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
                    public void onFailure(Call<InfoExcesoDto> call, Throwable t) {
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

    public void CargarDestinos(String origenId) {
        ApiClient.GetService().ListarDestinos(GetHeaderToken(), origenId)
                .enqueue(new Callback<List<DestinoDto>>() {
                    @Override
                    public void onResponse(Call<List<DestinoDto>> call, Response<List<DestinoDto>> response) {
                        if (response.isSuccessful()) {
                            ArrayAdapter<DestinoDto> adapterDestinos = new ArrayAdapter<DestinoDto>(getApplicationContext(), R.layout.spinner_item, response.body());
                            spDestinos.setAdapter(adapterDestinos);
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

                        waitControl.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<List<DestinoDto>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
                        waitControl.setVisibility(View.GONE);
                    }
                });
    }
}