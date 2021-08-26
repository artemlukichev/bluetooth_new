package com.example.ble_app.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;


import com.example.ble_app.MainActivity;

import java.util.UUID;

public class Utils extends Thread {

//    public static final UUID UUID_S_BATTERY = UUID.fromString ("0000180f-0000-1000-8000-00805f9b34fb"); // Батарея
//    public static final UUID UUID_S_BATTERY_C_LEVEL = UUID.fromString ("00002a19-0000-1000-8000-00805f9b34fb"); // Уровень заряда батареи

//    public static final UUID UUID_S_DEVICEINFO = UUID.fromString ("0000180a-0000-1000-8000-00805f9b34fb"); // Информация об устройстве
//    public static final UUID UUID_S_DEVICEINFO_C_FIRMWARE = UUID.fromString ("00002a28-0000-1000-8000-00805f9b34fb"); // Прошивка

    public static final UUID UUID_S_EXTRA = UUID.fromString ("0000ff00-0000-1000-8000-00805f9b34fb"); // Расширенная служба
    public static final UUID UUID_S_EXTRA_C = UUID.fromString ("0000fff0-0000-1000-8000-00805f9b34fb"); // Настройки устройства

    public static final UUID UUID_TICD = UUID.fromString ("0x1523"); // UUID службы (из протокола)
    public static final UUID UUID_TCID_CH = UUID.fromString ("0x1524"); // UUID службы (из протокола: запись/уведомления)

    public static final byte[] VALUE_CLOCK_TIME = {(byte) 0x23, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; //READ DEVICE CLOCK TIME
    public static final byte[] VALUE_MODEL = {(byte) 0x24, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; //READ DEVICE MODEL
    public static final byte[] VALUE_READ_STORAGE_DATA_P1 = {(byte) 0x25, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; //READ STORAGE DATA (TIME)
    public static final byte[] VALUE_READ_STORAGE_DATA_P2 = {(byte) 0x26, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; //READ STORAGE DATA (RESULT)
    public static final byte[] VALUE_SERIAL_NUMBER_P1 = {(byte) 0x27, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; //READ DEVICE SERIAL NUMBER (PART 1)
    public static final byte[] VALUE_SERIAL_NUMBER_P2 = {(byte) 0x28, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; //READ DEVICE SERIAL NUMBER (PART 2)
    public static final byte[] VALUE_STORAGE_NUM_OF_DATA = {(byte) 0x2B, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    public static final byte[] VALUE_DEVICE_CLOCK_TIME = {(byte) 0x33, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; //WRITE DEVICE CLOCK TIME
    public static final byte[] VALUE_START_MEASUREMENT = {(byte) 0x41, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; //START AN INFRA-RED TEMPERATURE M.
    public static final byte[] VALUE_TURN_OFF = {(byte) 0x50, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; // TURN OFF THE DEVICE
    public static final byte[] VALUE_CLEAR_DEL_MEMORY = {(byte) 0x52, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; //CLEAR/DELETE ALL MEMORY
    public static final byte[] VALUE_NOTIFICATION= {(byte) 0x54, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; //NOTIFICATION FOR ENTERING COMMUNICATION MODE


    public final static int REQUEST_ENABLE_BT = 2001;
    private final Context mActivity;
    private final BluetoothAdapter mBluetoothAdapter;

    public Utils (final Context activity) {
        mActivity = activity;
        final BluetoothManager btManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = btManager.getAdapter();
    }

    public void askUserToEnableBluetoothIfNeeded() {
        if (isBluetoothLeSupported() && (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())) {
            final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) mActivity).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public boolean isBluetoothLeSupported() {
        return mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public boolean isBluetoothOn() {
        if (mBluetoothAdapter == null) {
            return false;
        } else {
            return mBluetoothAdapter.isEnabled();
        }
    }


    public static BluetoothClass.Service getBleService() {
        return mActivity.mgetInstance().mBluetoothLeService;
    }


    public static boolean sendValueToBle(String mac, UUID serviceId, UUID characteristicId, byte[] value) {
        if (getBleService() == null) {
            Log.d ("BluetoothUtils", "Служба Bluetooth не работает");
            return false;
        }
        boolean result = false;
        BluetoothGattService mService = getBleService().getService(mac, serviceId);
        if (mService != null) {
            BluetoothGattCharacteristic mCharacteristic = mService.getCharacteristic(characteristicId);
            if (mCharacteristic != null) {
                mCharacteristic.setValue(value);
                Log.d("BluetoothUtils", Utils.byteArrayToHexString(value));
                result = getBleService().writeCharacteristic(mac, mCharacteristic);
            } else {
                Log.d ("BluetoothUtils", "Целевой канал - ПУСТОЙ");
            }
        }
        return result;
    }


    public static void readCharacteristic(String mac, UUID serviceId, UUID characteristicId) {
        if (getBleService() == null) {
            return;
        }
        BluetoothGattService mService = getBleService().getService(mac, serviceId);
        if (mService != null) {
            BluetoothGattCharacteristic mCharacteristic = mService.getCharacteristic(characteristicId);
            if (mCharacteristic != null) {
                Log.d ("BluetoothUtils", "Прочитать:" + characterId.toString ());
                getBleService().readCharacteristic(mac, mCharacteristic);
            } else {
                Log.d ("BluetoothUtils", "Целевой канал - ПУСТОЙ");
            }
        }
    }

    public static void readRemoteRssi(String mac) {
        if (getBleService() == null) {
            return;
        }
        BluetoothGatt gatt = getBleService().getGatt(mac);
        if (gatt != null) {
            gatt.readRemoteRssi();
        }
    }
}

}
