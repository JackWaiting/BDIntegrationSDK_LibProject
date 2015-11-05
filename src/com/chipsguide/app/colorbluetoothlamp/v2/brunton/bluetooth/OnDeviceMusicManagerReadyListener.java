package com.chipsguide.app.colorbluetoothlamp.v2.brunton.bluetooth;

import com.chipsguide.lib.bluetooth.interfaces.templets.IBluetoothDeviceMusicManager;

public interface OnDeviceMusicManagerReadyListener {
	void onMusicManagerReady(IBluetoothDeviceMusicManager manager, int mode);
	void onMusicManagerReadyFailed(int mode);
}
