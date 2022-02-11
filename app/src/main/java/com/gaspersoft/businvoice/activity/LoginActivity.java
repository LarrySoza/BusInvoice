package com.gaspersoft.businvoice.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gaspersoft.businvoice.ClsGlobal;
import com.gaspersoft.businvoice.api.ApiClient;
import com.gaspersoft.businvoice.R;
import com.gaspersoft.businvoice.models.LoginDto;
import com.gaspersoft.businvoice.models.TokenDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private int totalClicCambioEmpresa = 5;
    static int contadorClic;
    private EditText txtUsuario;
    private EditText txtPassword;
    private Button btnLogin;
    private ProgressBar waitControl;
    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        contadorClic = 0;
        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        waitControl=findViewById(R.id.waitControl);
        imgLogo = findViewById(R.id.imgLogo);

        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        String instabus = preferences.getString("instabus", "");

        ClsGlobal.instabus = instabus;

        if (!instabus.equals("")) {
            Bitmap logo = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.instabus);
            imgLogo.setImageBitmap(logo);
        }

        String tokenStr = preferences.getString("token", "");
        String ultimousuario = preferences.getString("ultimo_usuario", "");
        if(!"".equals(tokenStr)) {
            Intent frmMenu = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(frmMenu);
            finish();
        }

        if(!"".equals(ultimousuario)) {
            txtUsuario.setText(ultimousuario);
        }

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contadorClic++;
                int clicFaltantes = totalClicCambioEmpresa - contadorClic;

                Toast.makeText(getApplicationContext(), "Faltan " + clicFaltantes + " clic para cambiar empresa", Toast.LENGTH_SHORT).show();

                if (clicFaltantes == 0) {
                    SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                    SharedPreferences.Editor objEditor = preferences.edit();
                    String empresa = preferences.getString("instabus", "");

                    if (empresa.equals("")) {
                        objEditor.putString("instabus", "1");
                        objEditor.commit();

                        ClsGlobal.instabus = "1";

                        Bitmap logo = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.instabus);
                        imgLogo.setImageBitmap(logo);
                    } else {
                        objEditor.putString("instabus", "");
                        objEditor.commit();

                        ClsGlobal.instabus = "";

                        Bitmap logo = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo);
                        imgLogo.setImageBitmap(logo);
                    }

                    contadorClic = 0;
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUsuario.setError(null);
                txtPassword.setError(null);
                boolean validado = true;

                if ("".equals(txtUsuario.getText().toString())) {
                    txtUsuario.setError("Ingrese nombre de usuario");
                    validado = false;
                }

                if ("".equals(txtPassword.getText().toString())) {
                    txtPassword.setError("Ingrese password");
                    validado = false;
                }

                if (validado) {
                    LoginDto login = new LoginDto();
                    login.usuario = txtUsuario.getText().toString().trim();
                    login.clave = txtPassword.getText().toString();
                    waitControl.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(false);
                    CallLogin(login);
                } else {
                    Toast.makeText(getApplicationContext(), "Corregir validaciones", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void CallLogin(LoginDto login) {
        ApiClient.GetService().ObtenerToken(login)
            .enqueue(new Callback<TokenDto>() {
            @Override
            public void onResponse(Call<TokenDto> call, Response<TokenDto> response) {
                try {
                    if (response.isSuccessful()) {
                        TokenDto token = response.body();

                        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                        SharedPreferences.Editor objEditor = preferences.edit();
                        objEditor.putString("token", token.access_token);
                        objEditor.putString("usuario", login.usuario.toUpperCase());
                        objEditor.putString("ultimo_usuario", login.usuario);
                        objEditor.putString("tipo_usuario", token.tipo_usuario);
                        objEditor.commit();

                        Intent frmMenu = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(frmMenu);
                        finish();
                    } else {
                        if (response.code() == 401) {
                            Toast.makeText(getApplicationContext(), "Acceso no autorizado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "El servidor devolvio codigo" + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                waitControl.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
            }

            @Override
            public void onFailure(Call<TokenDto> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error al consumir Api", Toast.LENGTH_SHORT).show();
                waitControl.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
            }
        });
    }

}