package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.TimeDeviceLightActivity.IPCKey;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.AlarmLightColor;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.ColorUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.view.ColorSelectLayout;
import com.chipsguide.app.colorbluetoothlamp.v2.view.ColorSelectLayout.OnColorCheckedChangeListener;
import com.chipsguide.app.colorbluetoothlamp.v2.view.MyTimePickerView;
import com.chipsguide.lib.bluetooth.entities.BluetoothDeviceAlarmEntity;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceAlarmManager;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class TimeDeviceLightSettingActivity extends BaseActivity {
	public static final String EXTRA_ALARM = "alarm";
	public static final int REQUEST_SELECT_SOUND = 1;

	private TextView musicNameTv;
	private MyTimePickerView timePicker;
	private AlarmLightColor alarmLightColor;

	private String[] week;
	private String[] repeatDays;
	private int[] colorsRes = { android.R.color.white, R.color.color_orange,
			R.color.color_pink, R.color.color_purple, R.color.color_green,
			R.color.color_ching, R.color.color_blue2 };
	private boolean[] checkedItems;
	private boolean[] newCheckedItems;
	private String color;
	private int soundPath = 0;
	private LinearLayout selectedDaysLayout;
	private LampManager mLampManager;
	private BluetoothDeviceAlarmEntity mAlarmEntry;
	private BluetoothDeviceAlarmManager mBluetoothDeviceAlarmManager;
	private BluetoothDeviceManager mBluetoothDeviceManager;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_time_light_setting;
	}

	@Override
	public void initBase()
	{
		CustomApplication.addActivity(this);
		week = getResources().getStringArray(R.array.week);
		repeatDays = getResources().getStringArray(R.array.repeat_days);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null)
		{
			mAlarmEntry = (BluetoothDeviceAlarmEntity) bundle
					.getSerializable(IPCKey.ALARM_ENTRY);
		}
		mBluetoothDeviceManager = ((CustomApplication) getApplication())
				.getBluetoothDeviceManager();
		mBluetoothDeviceAlarmManager = mBluetoothDeviceManager
				.getBluetoothDeviceAlarmManager();
		mLampManager = LampManager.getInstance(this);
		mLampManager.getAlarmWithLight(mAlarmEntry.index);
	}

	@Override
	public void initUI()
	{
		musicNameTv = (TextView) findViewById(R.id.tv_music_name);
		updateMusicName();
		timePicker = (MyTimePickerView) findViewById(R.id.time_layout);
		initColorLayout();
		initSelectedDayLayout();
	}

	private void initColorLayout()
	{
		ColorSelectLayout colorSelectLayout = (ColorSelectLayout) findViewById(R.id.rg_color);
		colorSelectLayout
				.setOnColorCheckedChangeListener(new OnColorCheckedChangeListener()
				{
					@Override
					public void onColorChecked(int checkedColor, String colorStr)
					{
						color = colorStr;
					}
				});
		colorSelectLayout.setColorsRes(colorsRes);
		if (alarmLightColor != null)
		{
			// 查询固件得到的当前闹钟的颜色值
			String color = alarmLightColor.getColor();
			if (!TextUtils.isEmpty(color))
			{
				colorSelectLayout.checkColor(Color.parseColor(color));
			}
		}
	}

	private void initSelectedDayLayout()
	{
		selectedDaysLayout = (LinearLayout) findViewById(R.id.layout_selected_days);
		LayoutInflater inflater = LayoutInflater.from(this);
		for (int i = 0; i < repeatDays.length; i++)
		{
			RadioButton radioButton = (RadioButton) inflater.inflate(
					R.layout.radiobutton, selectedDaysLayout, false);
			radioButton.setText(repeatDays[i]);
			selectedDaysLayout.addView(radioButton);
		}
	}

	private void updateMusicName()
	{
		switch (soundPath)
		{
		case 0:
			musicNameTv.setText(R.string.silent);
			break;
		case 1:
			musicNameTv.setText(R.string.select_alarm_device_sound);
			break;
		}
	}

	@Override
	public void initData()
	{
		int hour = mAlarmEntry.getHour();
		int minute = mAlarmEntry.getMinute();
		timePicker.setHour(hour);
		timePicker.setMinute(minute);
		int size = mAlarmEntry.repeat.length;
		checkedItems = new boolean[7];
		for (int i = 0; i < size; i++)
		{
			checkedItems[i] = mAlarmEntry.repeat[i];
		}
		newCheckedItems = checkedItems.clone();
		updateSelectedDayLayout(checkedItems);
	}

	@Override
	public void initListener()
	{
		findViewById(R.id.btn_del).setOnClickListener(this);
		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.repeate_layout).setOnClickListener(this);
		findViewById(R.id.music_layout).setOnClickListener(this);
	}

	private void updateSelectedDayLayout(boolean[] selected)
	{
		int count = selectedDaysLayout.getChildCount();
		for (int i = 0; i < count; i++)
		{
			RadioButton rb = (RadioButton) selectedDaysLayout.getChildAt(i);
			rb.setChecked(selected[i]);
		}
	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.btn_del:
			delAlarm();
			finish();
			break;
		case R.id.btn_save:
			saveAlarm();
			finish();
			break;
		case R.id.repeate_layout:
			showSelectDaysDialog();
			break;
		case R.id.music_layout:
			Intent intent = new Intent(this, AlarmDeviceSoundActivity.class);
			intent.putExtra(AlarmDeviceSoundActivity.EXTRA_SOUND_PATH,
					soundPath);
			startActivityForResult(intent, REQUEST_SELECT_SOUND);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
		{
			if (requestCode == REQUEST_SELECT_SOUND)
			{
				soundPath = data.getIntExtra(
						AlarmSoundActivity.EXTRA_SOUND_PATH, 0);
				updateMusicName();
			}
		}
	}

	private void showSelectDaysDialog()
	{
		new AlertDialog.Builder(this)
				.setTitle(R.string.repeate)
				.setPositiveButton(R.string.ok, dialogClickListener)
				.setNegativeButton(R.string.cancl, dialogClickListener)
				.setMultiChoiceItems(week, checkedItems.clone(),
						new OnMultiChoiceClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked)
							{
								newCheckedItems[which] = isChecked;

							}
						}).show();
	}

	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			switch (which)
			{
			case DialogInterface.BUTTON_NEGATIVE:
				newCheckedItems = checkedItems.clone();
				break;
			case DialogInterface.BUTTON_POSITIVE:
				checkedItems = newCheckedItems.clone();
				updateSelectedDayLayout(checkedItems);
				break;
			default:
				break;
			}
		}
	};

	private void saveAlarm()
	{
		mAlarmEntry.hour = timePicker.getHour();
		mAlarmEntry.minute = timePicker.getMinute();

		for (int i = 0; i < newCheckedItems.length; i++)
		{
			mAlarmEntry.repeat[i] = newCheckedItems[i];
		}
		mAlarmEntry.state = true;
		if (mBluetoothDeviceAlarmManager != null)
		{
			mBluetoothDeviceAlarmManager.remove(mAlarmEntry);
			mBluetoothDeviceAlarmManager.set(mAlarmEntry);
		}
		updateAlarm();

	}

	/**
	 * 更新闹钟
	 */
	private void updateAlarm()
	{
		// 发送铃声和颜色。
		// 颜色为colors
		boolean isMute = true;
		switch (soundPath)
		{
		case 0:
			isMute = true;
			break;
		case 1:
			isMute = false;
			break;
		}
		int alarmColor = ColorUtil.Color2Color(this, color);
		int red = Color.red(alarmColor);
		int green = Color.green(alarmColor);
		int blue = Color.blue(alarmColor);
		if(red == green && green == blue)
		{
			mLampManager.setAlarmWithCommonLight(mAlarmEntry.index, 16, 0, isMute);
		}else
		{
			mLampManager.setAlarmWithColorLight(mAlarmEntry.index, 0, 0, isMute, red,
					green, blue);
		}
	}

	private void delAlarm()
	{
		mBluetoothDeviceAlarmManager.remove(mAlarmEntry);
	}
}
