package mirror.android.bluetooth;

import android.os.IBinder;
import android.os.IInterface;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticObject;
import mirror.RefStaticMethod;

public class BluetoothDevice {
    public static Class<?> TYPE = RefClass.load(BluetoothDevice.class, "android.bluetooth.BluetoothDevice");
    public static RefStaticObject<IInterface> sService;
    public static RefStaticMethod<IInterface> getService;
}
