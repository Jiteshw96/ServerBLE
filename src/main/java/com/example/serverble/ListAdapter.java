package com.example.serverble;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class ListAdapter extends BaseAdapter {

        Context context;
        List<BleDataFromClient> bleDataFromClients =  new ArrayList<BleDataFromClient>();

        public ListAdapter(Context c, List<BleDataFromClient> bleDataFromClients) {
            this.context = c;
            this.bleDataFromClients = bleDataFromClients;
        }

    @Override
    public int getCount() {
        return bleDataFromClients.size();
    }

    @Override
    public Object getItem(int position) {
        return bleDataFromClients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater.from(context));
                convertView = inflater.inflate(R.layout.row,parent,false);
            }

            TextView nameText = convertView.findViewById(R.id.name);
            TextView messageText = convertView.findViewById(R.id.message);
            TextView addressText = convertView.findViewById(R.id.address);

            nameText.setText(bleDataFromClients.get(position).getDevicename());
            messageText.setText(bleDataFromClients.get(position).getMessage());
            addressText.setText(bleDataFromClients.get(position).getDevicemacaddress());

            return  convertView;
        }
    }