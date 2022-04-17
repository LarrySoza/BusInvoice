package com.gaspersoft.businvoice.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gaspersoft.businvoice.ClsGlobal;
import com.gaspersoft.businvoice.R;
import com.gaspersoft.businvoice.api.ApiClient;
import com.gaspersoft.businvoice.dialogos.BarcodeDialog;
import com.gaspersoft.businvoice.dialogos.InfoBoletoDialog;
import com.gaspersoft.businvoice.models.InfoExcesoDto;
import com.gaspersoft.businvoice.models.InfoPasajeDto;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificarBoletoActivity extends AppCompatActivity implements BarcodeDialog.OnScanListener,InfoBoletoDialog.OnCerrarListener {
    private String tokenStr;
    private ImageView imgScan;
    private Button btnConfirmaBoleto;
    private EditText txtSerieBoleto;
    private EditText txtNumeroBoleto;
    private ProgressBar waitControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_boleto);

        imgScan = findViewById(R.id.imgScan);
        btnConfirmaBoleto = findViewById(R.id.btnConfirmaBoleto);
        txtSerieBoleto = findViewById(R.id.txtSerieBoleto);
        txtNumeroBoleto = findViewById(R.id.txtNumeroBoleto);
        waitControl = findViewById(R.id.waitControl);

        imgScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showScan();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnConfirmaBoleto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean validado = true;

                String serie = txtSerieBoleto.getText().toString();
                String numero = txtNumeroBoleto.getText().toString();

                if (serie.length() != 4) {
                    txtSerieBoleto.setError("Serie no valida");
                    validado = false;
                } else {
                    if (!(serie.startsWith("F") || serie.startsWith("B"))) {
                        txtSerieBoleto.setError("Serie debe empezar con F ó B");
                        validado = false;
                    }
                }

                if (numero.length() == 0) {
                    txtNumeroBoleto.setError("Ingrese Numero");
                    validado = false;
                }

                if (validado) {
                    waitControl.setVisibility(View.VISIBLE);
                    btnConfirmaBoleto.setEnabled(false);

                    String id = serie + ClsGlobal.padLeftZeros(numero, 7);

                    ConfirmarBoleto(id);
                }
            }
        });

        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);

        tokenStr = preferences.getString("token", "");
        if ("".equals(tokenStr)) {
            Intent frmLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(frmLogin);
            finish();
        }
    }

    private void ConfirmarBoleto(String id) {
        ApiClient.GetService().ConfirmarVenta(GetHeaderToken(), id)
                .enqueue(new Callback<InfoPasajeDto>() {
                    @Override
                    public void onResponse(Call<InfoPasajeDto> call, Response<InfoPasajeDto> response) {

                        if (response.isSuccessful()) {
                            MostrarInfoPasaje(response.body());
                        } else {
                            if (response.code() == 404) {
                                Toast.makeText(getApplicationContext(), "No Existe boleto: " + id, Toast.LENGTH_SHORT).show();
                            }
                        }

                        waitControl.setVisibility(View.GONE);
                        btnConfirmaBoleto.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<InfoPasajeDto> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
                        waitControl.setVisibility(View.GONE);
                        btnConfirmaBoleto.setEnabled(true);
                    }
                });
    }

    private void MostrarInfoPasaje(InfoPasajeDto body) {
        InfoBoletoDialog infoBoleto = new InfoBoletoDialog(body);
        infoBoleto.show(this.getSupportFragmentManager(), "Info");
    }

    private String GetHeaderToken() {
        return "Bearer " + tokenStr;
    }

    private void showScan() {
        txtSerieBoleto.setText("");
        txtNumeroBoleto.setText("");
        BarcodeDialog barCode = new BarcodeDialog();
        barCode.show(this.getSupportFragmentManager(), "barCode");
    }

    @Override
    public void OnLeerBarCode(String barCodeData) {
        String separador = Pattern.quote("|");
        String[] parts = barCodeData.split(separador);
        Boolean validado = true;

        if (parts.length >= 9) {
            String serie = parts[2].trim();
            String numero = parts[3].trim();

            if (serie.length() != 4) {
                //txtSerieBoleto.setError("Serie no valida");
                validado = false;
            } else {
                if (!(serie.startsWith("F") || serie.startsWith("B"))) {
                    //txtSerieBoleto.setError("Serie debe empezar con F ó B");
                    validado = false;
                }
            }

            if (numero.length() == 0) {
                //txtNumeroBoleto.setError("Ingrese Numero");
                validado = false;
            }

            if (validado) {
                waitControl.setVisibility(View.VISIBLE);
                btnConfirmaBoleto.setEnabled(false);

                String id = serie + ClsGlobal.padLeftZeros(numero, 7);

                ConfirmarBoleto(id);
            }
        }
    }

    @Override
    public void OnCerrar() {
        txtSerieBoleto.setText("");
        txtNumeroBoleto.setText("");
    }
}