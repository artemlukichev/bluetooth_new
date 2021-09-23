package com.example.ble_app.Bluetooth;

import static com.example.ble_app.Bluetooth.Utils.VALUE_DEVICE_CLOCK_TIME;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.ble_app.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ReceiveThread extends  Thread {
    private BluetoothSocket socket; //через сокет открываем каналы для получения/отправки
    private InputStream inputS;
    private OutputStream outputS;
    private byte[] rBuffer; //для считывания
    public static MainActivity a; //тип активити и через нее сделать setText
    public static MainActivity b;


    //конструктор
    public ReceiveThread(BluetoothSocket socket) {
        this.socket = socket;
        try {
            inputS = socket.getInputStream(); //открываем
        } catch (IOException e) {
        }
        try {
            outputS = socket.getOutputStream(); //открываем
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        rBuffer = new byte[8];
        while (true){
            try{
                int size = inputS.read(rBuffer); //считываем в массив
                String message = new String(rBuffer,0,size);
                Log.d("MyLog","Message: "+ message); //что получаем из устройства
                onResRceived(message.getBytes()); //отрпавляю на развертку
                onDataRceived(message.getBytes());
                //a = onResRceived(message.getBytes());
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

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    protected void toonDataRceived (int [] array)
    {
        /*SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("dd");
        SimpleDateFormat simpleDateFormatMonth = new SimpleDateFormat("MM");
        SimpleDateFormat simpleDateFormatYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat simpleDateFormatMinute = new SimpleDateFormat("m");
        SimpleDateFormat simpleDateFormatHour = new SimpleDateFormat("k");

         */

        DateFormat dateFormatYear = new SimpleDateFormat("yy");
        Date year = new Date();

        DateFormat dateFormatDay = new SimpleDateFormat("dd");
        Date day = new Date();

        DateFormat dateFormatMonth = new SimpleDateFormat("MM");
        Date month = new Date();

        DateFormat dateFormatMinute = new SimpleDateFormat("m");
        Date minute = new Date();

        DateFormat dateFormatHour = new SimpleDateFormat("k");
        Date hour = new Date();

        int y = Integer.parseInt(dateFormatYear.format(year));
        int m = Integer.parseInt(dateFormatMonth.format(month));
        int d = Integer.parseInt(dateFormatDay.format(day));
        int h = Integer.parseInt(dateFormatHour.format(hour));
        int min = Integer.parseInt(dateFormatMinute.format(minute));

        if (5 == array.length && 0x33 == array[0])
        {

            /*
            byte first =  (y & 0xFF) << 1));
            byte second =  (y & 0xFF) >> 1);
             */

          /*Итого имеем, 2 байта распределены так: 7 бит на год, потом 4 бита на месяц, потом 5 бит на день.
            Значит пусть у нас есть переменные: int y - год, int m - месяц, int d - день тогда:
           */

            short data; // берем два байта
            data = (short) (y ); //
            data = (short) (data << 4);
            data = (short) (data | m);
            data = (short) (data << 5);
            data = (short) (data | d);

            Utils.VALUE_DEVICE_CLOCK_TIME = new byte[] {(byte) 0x33, (byte) (data >> 8), (byte) (data & 0xff), (byte) 0x00, (byte) 0x00};
        }
        }


    public void onResRceived(byte[] array) {
        int objectTemp = 0;
        int backgroundTemp = 0;
        if (5 == array.length && 0x26 == array[0]) {
            byte first = array[1];
            byte second = array[2];

            byte third = array[3];
            byte fourth = array[4];

            objectTemp = (first & 0xff + second & 0xff) / 10; //*0.1
            backgroundTemp = (third & 0xff + fourth & 0xff) / 10; //ссылку на активити
            //int a = {objectTemp, backgroundTemp};
            //setText!!

            a.textView3.setText(objectTemp);
            b.textView3.setText(objectTemp);
        }
    }


    public void sendMessage(byte[] byteArray){
        try{
            outputS.write(byteArray);
        }catch(IOException e){

        }
    }


}
