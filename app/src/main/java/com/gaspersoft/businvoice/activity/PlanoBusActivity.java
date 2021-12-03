package com.gaspersoft.businvoice.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.DatePicker;

import com.gaspersoft.businvoice.R;

import java.util.Calendar;

public class PlanoBusActivity extends AppCompatActivity {

    private OnSeleccionarAsientoListener mSeleccionarAsientoListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plano_bus);
    }

    public interface OnSeleccionarAsientoListener {
        void OnSeleccionarAsiento(int asiento);
    }
}