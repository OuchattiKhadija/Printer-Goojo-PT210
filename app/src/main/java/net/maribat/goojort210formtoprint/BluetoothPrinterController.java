package net.maribat.goojort210formtoprint;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.android.print.sdk.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothPrinterController {
    BluetoothAdapter bluetoothAdapter;
    Context mContext;
    Handler mHandler;
    boolean hasRegDisconnectReceiver;
    IntentFilter filter;
    private PrinterInstance mPrinter;

    BluetoothDevice mDevice;

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    Activity mActivity;


    public BluetoothPrinterController(Context context, Activity activity, Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        mHandler = handler;
        mActivity = activity;
        hasRegDisconnectReceiver = false;

        filter = new IntentFilter();
        //filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        //filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    }


    public void connectToPrt(){
        List<String> pairedPrintersName = new ArrayList<>();
        if ( getListOfPairedPrinters().size() == 1 ){
            mDevice = getListOfPairedPrinters().get(0);
            openPrinter();
        }else{
            for (BluetoothDevice device : getListOfPairedPrinters()){
                pairedPrintersName.add(device.getName()
                        + "\n" + device.getAddress());
            }

            chooseDevice(pairedPrintersName);
        }
    }

    private void chooseDevice(List<String> pairedPrinters) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        View rowList = mActivity.getLayoutInflater().inflate(R.layout.row, null);
        listView = rowList.findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, pairedPrinters);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        alertDialog.setView(rowList);

        AlertDialog dialog = alertDialog.create();
        listView.setOnItemClickListener( (adapterView, view, i, l) -> {
            String adresse = pairedPrinters.get(i).substring(pairedPrinters.get(i).length() - 17);
            Toast.makeText(mContext,
                    adresse,
                    Toast.LENGTH_SHORT).show();
            mDevice = bluetoothAdapter.getRemoteDevice(adresse);
            openPrinter();
            dialog.dismiss();
        });

        dialog.show();
    }

    public List<BluetoothDevice> getListOfPairedPrinters() {
        List<BluetoothDevice> paired_printers = new ArrayList<>();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if (device.getBluetoothClass().getMajorDeviceClass() == 1536) {
                    paired_printers.add(device);
                }
            }
        }

        Log.i("TAG", "connectToPrinter: " + paired_printers.size());

        return paired_printers;

    }

    private void openPrinter() {
        mPrinter = new PrinterInstance(mContext, mDevice, mHandler);
        // default is gbk...
        // mPrinter.setEncoding("gbk");
        mPrinter.openConnection();
    }

    public PrinterInstance getPrinter() {
        if (mPrinter != null && mPrinter.isConnected()) {
            if(!hasRegDisconnectReceiver){
                mContext.registerReceiver(myReceiver, filter);
                hasRegDisconnectReceiver = true;
            }
        }
        return mPrinter;
    }



    // receive the state change of the bluetooth.
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            Log.i("TAG", "receiver is: " + action);
            if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                if (device != null && mPrinter != null && mPrinter.isConnected() && device.equals(mDevice)) {
                    close();
                }
            }
        }
    };


    public void close() {
        if (mPrinter != null) {
            mPrinter.closeConnection();
            mPrinter = null;
        }
        if(hasRegDisconnectReceiver){
            mContext.unregisterReceiver(myReceiver);
            hasRegDisconnectReceiver = false;
        }
    }




}
