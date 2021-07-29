package com.example.bluetooth_new.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.bluetooth_new.adapter.BtConsts;

public class BtConnection {
    private Context context; //доступ к памяти
    private SharedPreferences pref; //доступ к памяти (MAC)
    private BluetoothAdapter btAdapter; //для соединения
    private BluetoothDevice device; //информация, полученная с MAC
    private ConnectThread connectThread;

    public BtConnection(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(BtConsts.MY_PREF, Context.MODE_PRIVATE);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void connect(){
        String mac = pref.getString(BtConsts.MAC_KEY," "); //достаем то, что сохранилось
        if(!btAdapter.isEnabled() || mac.isEmpty()) return; //если адаптер не вкл или мак пустой, то ретерн
        device = btAdapter.getRemoteDevice(mac); //получаем устройство
        if(device == null) return;//если устройство не включено

        connectThread = new ConnectThread(btAdapter, device); //создаем поток
        connectThread.start();


    }
}
