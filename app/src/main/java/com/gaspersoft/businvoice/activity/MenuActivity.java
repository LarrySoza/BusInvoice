package com.gaspersoft.businvoice.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gaspersoft.businvoice.R;

public class MenuActivity extends AppCompatActivity {

    private TextView txtLogin;
    private Button btnEmitirBoletosProgramacion;
    private Button btnCerrarSesion;
    private Button btnReImprimir;
    private Button btnLiquidacion;
    private Button btnEmitirBoletosVentaAbierta;
    private Button btnExcesos;
    private Button btnVerificarBoleto;
    private String tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        txtLogin = findViewById(R.id.txtLogin);
        btnCerrarSesion = findViewById(R.id.btnCesarSesion);
        btnEmitirBoletosProgramacion = findViewById(R.id.btnEmitirBoletosProgramacion);
        btnReImprimir = findViewById(R.id.btnReImprimir);
        btnLiquidacion = findViewById(R.id.btnLiquidacion);
        btnEmitirBoletosVentaAbierta = findViewById(R.id.btnEmitirBoletosVentaAbierta);
        btnExcesos = findViewById(R.id.btnExcesos);
        btnVerificarBoleto = findViewById(R.id.btnVerificarBoleto);

        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        String nombreUsuario = preferences.getString("usuario", "");
        tipoUsuario = preferences.getString("tipo_usuario","");

        if (!"".equals(nombreUsuario)) {
            txtLogin.setText(nombreUsuario);
        }

        String tokenStr = preferences.getString("token", "");
        if ("".equals(tokenStr)) {
            Intent frmLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(frmLogin);
            finish();
        }

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor objEditor = preferences.edit();
                objEditor.putString("token", "");
                objEditor.commit();

                Intent frmLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(frmLogin);
                finish();
            }
        });

        btnEmitirBoletosProgramacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent frmEmitirBoleto = new Intent(getApplicationContext(), VentaProgramacionActivity.class);
                startActivity(frmEmitirBoleto);
            }
        });

        btnVerificarBoleto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent frmVerificarBoleto = new Intent(getApplicationContext(), VerificarBoletoActivity.class);
                startActivity(frmVerificarBoleto);
            }
        });

        btnEmitirBoletosVentaAbierta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent frmEmitirBoleto = new Intent(getApplicationContext(), VentaAbiertaActivity.class);
                startActivity(frmEmitirBoleto);
            }
        });

        btnExcesos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent frmExcesos = new Intent(getApplicationContext(), ExcesoActivity.class);
                startActivity(frmExcesos);
            }
        });

        btnReImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent frmUltimosMovimientos = new Intent(getApplicationContext(), BuscarBoletoActivity.class);
                startActivity(frmUltimosMovimientos);
            }
        });

        btnLiquidacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tipoUsuario.equals("ADMIN")) {
                    Intent frmLiquidacionAdmin = new Intent(getApplicationContext(), LiquidacionAdminActivity.class);
                    startActivity(frmLiquidacionAdmin);
                } else{
                    Intent frmLiquidacion = new Intent(getApplicationContext(), LiquidacionActivity.class);
                    startActivity(frmLiquidacion);
                }
            }
        });
    }
}