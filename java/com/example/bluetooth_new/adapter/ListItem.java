package com.example.bluetooth_new.adapter;

import android.bluetooth.BluetoothDevice;

public class ListItem {

    //private String btName;
    //private String btMac;
    private BluetoothDevice btDevice;
    private String itemType = BtAdapter.DEF_ITEM_TYPE;//для разделения списка на известные и найденные

    //для записи информации в адаптере
    /*public String getBtName() {
        return btName;
    }

    public void setBtName(String btName) {
        this.btName = btName;
    }

    public String getBtMac() {
        return btMac;
    }

    public void setBtMac(String btMac) {
        this.btMac = btMac;
    } */

    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    public void setBtDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

}
