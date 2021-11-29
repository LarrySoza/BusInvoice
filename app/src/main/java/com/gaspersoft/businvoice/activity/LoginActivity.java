package com.gaspersoft.businvoice.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gaspersoft.businvoice.api.ApiClient;
import com.gaspersoft.businvoice.api.IApiService;
import com.gaspersoft.businvoice.ClsGlobal;
import com.gaspersoft.businvoice.R;
import com.gaspersoft.businvoice.models.LoginDto;
import com.gaspersoft.businvoice.models.TokenDto;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUsuario;
    private EditText txtPassword;
    private Button btnLogin;
    private ProgressBar waitControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        waitControl=findViewById(R.id.waitControl);

        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);

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