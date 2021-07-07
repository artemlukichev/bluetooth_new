package com.example.bluetooth_new;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bluetooth_new.Bluetooth.BtConnection;
import com.example.bluetooth_new.adapter.BtConsts;

public class MainActivity extends AppCompatActivity {

    private MenuItem menuItem;
    private BluetoothAdapter btAdapter;
    private final int ENABLE_REQUEST = 15;
    private SharedPreferences pref;
    private BtConnection btConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    //в меню создаем рразметку
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        menuItem = menu.findItem(R.id.id_bt_button);
        setBtIcon();

        return super.onCreateOptionsMenu(menu);
    }

    //прослушиваем нажатие кнопки
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.id_bt_button){

            if(!btAdapter.isEnabled()){

                enableBt();

            } else {

                btAdapter.disable();
                menuItem.setIcon(R.drawable.ic_bt_enable);
            }
        } else if(item.getItemId() == R.id.id_menu){

            if(btAdapter.isEnabled()) {
                Intent i = new Intent(MainActivity.this, BtListActivity.class);
                startActivity(i);
            } else { //если бт не вкл
                Toast.makeText(this,"Включите блютуз",Toast.LENGTH_SHORT).show();
            }
        } else if(item.getItemId() == R.id.id_connect) {
            btConnection.connect(); //при нажатии на иконку подключения, подключаем
        }

        return super.onOptionsItemSelected(item);

    }

    //ловим ответ
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ENABLE_REQUEST){

            //проверка ответа на подключение
            if(resultCode == RESULT_OK){

                setBtIcon();

            }
        }
    }

    private void setBtIcon(){
        if(btAdapter.isEnabled()){
            menuItem.setIcon(R.drawable.ic_bt_disable);
        }
        else{
            menuItem.setIcon(R.drawable.ic_bt_enable);
        }
    }

    private void init(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        pref = getSharedPreferences(BtConsts.MY_PREF, Context.MODE_PRIVATE);
        btConnection = new BtConnection(this); //для подключения

    }

    //включаем bt
    private void enableBt(){

        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(i, ENABLE_REQUEST);

    }
}