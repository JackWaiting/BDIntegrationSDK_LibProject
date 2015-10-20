package com.chipsguide.app.colorbluetoothlamp.v2.bluetooth;

import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceMusicManager.IBluetoothDeviceMusicManager;

public interface OnDeviceMusicManagerReadyListener {
	void onMusicManagerReady(IBluetoothDeviceMusicManager manager, int mode);
	void onMusicManagerReadyFailed(int mode);
}
