package com.example.serverble;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static com.example.serverble.ServerBleApplication.CHANNEL_ID;

public class ServerService extends Service {
    private List<BluetoothDevice> mDevices;
    private BluetoothGattServer mGattServer;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private AppDatabase db;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    ServerBleApplication serverBleApplication;
    public static UUID SERVICE_UUID = UUID.fromString("18902a9a-1f4a-44fe-936f-14c8eea41800");

    //public static String CHARACTERISTIC_STRING = "18902a9a-1f4a-44fe-936f-14c8eea41801";
    public static UUID CHARACTERISTIC_UUID = UUID.fromString("18902a9a-1f4a-44fe-936f-14c8eea41801");
    private int REQUEST_ENABLE_BT = 1;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override    public int onStartCommand(Intent intent, int flags, int startId) {

        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mDevices = new ArrayList<>();

        if (mBluetoothManager != null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }

        db= (AppDatabase) AppDatabase.getAppDatabase(this);
        GattServerCallback gattServerCallback = new GattServerCallback(getApplicationContext());
        mGattServer = mBluetoothManager.openGattServer(this, gattServerCallback);
        setupServer();
        startAdvertising();

      String str = intent.getStringExtra("inputExtra");
      Intent notificationIntent = new Intent(this,ServerActivity.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
      Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Server Service")
                .setContentText(str)
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1,notification);
        //stopSelf();
        //ContextCompat.startForegroundService(this,notification);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupServer() {
        /*BluetoothGattService service = new BluetoothGattService(SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        mGattServer.addService(service);*/
        BluetoothGattService service1 = new BluetoothGattService(SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic writeCharacteristic = new BluetoothGattCharacteristic(
                CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        service1.addCharacteristic(writeCharacteristic);

        mGattServer.addService(service1);
        mGattServer.getService(SERVICE_UUID);
    }

    private void stopServer() {
        if (mGattServer != null) {

            mGattServer.close();
        }
    }
    private void restartServer() {
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
        startActivity(discoverableIntent);
        stopServer();
        setupServer();
        startAdvertising();
    }

    private void startAdvertising() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(discoverableIntent);
        Log.i("discoverable", "Is Discoverable");
    }

    private void notifyCharacteristic(byte[] value, UUID uuid) {
        BluetoothGattService service = mGattServer.getService(SERVICE_UUID);
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid);
        Log.i("notify_char", "Notifying characteristic " + characteristic.getUuid().toString()
                + ", new value: " +  com.example.serverble.StringUtils.byteArrayInHexFormat(value));

        characteristic.setValue(value);
        boolean confirm = com.example.serverble.BluetoothUtils.requiresConfirmation(characteristic);
        for(BluetoothDevice device : mDevices) {
            mGattServer.notifyCharacteristicChanged(device, characteristic, confirm);
        }
    }

    public void sendResponse(BluetoothDevice device, int requestId, int status, int offset, byte[] value) {
        mGattServer.sendResponse(device, requestId, status, 0, null);
    }

    private void sendReverseMessage(byte[] message) {
        byte[] response =  ByteUtils.reverse(message);

        serverBleApplication = (ServerBleApplication) getApplicationContext();
        serverBleApplication.setClientMsg(com.example.serverble.StringUtils.stringFromBytes(message));
        String stringMessage = serverBleApplication.getClientMsg();
        String[] parts= stringMessage.split(" ");


        new addAll(parts[0],parts[1],parts[2],parts[3]).execute(db);
        String str = serverBleApplication.getServerMsg();

        Log.i("rev_send", "Sending: " +  com.example.serverble.StringUtils.byteArrayInHexFormat(response));
        notifyCharacteristicEcho(com.example.serverble.StringUtils.bytesFromString(stringMessage));

    }

    public void notifyCharacteristicEcho(byte[] value) {
        notifyCharacteristic(value, CHARACTERISTIC_UUID);
    }



    private class GattServerCallback extends BluetoothGattServerCallback {

        private Context context;
        GattServerCallback(Context context){

            this.context = context;

        }
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                addDevice(device);
                Log.i("Connect", "Connected to "+device);
                serverBleApplication = (ServerBleApplication) getApplicationContext();
                //serverBleApplication.setStatus("Connected");
                /*Intent intent = new Intent(ServerActivity.this, ChatActivity.class);
                startActivity(intent);*/
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                removeDevice(device);
                serverBleApplication.setStatus("Disconnected");

            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device,
                                                int requestId,
                                                int offset,
                                                BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);

            Log.i("char_read", "onCharacteristicReadRequest "
                    + characteristic.getUuid().toString());

            if ( com.example.serverble.BluetoothUtils.requiresResponse(characteristic)) {
                sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
            }
        }
        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device,
                                                 int requestId,
                                                 BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite,
                                                 boolean responseNeeded,
                                                 int offset,
                                                 byte[] value) {
            super.onCharacteristicWriteRequest(device,
                    requestId,
                    characteristic,
                    preparedWrite,
                    responseNeeded,
                    offset,
                    value);
            Log.i("char_write", "onCharacteristicWriteRequest" + characteristic.getUuid().toString()
                    + "\nReceived: " +  com.example.serverble.StringUtils.byteArrayInHexFormat(value) +" In String Format "+  com.example.serverble.StringUtils.stringFromBytes(value));


            //    Toast.makeText(getApplicationContext(),  com.example.serverble.StringUtils.stringFromBytes(value),Toast.LENGTH_LONG).show();
            if (CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
                sendReverseMessage(value);
            }
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            Log.i("send_notific", "onNotificationSent");
        }
    }

    public void addDevice(BluetoothDevice device)
    {
        mDevices.add(device);
    }

    public void removeDevice(BluetoothDevice device)
    {
        mDevices.remove(device);
    }


    class addAll extends AsyncTask<AppDatabase, Void, Void>{
  BleDataFromClient user = new BleDataFromClient();

             public addAll(String Time, String Message, String DeviceName, String DeviceMacAddress) { user.setDevicemacaddress(DeviceMacAddress);
                user.setDevicename(DeviceName);
                user.setTime(Time);
                user.setMessage(Message);
             }

                @Override
                protected Void doInBackground(AppDatabase... db) {
                    int count = db[0].userDao().countDeviceAgainstMacAddress(user.getDevicemacaddress());
                    if(count == 0){
                        BleDataDeviceInfo bleDevice=new BleDataDeviceInfo();
                        bleDevice.setDevicename("ABC");
                        int colorCount = db[0].userDao().countDevice();
                        if(colorCount >= 5){
                            int color = 6;
                        }
                        bleDevice.setColor(String.valueOf(colorCount));
                        bleDevice.setDevicemacaddress(user.getDevicemacaddress());
                        bleDevice.getDevicemacaddress();
                        db[0].userDao().insertDevice(bleDevice);
                    }
                 db[0].userDao().insertAll(user);
                 return null;
             }
    }




}
