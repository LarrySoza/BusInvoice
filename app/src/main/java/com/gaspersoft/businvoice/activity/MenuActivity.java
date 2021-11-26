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

    TextView txtLogin;
    Button btnEmitirBoleto;
    Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        txtLogin = findViewById(R.id.txtLogin);
        btnCerrarSesion = findViewById(R.id.btnCesarSesion);
        btnEmitirBoleto = findViewById(R.id.btnEmitirBoletos);

        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        String nombreUsuario=preferences.getString("usuario", "");

        if(!"".equals(nombreUsuario)) {
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
                finish();
                System.exit(0);
            }
        });

        btnEmitirBoleto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent frmEmitirBoleto = new Intent(getApplicationContext(), BoletoActivity.class);
                startActivity(frmEmitirBoleto); }
        });
    }
}