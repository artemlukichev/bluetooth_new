package com.example.bluetooth_new.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {
    private Context context; //доступ к памяти
    private BluetoothAdapter btAdapter; //для соединения
    private BluetoothDevice device; //информация, полученная с MAC
    private BluetoothSocket mSocket;//сокет
    public static final String UUID = "0x180A";

    public ConnectThread(Context context, BluetoothAdapter btAdapter, BluetoothDevice device) {
        this.context = context;
        this.btAdapter = btAdapter;
        this.device = device;

        try {//cоздаем сокет
            mSocket = device.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
        } catch (IOException e) {

        }
    }
//запуск на второстепенном потоке (из-за connect и close)
    @Override
    public void run() {
        btAdapter.cancelDiscovery();
        try {
            mSocket.connect();  //подключение
            new ReceiveThread(mSocket).start();
            Log.d("MyLog", "Connected");
        } catch (IOException e) {
            Log.d("MyLog", "Not connected");
         try{
             mSocket.close();
         } catch (IOException e1){ //если ошибка, то закрываем

         }
        }
    }
    public void closeConnection(){  //для закрытия соединения
        try{
            mSocket.close();
        } catch (IOException e1){

        }
    }
}
