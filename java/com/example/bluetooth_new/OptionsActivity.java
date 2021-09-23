package com.example.ble_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.ble_app.Bluetooth.BtConnection;
import com.example.ble_app.Bluetooth.Utils;
import com.example.bluetooth_new.R;

public class OptionsActivity extends AppCompatActivity {
    private BtConnection btConnection;
    private Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5);
        b6 = findViewById(R.id.b6);
        b7 = findViewById(R.id.b7);
        b8 = findViewById(R.id.b8);
        b9 = findViewById(R.id.b9);
        b10 = findViewById(R.id.b10);
        b11 = findViewById(R.id.b11);
        b12 = findViewById(R.id.b12);

        //b1 = getView().findViewById(R.id.b1);

        setContentView(R.layout.activity_options);
        b1.setOnClickListener(v -> {
            btConnection.sendMessage(Utils.VALUE_CLOCK_TIME);
        });
        b2.setOnClickListener(v -> {
            btConnection.sendMessage(Utils.VALUE_MODEL);
        });
        b3.setOnClickListener(v -> {
            btConnection.sendMessage(Utils.VALUE_READ_STORAGE_DATA_P1);
        });
        b4.setOnClickListener(v -> {
            btConnection.sendMessage(Utils.VALUE_READ_STORAGE_DATA_P2);
        });
        b5.setOnClickListener(v -> {
            btConnection.sendMessage(Utils.VALUE_SERIAL_NUMBER_P1);
        });
        b6.setOnClickListener(v -> {
            btConnection.sendMessage(Utils.VALUE_SERIAL_NUMBER_P2 );
        });
        b7.setOnClickListener(v -> {
            btConnection.sendMessage(Utils.VALUE_STORAGE_NUM_OF_DATA);
        });
        b8.setOnClickListener(v -> {
            btConnection.sendMessage(Utils.VALUE_DEVICE_CLOCK_TIME);
        });
        b9.setOnClickListener(v -> {
            btConnection.sendMessage(Utils.VALUE_START_MEASUREMENT);
        });
        b10.setOnClickListener(v -> {
            btConnection.sendMessage(Utils.VALUE_TURN_OFF);
        });
        b11.setOnClickListener(v -> {
            btConnection.sendMessage(Utils.VALUE_CLEAR_DEL_MEMORY);
        });
        b12.setOnClickListener(v -> {
            btConnection.sendMessage(Utils.VALUE_NOTIFICATION);
        });

    }
}
