package com.gaspersoft.businvoice.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gaspersoft.businvoice.ClsGlobal;
import com.gaspersoft.businvoice.R;
import com.gaspersoft.businvoice.api.ApiClient;
import com.gaspersoft.businvoice.models.ProgramacionDto;
import com.gaspersoft.businvoice.models.InfoPasajeDto;
import com.gaspersoft.businvoice.models.OrigenDto;
import com.gaspersoft.businvoice.utils.PrintHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscarBoletoActivity extends AppCompatActivity {

    private ProgressBar waitControl;
    private Button btnReimprimir;
    private Button btnConsultar;
    private ListView lvMovimientos;
    private String tokenStr;
    private TextView lblInfoBoleto;
    private InfoPasajeDto selectedItem;
    private Spinner spOrigenes;
    private Spinner spDestinos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_boleto);

        //Inicializamos el servicio de impresion
        PrintHelper.getInstance().initSunmiPrinterService(this);

        waitControl = findViewById(R.id.waitControl);
        lvMovimientos = findViewById(R.id.lvMovimientos);
        btnReimprimir = findViewById(R.id.btnReimprimir);
        lblInfoBoleto = findViewById(R.id.lblInfoBoleto);
        spOrigenes = findViewById(R.id.spOrigenProgramacion);
        spDestinos = findViewById(R.id.spDestinoProgramacion);
        btnConsultar=findViewById(R.id.btnListarBoletos);

        btnReimprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItem != null) {
                    ClsGlobal.ImprimirBoletoViaje(getApplicationContext(), selectedItem);
                } else {
                    Toast.makeText(getApplicationContext(), "Seleccione boleto", Toast.LENGTH_SHORT).show();
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

        CargarOrigenes();

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ProgramacionDto programacionDto= (ProgramacionDto) spDestinos.getSelectedItem();
                    waitControl.setVisibility(View.VISIBLE);
                    btnConsultar.setEnabled(false);
                    GetVentasProgramacion(programacionDto.programacionId);
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String GetHeaderToken() {
        return "Bearer " + tokenStr;
    }

    private void GetVentasProgramacion(int programacionId) {
        ApiClient.GetService().GetVentasProgramacion(GetHeaderToken(), programacionId)
                .enqueue(new Callback<List<InfoPasajeDto>>() {
                    @Override
                    public void onResponse(Call<List<InfoPasajeDto>> call, Response<List<InfoPasajeDto>> response) {
                        waitControl.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            ArrayAdapter<InfoPasajeDto> adapterTiposDocumento = new ArrayAdapter<InfoPasajeDto>(getApplicationContext(), R.layout.list_item_ultimos_movimientos, response.body());
                            lvMovimientos.setAdapter(adapterTiposDocumento);

                            lvMovimientos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    selectedItem = (InfoPasajeDto) parent.getItemAtPosition(position);
                                    lblInfoBoleto.setText("Cpe : " + selectedItem.cpeNumeroDocumento);
                                }
                            });
                        }else {
                            Toast.makeText(getApplicationContext(), "Error:" + response.code(), Toast.LENGTH_SHORT).show();
                        }

                        waitControl.setVisibility(View.GONE);
                        btnConsultar.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<List<InfoPasajeDto>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();

                        waitControl.setVisibility(View.GONE);
                        btnConsultar.setEnabled(true);
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
}