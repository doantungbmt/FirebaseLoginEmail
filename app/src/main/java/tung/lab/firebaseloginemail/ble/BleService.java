package tung.lab.firebaseloginemail.ble;import android.annotation.SuppressLint;import android.app.Service;import android.bluetooth.BluetoothAdapter;import android.bluetooth.BluetoothDevice;import android.bluetooth.BluetoothGatt;import android.bluetooth.BluetoothGattCallback;import android.bluetooth.BluetoothGattCharacteristic;import android.bluetooth.BluetoothGattDescriptor;import android.bluetooth.BluetoothGattService;import android.bluetooth.BluetoothManager;import android.bluetooth.BluetoothProfile;import android.content.Context;import android.content.Intent;import android.os.Binder;import android.os.Build;import android.os.IBinder;import android.util.Log;import java.lang.reflect.Method;import java.util.ArrayList;import java.util.HashMap;import java.util.LinkedList;import java.util.List;import java.util.Queue;import java.util.UUID;import tung.lab.firebaseloginemail.Utils.BleData;import tung.lab.firebaseloginemail.Utils.ResolveData;import tung.lab.firebaseloginemail.Utils.RxBus;public final class BleService extends Service {    private static final String TAG = "BleService";    private static final UUID NOTIY = UUID            .fromString("00002902-0000-1000-8000-00805f9b34fb");    private static final UUID SERVICE_DATA = UUID            .fromString("0000fff0-0000-1000-8000-00805f9b34fb");    private static final UUID DATA_Characteristic = UUID            .fromString("0000fff6-0000-1000-8000-00805f9b34fb");    private static final UUID NOTIY_Characteristic = UUID            .fromString("0000fff7-0000-1000-8000-00805f9b34fb");    private boolean NeedReconnect = false;    public final static String ACTION_GATT_onDescriptorWrite = "com.jstylelife.ble.service.onDescriptorWrite";    public final static String ACTION_GATT_CONNECTED = "com.jstylelife.ble.service.ACTION_GATT_CONNECTED";    public final static String ACTION_GATT_DISCONNECTED = "com.jstylelife.ble.service.ACTION_GATT_DISCONNECTED";    public final static String ACTION_DATA_AVAILABLE = "com.jstylelife.ble.service.ACTION_DATA_AVAILABLE";    public HashMap<BluetoothDevice, BluetoothGatt> hasp = new HashMap<BluetoothDevice, BluetoothGatt>();    private final IBinder kBinder = new LocalBinder();    private static ArrayList<BluetoothGatt> arrayGatts = new ArrayList<BluetoothGatt>(); // 存放BluetoothGatt的集�?    public static BluetoothGattCharacteristic colorCharacteristic;    private HashMap<String, BluetoothGatt> gattHash = new HashMap<String, BluetoothGatt>();    private BluetoothManager bluetoothManager;    private BluetoothAdapter mBluetoothAdapter;    private BluetoothGatt mGatt;    private boolean isConnected;    @Override    public IBinder onBind(Intent intent) {        initAdapter();        return kBinder;    }    @Override    public boolean onUnbind(Intent intent) {        return super.onUnbind(intent);    }    private String address;    @SuppressLint("MissingPermission")    public void initBluetoothDevice(final String address, final Context context) {        this.address = address;        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);        if (isConnected()) return;        if (null != mGatt) {            refreshDeviceCache(mGatt);            mGatt = null;        }        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {            mGatt = device.connectGatt(context, false, bleGattCallback, BluetoothDevice.TRANSPORT_LE);        } else {            mGatt = device.connectGatt(context, false, bleGattCallback);        }        if (mGatt == null) {            System.out.println(device.getAddress() + "gatt is null");        }    }    public String getDeviceAddress() {        return this.address;    }    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {        @Override        public void onLeScan(final BluetoothDevice device, final int rssi,                             final byte[] scanRecord) {            if (device.getAddress().equals(address) && Math.abs(rssi) < 90) {            }        }    };    private void initAdapter() {        if (bluetoothManager == null) {            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);            if (bluetoothManager == null) {                return;            }        }        mBluetoothAdapter = bluetoothManager.getAdapter();    }    @SuppressLint("MissingPermission")    public void disconnect() {        NeedReconnect = false;        if (mGatt == null)            return;        mGatt.disconnect();    }    @SuppressLint("MissingPermission")    public void disconnect(String address) {        ArrayList<BluetoothGatt> gatts = new ArrayList<BluetoothGatt>();        for (BluetoothGatt gatt : arrayGatts) {            if (gatt != null && gatt.getDevice().getAddress().equals(address)) {                gatts.add(gatt);                gatt.close();            }        }        arrayGatts.removeAll(gatts);    }    public class LocalBinder extends Binder {        public BleService getService() {            return BleService.this;        }    }    private int discoverCount;    private Object ob = new Object();    private BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {        @SuppressLint("MissingPermission")        @Override        public void onConnectionStateChange(BluetoothGatt gatt, int status,                                            int newState) {            String action = null;            Log.i(TAG, "onConnectionStateChange:  status" + status + " newstate " + newState);            if (newState == BluetoothProfile.STATE_CONNECTED) {                if (status == 133) {                    mGatt.close();                    mGatt = null;                    return;                }                action = ACTION_GATT_CONNECTED;                try {                    gatt.discoverServices();                } catch (Exception e) {                    // TODO: handle exception                    e.printStackTrace();                }            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {                isConnected = false;                Log.i(TAG, "onConnectionStateChange: " + ACTION_GATT_DISCONNECTED);                if (mGatt != null) {                    mGatt.close();                    mGatt = null;                }                queues.clear();                if (!NeedReconnect) {                    action = ACTION_GATT_DISCONNECTED;                    broadcastUpdate(action);                }            }        }        @Override        public void onServicesDiscovered(BluetoothGatt gatt, int status) {            if (status == BluetoothGatt.GATT_SUCCESS) {                String address = gatt.getDevice().getAddress();                @SuppressLint("MissingPermission") String name = mBluetoothAdapter.getRemoteDevice(address)                        .getName();                setCharacteristicNotification(true);                discoverCount = 0;            } else {                Log.w("servieDiscovered", "onServicesDiscovered received: "                        + status);            }        }        @SuppressLint("MissingPermission")        @Override        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {            super.onMtuChanged(gatt, mtu, status);            if (BluetoothGatt.GATT_SUCCESS == status) {                setCharacteristicNotification(true);            } else {                gatt.requestMtu(153);            }        }        public void onCharacteristicRead(BluetoothGatt gatt,                                         BluetoothGattCharacteristic characteristic,                                         int status) {            if (status == BluetoothGatt.GATT_SUCCESS) {                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic, gatt                        .getDevice().getAddress());            } else {            }        }        public void onDescriptorWrite(BluetoothGatt gatt,                                      BluetoothGattDescriptor descriptor, int status) {            if (status == BluetoothGatt.GATT_SUCCESS) {                isConnected = true;                broadcastUpdate(ACTION_GATT_onDescriptorWrite);            } else {                Log.i(TAG, "onDescriptorWrite: failed");            }        }        ;        public void onCharacteristicChanged(BluetoothGatt gatt,                                            BluetoothGattCharacteristic characteristic) {            if (mGatt == null)                return;            Log.i(TAG, "onCharacteristicChanged: " + ResolveData.byte2Hex(characteristic.getValue()));            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic, gatt                    .getDevice().getAddress());        }        public void onCharacteristicWrite(BluetoothGatt gatt,                                          BluetoothGattCharacteristic characteristic, int status) {            if (status == BluetoothGatt.GATT_SUCCESS) {                nextQueue();            } else {            }        }        ;    };    public boolean refreshDeviceCache(BluetoothGatt gatt) {        try {            BluetoothGatt localBluetoothGatt = gatt;            Method localMethod = localBluetoothGatt.getClass().getMethod(                    "refresh", new Class[0]);            if (localMethod != null) {                boolean bool = ((Boolean) localMethod.invoke(                        localBluetoothGatt, new Object[0])).booleanValue();                return bool;            }        } catch (Exception localException) {            Log.e("s", "An exception occured while refreshing device");        }        return false;    }    private void broadcastUpdate(String action) {        BleData bleData = new BleData();        bleData.setAction(action);        RxBus.getInstance().post(bleData);    }    private void broadcastUpdate(String action,                                 BluetoothGattCharacteristic characteristic, String mac) {        //   Intent intent = new Intent(action);        byte[] data = characteristic.getValue();        BleData bleData = new BleData();        bleData.setAction(action);        bleData.setValue(data);        RxBus.getInstance().post(bleData);    }    @SuppressLint("MissingPermission")    public void readValue(BluetoothGattCharacteristic characteristic) {        if (mGatt == null) return;        mGatt.readCharacteristic(characteristic);    }    @SuppressLint("MissingPermission")    public void writeValue(byte[] value) {        if (mGatt == null || value == null) return;        BluetoothGattService service = mGatt.getService(SERVICE_DATA);        if (service == null) return;        BluetoothGattCharacteristic characteristic = service.getCharacteristic(DATA_Characteristic);        if (characteristic == null) return;        if (value[0] == (byte) 0x47) {            NeedReconnect = false;        }        characteristic.setValue(value);        Log.i(TAG, "writeValue: " + ResolveData.byte2Hex(value));        mGatt.writeCharacteristic(characteristic);    }    @SuppressLint("MissingPermission")    public void setCharacteristicNotification(boolean enable) {        // TODO Auto-generated method stub        if (mGatt == null) return;        BluetoothGattService service = mGatt.getService(SERVICE_DATA);        if (service == null) return;        BluetoothGattCharacteristic characteristic = service.getCharacteristic(NOTIY_Characteristic);        if (characteristic == null) return;        mGatt.setCharacteristicNotification(characteristic, enable);        try {            Thread.sleep(20);        } catch (InterruptedException e) {            e.printStackTrace();        }        BluetoothGattDescriptor descriptor = characteristic                .getDescriptor(NOTIY);        if (descriptor == null) {            return;        }        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);        if (mGatt == null)            return;        mGatt.writeDescriptor(descriptor);    }    public List<BluetoothGattService> getSupportedGattServices() {        if (mGatt == null) {            return null;        }        return mGatt.getServices();    }    private BluetoothGatt getBluetoothGatt(BluetoothDevice device) {        return mGatt;    }    @SuppressLint("MissingPermission")    public void readRssi(BluetoothDevice device) {        mGatt.readRemoteRssi();    }    @Override    public void onDestroy() {        // TODO Auto-generated method stub        super.onDestroy();    }    Queue<byte[]> queues = new LinkedList<>();    public void offerValue(byte[] value) {        queues.offer(value);    }    public void nextQueue() {        final Queue<byte[]> requests = queues;        byte[] data = requests != null ? requests.poll() : null;        writeValue(data);    }    public boolean isConnected() {        return this.isConnected;    }}