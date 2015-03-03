package com.chipsguide.app.colorbluetoothlamp.v2.service;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.AlarmLightColor;
import com.chipsguide.app.colorbluetoothlamp.v2.db.AlarmLightColorDAO;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.lib.timer.Alarm;
import com.chipsguide.lib.timer.Alarms;
import com.chipsguide.lib.timer.Alarms.OnAlertListener;

public class AlarmAlertService extends Service {
	private MediaPlayer mediaPlayer;
	private AlarmLightColorDAO lightColorDao;
	private LampManager mLampManager;
	private boolean destroy;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initAlertListener();
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setLooping(true);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer arg0) {
				mediaPlayer.start();
			}
		});
	}

	private void initAlertListener() {
		mLampManager = LampManager
				.getInstance(getApplicationContext());
		lightColorDao = AlarmLightColorDAO
				.getDao(getApplicationContext());
		Alarms.getInstance(getApplicationContext()).setOnAlertListener(new MyAlertListener(this));
	}

	private void showDialog() {
		AlertDialog ad = new AlertDialog.Builder(this).setTitle(R.string.alarm)
				.setMessage(R.string.time_up_)
				.setNegativeButton(R.string.dismiss, listener).create();
		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.show();
	}
	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_NEGATIVE:
				mediaPlayer.release();
				mediaPlayer = null;
				break;
			default:
				break;
			}
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		destroy = true;
		if(mediaPlayer != null){
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	private static class MyAlertListener implements OnAlertListener{
		private WeakReference<AlarmAlertService> ref;
		public MyAlertListener(AlarmAlertService service){
			ref = new WeakReference<AlarmAlertService>(service);
		}

		@Override
		public void onAlert(List<Alarm> list) {
			AlarmAlertService service = ref.get();
			if(service != null && !service.destroy){
				service.onAlert(list);
			}
		}
	}
	
	private void onAlert(List<Alarm> list) {
		Alarm alarm = list.get(0);
		AlarmLightColor lightcolor = lightColorDao.query(alarm
				.getId() + "");
		int color = Color.parseColor(lightcolor.getColor());
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		mLampManager.setColor(red, green, blue);
		String ringTone = alarm.getAlarmTonePath();
		if (!TextUtils.isEmpty(ringTone)) {
			String[] arr = ringTone.split("\\|");
			String type = arr[0];
			PlayType playType = PlayType.valueOf(type);
			if (playType == PlayType.Local) {
				playLocalMusic(arr[2]);
			} else {
				Log.d("", arr[1]);
			}
		}
		showDialog();
	}
	
	private void playLocalMusic(String path) {
		try {
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
