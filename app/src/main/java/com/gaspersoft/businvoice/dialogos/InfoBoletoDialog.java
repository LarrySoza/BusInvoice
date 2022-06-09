package com.gaspersoft.businvoice.dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.gaspersoft.businvoice.R;
import com.gaspersoft.businvoice.models.InfoPasajeDto;

public class InfoBoletoDialog extends DialogFragment {
    private View v;
    private EditText txtInfoOrigen;
    private EditText txtInfoDestino;
    private EditText txtInfoFechaViaje;
    private EditText txtInfoFechaEmision;
    private EditText txtInfoSupervisor;
    private EditText txtFechaSupervisor;
    private EditText txtInfoBusBoleto;
    private InfoPasajeDto infoPasaje;

    private TextView lblNumeroBoleto;
    private TextView lblInfoEstado;
    private TextView lblFechaSupervisor;
    private TextView lblInfoSupervisor;
    private Button btnAceptar;

    public interface OnCerrarListener {
        void OnCerrarInfo();
    }

    private OnCerrarListener mCerrarListener;

    public InfoBoletoDialog(InfoPasajeDto infoPasaje) {
        this.infoPasaje = infoPasaje;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return crearInfoBoletoDialog();
    }

    private Dialog crearInfoBoletoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        v = inflater.inflate(R.layout.activity_info_boleto, null);

        txtInfoOrigen = v.findViewById(R.id.txtInfoOrigen);
        txtInfoDestino = v.findViewById(R.id.txtInfoDestino);
        txtInfoFechaViaje = v.findViewById(R.id.txtInfoFechaViaje);
        txtInfoFechaEmision = v.findViewById(R.id.txtInfoFechaRegistro);
        txtInfoSupervisor = v.findViewById(R.id.txtInfoSupervisor);
        txtFechaSupervisor = v.findViewById(R.id.txtFechaSupervisor);
        txtInfoBusBoleto = v.findViewById(R.id.txtInfoBusBoleto);

        lblNumeroBoleto = v.findViewById(R.id.lblNumeroBoleto);
        lblInfoEstado = v.findViewById(R.id.lblInfoEstado);
        lblFechaSupervisor = v.findViewById(R.id.lblFechaSupervisor);
        lblInfoSupervisor = v.findViewById(R.id.lblInfoSupervisor);
        btnAceptar=v.findViewById(R.id.btnAceptar);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        MostrarDatos();
        builder.setView(v);
        return builder.create();
    }

    private void MostrarDatos() {
        //Muestros los datos
        txtInfoOrigen.setText(infoPasaje.pasajePuntoOrigen);
        txtInfoDestino.setText(infoPasaje.pasajePuntoLlegada);
        txtInfoFechaViaje.setText(infoPasaje.pasajeFechaViaje);
        txtInfoFechaEmision.setText(infoPasaje.cpeFechaRegistro);
        txtInfoSupervisor.setText(infoPasaje.supervisor);
        txtFechaSupervisor.setText(infoPasaje.fechaConfirmacion);
        lblNumeroBoleto.setText(infoPasaje.cpeNumeroDocumento);
        txtInfoBusBoleto.setText(infoPasaje.bus);

        if (txtInfoSupervisor.getText().length() > 0) {
            lblInfoEstado.setText("CONFIRMADO");
            lblInfoEstado.setTextColor(Color.parseColor("#F44336"));

            txtInfoSupervisor.setVisibility(View.VISIBLE);
            txtFechaSupervisor.setVisibility(View.VISIBLE);
            lblInfoSupervisor.setVisibility(View.VISIBLE);
            lblFechaSupervisor.setVisibility(View.VISIBLE);

        } else {
            lblInfoEstado.setText("SIN CONFIRMAR");
            lblInfoEstado.setTextColor(Color.parseColor("#4CAF50"));
            txtInfoSupervisor.setVisibility(View.GONE);
            txtFechaSupervisor.setVisibility(View.GONE);
            lblInfoSupervisor.setVisibility(View.GONE);
            lblFechaSupervisor.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mCerrarListener.OnCerrarInfo();
        super.onDismiss(dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCerrarListener = (OnCerrarListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() +
                            "no implementó OnScanListener");
        }
    }
}
