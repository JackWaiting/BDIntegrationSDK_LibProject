package com.chipsguide.app.colorbluetoothlamp.v2.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.AlarmLightColor;
import com.chipsguide.app.colorbluetoothlamp.v2.db.AlarmLightColorDAO;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.lib.timer.Alarm;
import com.chipsguide.lib.timer.service.AlarmService;

public class AlarmAlertService extends AlarmService {
	private MediaPlayer mediaPlayer;
	private AlarmLightColorDAO lightColorDao;
	private LampManager mLampManager;
	protected boolean destroy;
	private PlayerManager playerManager;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		playerManager = PlayerManager.getInstance(getApplicationContext());
		initAlertListener();
	}

	private void initAlertListener() {
		mLampManager = LampManager
				.getInstance(getApplicationContext());
		lightColorDao = AlarmLightColorDAO
				.getDao(getApplicationContext());
	}

	private void showDialog(String time) {
		AlertDialog ad = new AlertDialog.Builder(this).setTitle(R.string.alarm)
				.setMessage(time)
				.setNegativeButton(R.string.cancl, listener).create();
		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.setCanceledOnTouchOutside(false);
		ad.setCancelable(false);
		ad.show();
	}
	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_NEGATIVE:
				if(mediaPlayer != null){
					mediaPlayer.release();
					mediaPlayer = null;
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("", "onDestroy");
		destroy = true;
		if(mediaPlayer != null){
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@SuppressLint("SimpleDateFormat") private void onAlert(List<Alarm> list) {
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
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Calendar calendar = alarm.getAlarmTime();
		Date date = calendar.getTime();
		showDialog(format.format(date));
	}
	
	private void build() {
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
	
	private void playLocalMusic(String path) {
		try {
			build();
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

	@Override
	public void onAlarmActive(List<Alarm> list) {
		if(playerManager.isPlaying()){
			playerManager.pause();
		}
		onAlert(list);
	}

}
