package com.example.bluetooth_new.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReceiveThread extends  Thread {
    private BluetoothSocket socket; //через сокет открываем каналы для получения/отправки
    private InputStream inputS;
    private OutputStream outputS;
    private byte[] rBuffer; //для считывания

    //конструктор
    public ReceiveThread(BluetoothSocket socket) {
        this.socket = socket;
        try {
            inputS = socket.getInputStream(); //открываем
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        rBuffer = new byte[1000];
        while (true){
            try{
            int size = inputS.read(rBuffer); //считываем в массив
                String message = new String(rBuffer,0,size);
                Log.d("MyLog","Message: "+ message); //что получаем из устройства
            } catch (IOException e){ //если срыв соединения
                break;
            }
        }
    }
}
