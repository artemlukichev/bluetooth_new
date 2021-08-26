package com.example.ble_app.Bluetooth;

import static com.example.ble_app.adapter.BtConsts.MAC_KEY;

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
        byte[] value = Utils.VALUE_CLOCK_TIME;
        Utils.sendValueToBle(MAC_KEY, Utils.UUID_TICD, Utils.UUID_TCID_CH, value);
        rBuffer = new byte[1000];
        while (true){
            try{
                int size = inputS.read(rBuffer); //считываем в массив
                String message = new String(rBuffer,0,size);
                onDataRceived(value);
                Log.d("MyLog","Message: "+ message); //что получаем из устройства
            } catch (IOException e){ //если срыв соединения
                break;
            }
        }

    }
    protected void onDataRceived(byte [] array)
    {
        if (5 == array.length && 0x23 == array[0])
        {
            byte first = array[1];
            byte second = array[2];

            int y = (first  & 0xFF) >> 1;
            int m1 = (first & 0x1) << 3;
            int m2 = (second & 0xFF) >> 5;
            int m = m1 + m2;
            int d = second & 0x1f;
        }
    }
}
