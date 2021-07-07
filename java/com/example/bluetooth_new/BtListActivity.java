package com.example.bluetooth_new;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bluetooth_new.adapter.BtAdapter;
import com.example.bluetooth_new.adapter.ListItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BtListActivity extends AppCompatActivity {
    private ListView listView;
    private BtAdapter adapter;
    private BluetoothAdapter btAdapter;
    private List<ListItem> list;
    private boolean isBtPermissionGranted = false; //для разрешения
    private final int BT_REQUEST_PERM = 111; //для разрешения
    private boolean isDicovery = false;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_list);
        getBtPermission();
        init();

    }
    
    //фильтрация для результатов приемника, регистрация на события
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter f1 = new IntentFilter(BluetoothDevice.ACTION_FOUND); //когда находим устройство - интент в приемнике action-found
        IntentFilter f2 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED); //когда есть изменения,
        IntentFilter f3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //когда дискавери закончено
        registerReceiver(bReciever,f1);
        registerReceiver(bReciever,f2);

    }
    
    //Убираю регистрацию фильтров в паузе
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bReciever);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bt_list_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            if(isDicovery){  //нажимаем на кнопку, если сейчас поиск, прекращаем его и обновляем список

                btAdapter.cancelDiscovery();
                isDicovery = false;
                getPairedDevices(); //список подключенных устройств

            } else { //если не поиск, значит уже показан список подключенных устройств
                finish();
            }
        } else if ((item.getItemId() == R.id.id_search)) {
            if(isDicovery) return true; //если находимся в режиме дискавери, то не запускаем
            ab.setTitle(R.string.discovering);
            list.clear();
            ListItem itemTitle = new ListItem();
            itemTitle.setItemType(BtAdapter.TITLE_ITEM_TYPE); //передаем TITLE_ITEM_TYPE
            list.add(itemTitle);
            adapter.notifyDataSetChanged(); //обновление адаптера
            btAdapter.startDiscovery(); //поиск
            isDicovery = true;
        }

        return true;
    }
    //back to beginning
    private void init(){
        ab = getSupportActionBar();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        list = new ArrayList<>();
        ActionBar ab = getSupportActionBar();
        if (ab == null) return;
        ab.setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.listView);
        adapter = new BtAdapter(this, R.layout.bt_list_item, list);
        listView.setAdapter(adapter);
        getPairedDevices();
        onItemClickListenner();

    }
    //слушатель нажатия на элемент из списка (подключать устройства)
    private void onItemClickListenner(){

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListItem item = (ListItem)parent.getItemAtPosition(position);//каст от object в ListItem
                //проверка какого типа итем, если дискавери, то запускается createBond
                if(item.getItemType().equals(BtAdapter.DISCOVERY_ITEM_TYPE)){
                    item.getBtDevice().createBond(); //процесс связывания с устройством
                }
            }
        });

    }

    //список устройств, есть ли устройства, получаем девайсы
    private void getPairedDevices(){

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            list.clear(); //очищаем и заполняем
            for (BluetoothDevice device : pairedDevices) {
                ListItem item = new ListItem();
                item.setBtDevice(device);
               // item.setBtName(device.getName());  //Name
               // item.setBtMac(device.getAddress()); //Mac
                list.add(item); //передаем в item
            }
            adapter.notifyDataSetChanged(); //данные изменились
        }
    }
    //принимается результат разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        //если наш запрос
        if(requestCode == BT_REQUEST_PERM) {
         if(grantResults[0] == PackageManager.PERMISSION_GRANTED ){
             isBtPermissionGranted = true;
             Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show();
         }else{
             Toast.makeText(this, "Нет разрешения на поиск устройств", Toast.LENGTH_SHORT).show();
         }
        } else { //если не наш
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //разрешение пользователя
    private void getBtPermission(){
        //если нет разрешения
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},BT_REQUEST_PERM);
        } else {
            isBtPermissionGranted = true; //разрешение есть
        }

    }

    //Приемник (дискавери), результат
    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                ListItem item = new ListItem();
                item.setBtDevice(device);
                item.setItemType(BtAdapter.DISCOVERY_ITEM_TYPE); //найденные устройства
                list.add(item); //добавляется в список
                adapter.notifyDataSetChanged(); //обвновление
                //Toast.makeText(context, "Found device name: " + device.getName(), Toast.LENGTH_SHORT).show();
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){

                isDicovery = false;
                getPairedDevices(); //загружается список, что и был раньше
                ab.setTitle(R.string.app_name); //когда закончился поиск
            }
            //проверяем успешно ли подключено устройство
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); //получаем device
                if(device.getBondState() == BluetoothDevice.BOND_BONDED){ //если подключено
                 getPairedDevices(); //загружается список
                }
            }
        }
    };

}