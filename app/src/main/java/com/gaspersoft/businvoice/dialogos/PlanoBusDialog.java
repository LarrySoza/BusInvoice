package com.gaspersoft.businvoice.dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.gaspersoft.businvoice.R;
import com.gaspersoft.businvoice.api.ApiClient;
import com.gaspersoft.businvoice.models.BusDto;
import com.gaspersoft.businvoice.models.BusItemDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanoBusDialog extends DialogFragment {

    private String tokenStr;

    private int programacionId;
    private int progitem;
    private Button btnPrimerPiso;
    private Button btnSegundoPiso;
    private List<Button> botones;
    private List<TableRow> filas;
    private ProgressBar waitControl;
    private ScrollView plano;
    private TextView txtInfoBus;
    private BusDto bus;
    private TableRow pisos;
    private View v;

    public interface OnSeleccionarAsientoListener {
        void OnSeleccionarAsiento(Integer asiento);

        void OnErrorPlanoBus(String mensaje);
    }

    private OnSeleccionarAsientoListener mSeleccionarAsientoListener;

    private String GetHeaderToken() {
        return "Bearer " + tokenStr;
    }

    public PlanoBusDialog(String token, int programacionId, int progitem) {
        this.programacionId = programacionId;
        this.progitem = progitem;
        this.tokenStr = token;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return crearBusDialog();
    }

    private AlertDialog crearBusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        v = inflater.inflate(R.layout.activity_plano_bus, null);

        btnPrimerPiso = v.findViewById(R.id.btnPrimerPiso);
        btnSegundoPiso = v.findViewById(R.id.btnSegundoPiso);
        waitControl = v.findViewById(R.id.waitControl);
        plano = v.findViewById(R.id.scrollBus);
        txtInfoBus = v.findViewById(R.id.txtInfoBus);
        pisos = v.findViewById(R.id.btnsPisos);


        btnPrimerPiso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPrimerPiso.setEnabled(false);
                btnSegundoPiso.setEnabled(true);
                MakePrimerPiso();
            }
        });

        btnSegundoPiso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPrimerPiso.setEnabled(true);
                btnSegundoPiso.setEnabled(false);
                MakeSegundoPiso();
            }
        });

        InitItems(v);

        waitControl.setVisibility(View.VISIBLE);
        pisos.setVisibility(View.GONE);
        plano.setVisibility(View.INVISIBLE);
        MakeBus();

        builder.setView(v);

        return builder.create();
    }

    private void MakePrimerPiso() {
        int i = 0;
        for (TableRow fila : filas) {
            if (i < bus.totalFilasPrimerPiso) {
                fila.setVisibility(View.VISIBLE);
            } else {
                fila.setVisibility(View.GONE);
            }
            i++;
        }

        for (Button b : botones) {
            String id = v.getResources().getResourceEntryName(b.getId());

            for (BusItemDto item : bus.itemsPrimerPiso) {
                if (item.GetId().equals(id)) {
                    if (item.tipo == 0) {
                        b.setText(item.asiento.toString());

                        if(item.estado==0) {
                            b.setEnabled(true);
                        }else {
                            b.setEnabled(false);
                        }

                        b.setVisibility(View.VISIBLE);
                    } else {
                        b.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    private void MakeSegundoPiso() {
        int i = 0;
        for (TableRow fila : filas) {
            if (i < bus.totalFilasSegundoPiso) {
                fila.setVisibility(View.VISIBLE);
            } else {
                fila.setVisibility(View.GONE);
            }
            i++;
        }
    }

    private void MakeBus() {
        ApiClient.GetService().GetMapaBus(GetHeaderToken(), programacionId, progitem)
                .enqueue(new Callback<BusDto>() {
                    @Override
                    public void onResponse(Call<BusDto> call, Response<BusDto> response) {
                        if (response.isSuccessful()) {
                            bus = response.body();
                            txtInfoBus.setText("BUS: " + bus.codigo + "/" + bus.placa);
                            if(bus.totalPisos>2) {
                                pisos.setVisibility(View.VISIBLE);
                                btnPrimerPiso.setEnabled(false);
                                btnSegundoPiso.setEnabled(true);
                            }
                            MakePrimerPiso();
                        } else {
                            mSeleccionarAsientoListener.OnErrorPlanoBus("Error al cargar bus: " + response.code());
                            dismiss();
                        }

                        waitControl.setVisibility(View.GONE);
                        plano.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<BusDto> call, Throwable t) {
                        waitControl.setVisibility(View.GONE);
                        plano.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void InitItems(View v) {
        botones = new ArrayList<>();
        botones.add(v.findViewById(R.id.btn1_1));
        botones.add(v.findViewById(R.id.btn1_2));
        botones.add(v.findViewById(R.id.btn1_3));
        botones.add(v.findViewById(R.id.btn1_4));
        botones.add(v.findViewById(R.id.btn1_5));
        botones.add(v.findViewById(R.id.btn2_1));
        botones.add(v.findViewById(R.id.btn2_2));
        botones.add(v.findViewById(R.id.btn2_3));
        botones.add(v.findViewById(R.id.btn2_4));
        botones.add(v.findViewById(R.id.btn2_5));
        botones.add(v.findViewById(R.id.btn3_1));
        botones.add(v.findViewById(R.id.btn3_2));
        botones.add(v.findViewById(R.id.btn3_3));
        botones.add(v.findViewById(R.id.btn3_4));
        botones.add(v.findViewById(R.id.btn3_5));
        botones.add(v.findViewById(R.id.btn4_1));
        botones.add(v.findViewById(R.id.btn4_2));
        botones.add(v.findViewById(R.id.btn4_3));
        botones.add(v.findViewById(R.id.btn4_4));
        botones.add(v.findViewById(R.id.btn4_5));
        botones.add(v.findViewById(R.id.btn5_1));
        botones.add(v.findViewById(R.id.btn5_2));
        botones.add(v.findViewById(R.id.btn5_3));
        botones.add(v.findViewById(R.id.btn5_4));
        botones.add(v.findViewById(R.id.btn5_5));
        botones.add(v.findViewById(R.id.btn6_1));
        botones.add(v.findViewById(R.id.btn6_2));
        botones.add(v.findViewById(R.id.btn6_3));
        botones.add(v.findViewById(R.id.btn6_4));
        botones.add(v.findViewById(R.id.btn6_5));
        botones.add(v.findViewById(R.id.btn7_1));
        botones.add(v.findViewById(R.id.btn7_2));
        botones.add(v.findViewById(R.id.btn7_3));
        botones.add(v.findViewById(R.id.btn7_4));
        botones.add(v.findViewById(R.id.btn7_5));
        botones.add(v.findViewById(R.id.btn8_1));
        botones.add(v.findViewById(R.id.btn8_2));
        botones.add(v.findViewById(R.id.btn8_3));
        botones.add(v.findViewById(R.id.btn8_4));
        botones.add(v.findViewById(R.id.btn8_5));
        botones.add(v.findViewById(R.id.btn9_1));
        botones.add(v.findViewById(R.id.btn9_2));
        botones.add(v.findViewById(R.id.btn9_3));
        botones.add(v.findViewById(R.id.btn9_4));
        botones.add(v.findViewById(R.id.btn9_5));
        botones.add(v.findViewById(R.id.btn10_1));
        botones.add(v.findViewById(R.id.btn10_2));
        botones.add(v.findViewById(R.id.btn10_3));
        botones.add(v.findViewById(R.id.btn10_4));
        botones.add(v.findViewById(R.id.btn10_5));
        botones.add(v.findViewById(R.id.btn11_1));
        botones.add(v.findViewById(R.id.btn11_2));
        botones.add(v.findViewById(R.id.btn11_3));
        botones.add(v.findViewById(R.id.btn11_4));
        botones.add(v.findViewById(R.id.btn11_5));
        botones.add(v.findViewById(R.id.btn12_1));
        botones.add(v.findViewById(R.id.btn12_2));
        botones.add(v.findViewById(R.id.btn12_3));
        botones.add(v.findViewById(R.id.btn12_4));
        botones.add(v.findViewById(R.id.btn12_5));
        botones.add(v.findViewById(R.id.btn13_1));
        botones.add(v.findViewById(R.id.btn13_2));
        botones.add(v.findViewById(R.id.btn13_3));
        botones.add(v.findViewById(R.id.btn13_4));
        botones.add(v.findViewById(R.id.btn13_5));
        botones.add(v.findViewById(R.id.btn14_1));
        botones.add(v.findViewById(R.id.btn14_2));
        botones.add(v.findViewById(R.id.btn14_3));
        botones.add(v.findViewById(R.id.btn14_4));
        botones.add(v.findViewById(R.id.btn14_5));
        botones.add(v.findViewById(R.id.btn15_1));
        botones.add(v.findViewById(R.id.btn15_2));
        botones.add(v.findViewById(R.id.btn15_3));
        botones.add(v.findViewById(R.id.btn15_4));
        botones.add(v.findViewById(R.id.btn15_5));
        botones.add(v.findViewById(R.id.btn16_1));
        botones.add(v.findViewById(R.id.btn16_2));
        botones.add(v.findViewById(R.id.btn16_3));
        botones.add(v.findViewById(R.id.btn16_4));
        botones.add(v.findViewById(R.id.btn16_5));
        botones.add(v.findViewById(R.id.btn17_1));
        botones.add(v.findViewById(R.id.btn17_2));
        botones.add(v.findViewById(R.id.btn17_3));
        botones.add(v.findViewById(R.id.btn17_4));
        botones.add(v.findViewById(R.id.btn17_5));
        botones.add(v.findViewById(R.id.btn18_1));
        botones.add(v.findViewById(R.id.btn18_2));
        botones.add(v.findViewById(R.id.btn18_3));
        botones.add(v.findViewById(R.id.btn18_4));
        botones.add(v.findViewById(R.id.btn18_5));

        filas=new ArrayList<>();
        filas.add(v.findViewById(R.id.fila1));
        filas.add(v.findViewById(R.id.fila2));
        filas.add(v.findViewById(R.id.fila3));
        filas.add(v.findViewById(R.id.fila4));
        filas.add(v.findViewById(R.id.fila5));
        filas.add(v.findViewById(R.id.fila6));
        filas.add(v.findViewById(R.id.fila7));
        filas.add(v.findViewById(R.id.fila8));
        filas.add(v.findViewById(R.id.fila9));
        filas.add(v.findViewById(R.id.fila10));
        filas.add(v.findViewById(R.id.fila11));
        filas.add(v.findViewById(R.id.fila12));
        filas.add(v.findViewById(R.id.fila13));
        filas.add(v.findViewById(R.id.fila14));
        filas.add(v.findViewById(R.id.fila15));
        filas.add(v.findViewById(R.id.fila16));
        filas.add(v.findViewById(R.id.fila17));
        filas.add(v.findViewById(R.id.fila18));

        for (Button b : botones) {
            b.setText("");
            b.setEnabled(false);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button b = (Button) v;

                    if (!"".equals(b.getText().toString())) {
                        mSeleccionarAsientoListener.OnSeleccionarAsiento(Integer.parseInt(b.getText().toString()));
                        dismiss();
                    }
                }
            });
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
