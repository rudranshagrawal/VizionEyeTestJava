package rudransh.agrawal.vizioneyetestjava;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    int RC_BLE = 1;
    // Bluetooth's variables
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;
    BluetoothManager bluetoothManager;
    BluetoothScanCallback bluetoothScanCallback;
    BluetoothGatt gattClient;

    BluetoothGattCharacteristic characteristicID; // To get Value

    // UUID's (set yours)
    final UUID SERVICE_UUID = UUID.fromString("ab0828b1-198e-4351-b779-901fa0e0371e");
    final UUID CHARACTERISTIC_UUID_ID = UUID.fromString("1a220d0a-6b06-4767-8692-243153d94d85");
    final UUID DESCRIPTOR_UUID_ID = UUID.fromString("ec6e1003-884b-4a1c-850f-1cfce9cf6567");


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button scanButton = (Button) findViewById(R.id.BLEScanButton);
//        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
//        BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
//
//        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent =
//                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            String[] perms = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
//            if (EasyPermissions.hasPermissions(this, perms)) {
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            } else {
//                // Do not have permissions, request them now
//                EasyPermissions.requestPermissions(this,getString(R.string.ble_rationale),
//                        RC_BLE, perms);
//            }
//        }
//
//        ScanCallback scanCallback = new ScanCallback() {
//            @Override
//            public void onScanResult(int callbackType, ScanResult result) {
//                BluetoothDevice device = result.getDevice();
//                // ...do whatever you want with this found device
//                Log.w(TAG, "Device found with name:" + device.getAddress());
//            }
//
//            @Override
//            public void onBatchScanResults(List<ScanResult> results) {
//                // Ignore for now
//            }
//
//            @Override
//            public void onScanFailed(int errorCode) {
//                // Ignore for now
//                scanButton.setText("Stopped Scanning");
//            }
//        };
//
//
//
//
//        scanButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scanButton.setText("Scanning");
//
//
//                ScanSettings scanSettings = new ScanSettings.Builder()
//                        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
//                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
//                        .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
//                        .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
//                        .setReportDelay(0L)
//                        .build();
//
//                String[] names = new String[]{"VIZION"};
//                List<ScanFilter> filters = null;
//                if(names != null) {
//                    filters = new ArrayList<>();
//                    for (String name : names) {
//                        ScanFilter filter = new ScanFilter.Builder()
//                                .setDeviceName(name)
//                                .build();
//                        filters.add(filter);
//                    }
//                }
//                scanner.startScan(filters, scanSettings, scanCallback);
//            }
//        });
        // Bluetooth
        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        startScan();
    }

    // BLUETOOTH SCAN

    @SuppressLint("MissingPermission")
    private void startScan(){
//        Log.i(TAG,"startScan()");
        bluetoothScanCallback = new BluetoothScanCallback();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        String[] perms = {Manifest.permission.BLUETOOTH_SCAN};
            if (EasyPermissions.hasPermissions(this, perms)) {
                bluetoothLeScanner.startScan(bluetoothScanCallback);
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(this,getString(R.string.ble_rationale),
                        RC_BLE, perms);
            }
    }

    // BLUETOOTH CONNECTION
    @SuppressLint("MissingPermission")
    private void connectDevice(BluetoothDevice device) {
        if (device == null) Log.i(TAG,"Device is null");
        GattClientCallback gattClientCallback = new GattClientCallback();
        String[] perms = {Manifest.permission.BLUETOOTH_CONNECT};
        if (EasyPermissions.hasPermissions(this, perms)) {
            gattClient = device.connectGatt(this,false,gattClientCallback);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this,getString(R.string.ble_rationale),
                    RC_BLE, perms);
        }

    }

    // BLE Scan Callbacks
    private class BluetoothScanCallback extends ScanCallback {

        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(TAG, "onScanResult");
            if (result.getDevice().getName() != null){
                String YOUR_DEVICE_NAME = "VIZION";
                if (result.getDevice().getName().equals(YOUR_DEVICE_NAME)) {
                    // When find your device, connect.
                    connectDevice(result.getDevice());
                    bluetoothLeScanner.stopScan(bluetoothScanCallback); // stop scan
                }
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.i(TAG, "onBathScanResults");
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.i(TAG, "ErrorCode: " + errorCode);
        }
    }

    // Bluetooth GATT Client Callback
    private class GattClientCallback extends BluetoothGattCallback {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(TAG,"onConnectionStateChange");

            if (status == BluetoothGatt.GATT_FAILURE) {
                Log.i(TAG, "onConnectionStateChange GATT FAILURE");
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onConnectionStateChange != GATT_SUCCESS");
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange CONNECTED");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange DISCONNECTED");
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.i(TAG,"onServicesDiscovered");
            if (status != BluetoothGatt.GATT_SUCCESS) return;

            // Reference your UUIDs
            characteristicID = gatt.getService(SERVICE_UUID).getCharacteristic(CHARACTERISTIC_UUID_ID);
            gatt.setCharacteristicNotification(characteristicID,true);

            BluetoothGattDescriptor descriptor = characteristicID.getDescriptor(DESCRIPTOR_UUID_ID);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i(TAG,"onCharacteristicRead");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i(TAG,"onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.i(TAG,"onCharacteristicChanged");
            // Here you can read the characteristc's value
            // new String(characteristic.getValue();
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.i(TAG,"onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.i(TAG,"onDescriptorWrite");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}