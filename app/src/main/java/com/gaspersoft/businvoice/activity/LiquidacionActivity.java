package com.gaspersoft.businvoice.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gaspersoft.businvoice.utils.PrintHelper;
import com.gaspersoft.businvoice.utils.*;
import com.gaspersoft.businvoice.ClsGlobal;
import com.gaspersoft.businvoice.DatePickerFragment;
import com.gaspersoft.businvoice.R;
import com.gaspersoft.businvoice.api.ApiClient;
import com.gaspersoft.businvoice.models.DestinoDto;
import com.gaspersoft.businvoice.models.LiquidacionDto;
import com.gaspersoft.businvoice.utils.PrintHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiquidacionActivity extends AppCompatActivity {

    private EditText txtFecha;
    private TextView txtInfoLiquidacion;
    private Button btnConsultar;
    private Button btnImprimir;
    private ProgressBar waitControl;
    private LiquidacionDto liquidacionDto;

    private String tokenStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquidacion);

        //Inicializamos el servicio de impresion
        PrintHelper.getInstance().initSunmiPrinterService(this);

        txtFecha = findViewById(R.id.txtFecha);
        btnConsultar = findViewById(R.id.btnConsultarLiquidacion);
        txtInfoLiquidacion = findViewById(R.id.txtInfoLiquidacion);
        btnImprimir = findViewById(R.id.btnImprimirLiquidacion);
        waitControl = findViewById(R.id.waitControl);

        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);

        tokenStr = preferences.getString("token", "");
        if ("".equals(tokenStr)) {
            Intent frmLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(frmLogin);
            finish();
        }

        btnImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liquidacionDto != null) {
                    ImprimirLiquidacion(liquidacionDto);
                } else {
                    Toast.makeText(getApplicationContext(), "Seleccione boleto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtFecha.setError(null);

                if (!"".equals(txtFecha.getText().toString())) {
                    btnConsultar.setEnabled(false);
                    waitControl.setVisibility(View.VISIBLE);
                    ConsultarLiquidacion(txtFecha.getText().toString());
                } else {
                    txtFecha.setError("Ingrese fecha");
                }
            }
        });

        txtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void ImprimirLiquidacion(LiquidacionDto liquidacionDto) {
        if(PrintHelper.getInstance().sunmiPrinter==PrintHelper.NoSunmiPrinter) {
            Toast.makeText(this, "Impresora no disponible", Toast.LENGTH_LONG).show();
            return;
        }

        if (!BluetoothUtil.isBlueToothPrinter) {


        } else {
            Toast.makeText(this, "Error de impresora", Toast.LENGTH_SHORT).show();
        }
    }

    private String GetHeaderToken() {
        return "Bearer " + tokenStr;
    }

    public void ConsultarLiquidacion(String fecha) {
        ApiClient.GetService().GetLiquidacion(GetHeaderToken(), fecha)
                .enqueue(new Callback<LiquidacionDto>() {
                    @Override
                    public void onResponse(Call<LiquidacionDto> call, Response<LiquidacionDto> response) {
                        try {
                            if (response.isSuccessful()) {
                                liquidacionDto = response.body();

                                String liquidacionStr =
                                        "FECHA DOCUMENTOS -> " + liquidacionDto.fechaStr + "\n" +
                                                "USUARIO -> " + liquidacionDto.usuario + "\n" +
                                                "FACTURAS -> " + liquidacionDto.importeTotalFacturas + "\n" +
                                                "BOLETAS -> " + liquidacionDto.importeTotalFacturas + "\n" +
                                                "---------------------" + "\n" +
                                                "TOTAL VENTAS -> " + liquidacionDto.importeTotal;

                                txtInfoLiquidacion.setText(liquidacionStr);

                                if(liquidacionDto.importeTotal>0) {
                                    btnImprimir.setEnabled(true);
                                }else {
                                    btnImprimir.setEnabled(false);
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "El servidor devolvio codigo " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        btnConsultar.setEnabled(true);
                        waitControl.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<LiquidacionDto> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
                        btnConsultar.setEnabled(true);
                        waitControl.setVisibility(View.GONE);
                    }
                });
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = year + "-" + (month + 1) + "-" + day;
                txtFecha.setText(selectedDate);
            }
        });

        newFragment.show(this.getSupportFragmentManager(), "datePicker");
    }
}