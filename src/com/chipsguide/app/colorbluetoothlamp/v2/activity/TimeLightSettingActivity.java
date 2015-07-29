package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.AlarmLightColor;
import com.chipsguide.app.colorbluetoothlamp.v2.db.AlarmLightColorDAO;
import com.chipsguide.app.colorbluetoothlamp.v2.view.ColorSelectLayout;
import com.chipsguide.app.colorbluetoothlamp.v2.view.ColorSelectLayout.OnColorCheckedChangeListener;
import com.chipsguide.app.colorbluetoothlamp.v2.view.MyTimePickerView;
import com.chipsguide.lib.timer.Alarm;
import com.chipsguide.lib.timer.Alarm.Day;
import com.chipsguide.lib.timer.Alarms;

public class TimeLightSettingActivity extends BaseActivity {
	public static final String EXTRA_ALARM = "alarm";
	public static final int REQUEST_SELECT_SOUND = 1;

	private TextView musicNameTv;
	private MyTimePickerView timePicker;
	private Alarms alarms;
	private AlarmLightColorDAO lightColorDao;
	private Alarm alarm;
	private AlarmLightColor alarmLightColor;

	private String[] week;
	private String[] repeatDays;
	private int[] colorsRes = { android.R.color.white, R.color.color_orange,
			R.color.color_pink, R.color.color_purple, R.color.color_green,
			R.color.color_ching, R.color.color_blue2};
	private boolean[] checkedItems;
	private boolean[] newCheckedItems;
	private String color;
	private String soundPath;
	private LinearLayout selectedDaysLayout;

	@Override
	public int getLayoutId() {
		return R.layout.activity_time_light_setting;
	}

	@Override
	public void initBase() {
		week = getResources().getStringArray(R.array.week);
		repeatDays = getResources().getStringArray(R.array.repeat_days);
		alarms = Alarms.getInstance(getApplicationContext());
		lightColorDao = AlarmLightColorDAO.getDao(getApplicationContext());
		alarm = (Alarm) getIntent().getSerializableExtra(EXTRA_ALARM);
		alarmLightColor = lightColorDao.query(alarm.getId() + "");
		soundPath = alarm.getAlarmTonePath();
	}

	@Override
	public void initUI() {
		musicNameTv = (TextView) findViewById(R.id.tv_music_name);
		updateMusicName();
		timePicker = (MyTimePickerView) findViewById(R.id.time_layout);
		initColorLayout();
		initSelectedDayLayout();
	}
	
	private void initColorLayout() {
		ColorSelectLayout colorSelectLayout = (ColorSelectLayout) findViewById(R.id.rg_color);
		colorSelectLayout.setOnColorCheckedChangeListener(new OnColorCheckedChangeListener() {
			@Override
			public void onColorChecked(int checkedColor, String colorStr) {
				color = colorStr;
			}
		});
		colorSelectLayout.setColorsRes(colorsRes);
		if(alarmLightColor != null){
			String color = alarmLightColor.getColor();
			if(!TextUtils.isEmpty(color)){
				colorSelectLayout.checkColor(Color.parseColor(color));
			}
		}
	}

	private void initSelectedDayLayout() {
		selectedDaysLayout = (LinearLayout) findViewById(R.id.layout_selected_days);
		LayoutInflater inflater = LayoutInflater.from(this);
		for (int i = 0; i < repeatDays.length; i++) {
			RadioButton radioButton = (RadioButton) inflater.inflate(
					R.layout.radiobutton, selectedDaysLayout, false);
			radioButton.setText(repeatDays[i]);
			selectedDaysLayout.addView(radioButton);
		}
	}

	private void updateMusicName() {
		if (!TextUtils.isEmpty(soundPath)) {
			String[] arr = soundPath.split("\\|");
			if (arr != null && arr.length > 1) {
				musicNameTv.setText(arr[1]);
			}
		} else {
			musicNameTv.setText(R.string.silent);
		}
	}

	@Override
	public void initData() {
		Calendar calendar = alarm.getAlarmTime();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		timePicker.setHour(hour);
		timePicker.setMinute(minute);
		Day[] days = alarm.getDays();
		int size = days.length;
		checkedItems = new boolean[7];
		for (int i = 0; i < size; i++) {
			int position = days[i].ordinal() - 1;
			if (position < 0) {
				position = 6;
			}
			checkedItems[position] = true;
		}
		newCheckedItems = checkedItems.clone();
		updateSelectedDayLayout(checkedItems);
	}

	@Override
	public void initListener() {
		findViewById(R.id.btn_del).setOnClickListener(this);
		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.repeate_layout).setOnClickListener(this);
		findViewById(R.id.music_layout).setOnClickListener(this);
	}

	private void updateSelectedDayLayout(boolean[] selected) {
		int count = selectedDaysLayout.getChildCount();
		for (int i = 0; i < count; i++) {
			RadioButton rb = (RadioButton) selectedDaysLayout.getChildAt(i);
			rb.setChecked(selected[i]);
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		CustomApplication.addActivity(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
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
			Intent intent = new Intent(this, AlarmSoundActivity.class);
			intent.putExtra(AlarmSoundActivity.EXTRA_SOUND_PATH, soundPath);
			startActivityForResult(intent, REQUEST_SELECT_SOUND);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_SELECT_SOUND) {
				soundPath = data
						.getStringExtra(AlarmSoundActivity.EXTRA_SOUND_PATH);
				updateMusicName();
			}
		}
	}

	private void showSelectDaysDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.repeate)
				.setPositiveButton(R.string.ok, dialogClickListener)
				.setNegativeButton(R.string.cancl, dialogClickListener)
				.setMultiChoiceItems(week, checkedItems.clone(),
						new OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								newCheckedItems[which] = isChecked;

							}
						}).show();
	}

	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
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

	private void saveAlarm() {
		int hour = timePicker.getHour();
		int minute = timePicker.getMinute();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		alarm.setAlarmActive(true);
		alarm.setAlarmTime(calendar);
		alarm.setAlarmTonePath(soundPath);
		alarm.setDays(getSelectedDays());
		alarms.saveAlarm(alarm);
		if (alarmLightColor == null) {
			alarmLightColor = new AlarmLightColor();
			alarmLightColor.setAlarm_id(alarm.getId());
		}
		alarmLightColor.setColor(color);
		lightColorDao.saveOrUpdate(alarmLightColor);
	}

	private Day[] getSelectedDays() {
		List<Day> list = new ArrayList<Day>();
		Day[] allDay = Day.values();
		for (int i = 0; i < 7; i++) {
			if (checkedItems[i]) {
				if (i == 6) {
					list.add(allDay[0]);
				} else {
					list.add(allDay[i + 1]);
				}
			}
		}
		Day[] days = new Day[list.size()];
		return list.toArray(days);
	}

	private void delAlarm() {
		alarms.delete(alarm);
	}
	
	@Override
	public void updateConnectState()
	{
	}

	@Override
	public void updateAlarm(int state)
	{
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mSubject.deleteach(this);
	}
}
