package com.gaspersoft.businvoice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class BusFragment extends DialogFragment {

    private Button btnPrimerPiso;
    private Button btnSegundoPiso;

    public interface OnSeleccionarAsientoListener {
        void OnSeleccionarAsiento(Integer asiento);
    }

    private OnSeleccionarAsientoListener mSeleccionarAsientoListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return crearBusDialog();
    }

    private AlertDialog crearBusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.activity_plano_bus, null);

        btnPrimerPiso = v.findViewById(R.id.btnPrimerPiso);
        btnSegundoPiso = v.findViewById(R.id.btnSegundoPiso);

        btnPrimerPiso.setText("HOLA");

        builder.setView(v);

        return builder.create();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1_1:
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mSeleccionarAsientoListener = (OnSeleccionarAsientoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() +
                            "no implementó OnSeleccionarAsientoListener");
        }
    }
}
