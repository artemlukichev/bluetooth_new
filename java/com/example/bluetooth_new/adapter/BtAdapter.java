package com.example.bluetooth_new.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bluetooth_new.R;

import java.util.ArrayList;
import java.util.List;

public class BtAdapter extends ArrayAdapter<ListItem> {
    public static final String DEF_ITEM_TYPE = "normal";  //сохраненные
    public  static final String TITLE_ITEM_TYPE = "title"; //разделение списка
    public  static final String DISCOVERY_ITEM_TYPE = "discovery"; //найденные
    private List<ListItem> mainList; // 4 name & mac
    private List<ViewHolder> listViewHolders; // 4 viewholder, чтобы убирать убирать чеки
    private SharedPreferences pref;
    private boolean isDiscoveryType = false; //для блокировки слушателя


    public BtAdapter(@NonNull Context context, int resource, List<ListItem> btList) {
        super(context, resource, btList);
        mainList = btList;
        listViewHolders = new ArrayList<>();
        pref = context.getSharedPreferences(BtConsts.MY_PREF,Context.MODE_PRIVATE); //4 zapisi
    }

    //свой шаблон (заполняю текствью)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //если в лист_итем придет type, то вызовется "найденные устройства", дефолт - уже подключенные у-ва
        switch (mainList.get(position).getItemType()){
            case TITLE_ITEM_TYPE: convertView = titleItem(convertView, parent);
                break;
            default: convertView = defaultItem(convertView,position,parent);
            break;
        }
        return convertView;
    }
    //4 zapisi
    private void savePref(int pos){
        SharedPreferences.Editor editor = pref.edit(); //открытие таблицы для записи
        //editor.putString(BtConsts.MAC_KEY, mainList.get(pos).getBtMac());
        editor.putString(BtConsts.MAC_KEY, mainList.get(pos).getBtDevice().getAddress());
        editor.apply(); //сохранение
    }

    //для скролла (для сохранения)
    static class ViewHolder{

        TextView tvBtName;
        CheckBox chBtSelected;
    }

    private View defaultItem(View convertView, int position, ViewGroup parent) {

        ViewHolder viewHolder;

        boolean hasViewHolder = false;
        if(convertView !=null) hasViewHolder =  (convertView.getTag() instanceof ViewHolder);

        //ссылки - как добраться до текствью и что-то записать
        if (convertView == null || !hasViewHolder) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_item, null, false);
            viewHolder.tvBtName = convertView.findViewById(R.id.tvBtName);
            viewHolder.chBtSelected = convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder); //сохраняем в виде Tag
            listViewHolders.add(viewHolder);

        } else {
            //если convertView не null, значит там что-то есть, берем это из Tag
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.chBtSelected.setChecked(false); //чтобы чекбокс убирался при подключении к другому у-ву

        }

        if(mainList.get(position).getItemType().equals(BtAdapter.DISCOVERY_ITEM_TYPE))
        { //Когда рисуются итемы, которые найдены с помощью дискавери, прячем чекБокс
            viewHolder.chBtSelected.setVisibility(View.GONE);
            isDiscoveryType = true;
        } else { //не дискавери итем, тогда чекбокс виден
            viewHolder.chBtSelected.setVisibility(View.VISIBLE);
            isDiscoveryType = false;
        }
        //viewHolder.tvBtName.setText(mainList.get(position).getBtName());
        viewHolder.tvBtName.setText(mainList.get(position).getBtDevice().getName());
        //прослушиваем нажатие (когда есть подключенные устройства)
        viewHolder.chBtSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isDiscoveryType) {
                    for (ViewHolder holder : listViewHolders) {     //прогоняем по списку и очищаем список
                        holder.chBtSelected.setChecked(false);
                    }
                    viewHolder.chBtSelected.setChecked(true);   //отмечен один
                    savePref(position);
                }
            }
        });

        //если элемент уже сохранен, то он отмечается галочкой
        /*if (pref.getString(BtConsts.MAC_KEY, "no bt selected").equals(mainList.get(position).getBtMac())) {
            viewHolder.chBtSelected.setChecked(true);*/
        if (pref.getString(BtConsts.MAC_KEY, "no bt selected").equals(mainList.get(position).getBtDevice().getAddress())) {
            viewHolder.chBtSelected.setChecked(true);
        }
        isDiscoveryType = false;
        return convertView;
        //viewHolder.chBtSelected.setChecked(true);


    }
    private View titleItem(View convertView, ViewGroup parent) {
        boolean hasViewHolder = false;
        if(convertView != null){
            hasViewHolder = (convertView.getTag() instanceof ViewHolder);
        }
        if (convertView == null || hasViewHolder) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_item_title, null, false);
        }
        return convertView;

    }


}
