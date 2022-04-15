package com.gaspersoft.businvoice.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.gaspersoft.businvoice.R;
import com.gaspersoft.businvoice.dialogos.BarcodeDialog;

import java.util.regex.Pattern;

public class VerificarBoletoActivity extends AppCompatActivity implements BarcodeDialog.OnScanListener {

    private ImageView imgScan;
    private Button btnConfirmaBoleto;
    private EditText txtSerieBoleto;
    private EditText txtNumeroBoleto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_boleto);

        imgScan = findViewById(R.id.imgScan);
        btnConfirmaBoleto = findViewById(R.id.btnConfirmaBoleto);
        txtSerieBoleto = findViewById(R.id.txtSerieBoleto);
        txtNumeroBoleto = findViewById(R.id.txtNumeroBoleto);

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
    }

    private void showScan() {
        BarcodeDialog barCode = new BarcodeDialog();
        barCode.show(this.getSupportFragmentManager(), "bus");
    }

    @Override
    public void OnLeerBarCode(String barCodeData) {
        String separador = Pattern.quote("|");
        String[] parts = barCodeData.split(separador);
        txtSerieBoleto.setText("");
        txtNumeroBoleto.setText("");

        if (parts.length >= 9) {
            String serie = parts[2].trim();
            String numero = parts[3].trim();

            if (serie.length() == 4) {
                txtSerieBoleto.setText(serie);
                txtNumeroBoleto.setText(numero);
            }
        }
    }
}
