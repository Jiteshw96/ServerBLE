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

         private AppDatabase db;
         private Context context;
         ArrayList<Integer> msgColorList = new ArrayList();

       // ServerBleApplication serverBleApplication;
        List<BleDataFromClient> bleDataFromClients =  new ArrayList<BleDataFromClient>();

        public ListAdapter(Context c, List<BleDataFromClient> bleDataFromClients) {
            this.context = c;
            this.bleDataFromClients = bleDataFromClients;
            init();


        }

    private void init(){
        int[] colors = this.context.getResources().getIntArray(R.array.colors);
        db= (AppDatabase) AppDatabase.getAppDatabase(this.context);
        for (int i = 0; i < colors.length; i++) {
            msgColorList.add(colors[i]);
        }
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
            String macAddress = bleDataFromClients.get(position).getDevicemacaddress();
            nameText.setText(bleDataFromClients.get(position).getDevicename());
            messageText.setText(bleDataFromClients.get(position).getMessage());
            addressText.setText(macAddress);
            String colorCode =  db.userDao().getDeviceAgainstMacAddress(macAddress).getColor();
            int color =msgColorList.get(Integer.parseInt(colorCode));
            convertView.findViewById(R.id.msgList).setBackgroundColor(color);
            return  convertView;
        }
    }