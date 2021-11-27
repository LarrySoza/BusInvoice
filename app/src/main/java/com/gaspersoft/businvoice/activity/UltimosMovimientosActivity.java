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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gaspersoft.businvoice.ClsGlobal;
import com.gaspersoft.businvoice.R;
import com.gaspersoft.businvoice.api.ApiClient;
import com.gaspersoft.businvoice.models.InfoPasajeDto;
import com.gaspersoft.businvoice.models.TipoDocumentoDto;
import com.gaspersoft.businvoice.utils.PrintHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UltimosMovimientosActivity extends AppCompatActivity {

    private ProgressBar waitControl;
    private Button btnReimprimir;
    private ListView lvMovimientos;
    private String tokenStr;
    private TextView lblTitulo;
    private InfoPasajeDto selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultimos_movimientos);

        //Inicializamos el servicio de impresion
        PrintHelper.getInstance().initSunmiPrinterService(this);

        waitControl = findViewById(R.id.waitControl);
        lvMovimientos = findViewById(R.id.lvMovimientos);
        btnReimprimir=findViewById(R.id.btnReimprimir);
        lblTitulo=findViewById(R.id.lblTitulo);

        btnReimprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedItem!=null) {
                    ClsGlobal.ImprimirCpe(getApplicationContext(),selectedItem);
                }
            }
        });
        waitControl.setVisibility(View.VISIBLE);

        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);

        tokenStr = preferences.getString("token", "");
        if ("".equals(tokenStr)) {
            Intent frmLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(frmLogin);
            finish();
        }

        CargarUltimosMovimientos();
    }

    private String GetHeaderToken() {
        return "Bearer " + tokenStr;
    }

    private void CargarUltimosMovimientos() {
        ApiClient.GetService().Listar10Ultimos(GetHeaderToken())
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
                                lblTitulo.setText("Cpe : " + selectedItem.cpeNumeroDocumento);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<List<InfoPasajeDto>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
                }
            });
    }
}