package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.AlarmLightColor;
import com.chipsguide.app.colorbluetoothlamp.v2.db.AlarmLightColorDAO;
import com.chipsguide.app.colorbluetoothlamp.v2.view.MyTimePickerView;
import com.chipsguide.lib.timer.Alarm;
import com.chipsguide.lib.timer.Alarm.Day;
import com.chipsguide.lib.timer.Alarms;

public class TimeLightSettingActivity extends BaseActivity {
	public static final String EXTRA_ALARM = "alarm";
	public static final int REQUEST_SELECT_SOUND = 1;

	private TextView repeateDayTv, musicNameTv;
	private MyTimePickerView timePicker;
	private Alarms alarms;
	private AlarmLightColorDAO lightColorDao;
	private Alarm alarm;
	private AlarmLightColor alarmLightColor;

	private String[] week;
	private boolean[] checkedItems;
	private boolean[] newCheckedItems;
	private String color;
	private String soundPath;

	@Override
	public int getLayoutId() {
		return R.layout.activity_time_light_setting;
	}

	@Override
	public void initBase() {
		week = getResources().getStringArray(R.array.week);
		alarms = Alarms.getInstance(getApplicationContext());
		lightColorDao = AlarmLightColorDAO.getDao(getApplicationContext());
		alarm = (Alarm) getIntent().getSerializableExtra(EXTRA_ALARM);
		alarmLightColor = lightColorDao.query(alarm.getId()+"");
		soundPath = alarm.getAlarmTonePath();
	}

	@Override
	public void initUI() {
		repeateDayTv = (TextView) findViewById(R.id.tv_repeate_day);
		musicNameTv = (TextView) findViewById(R.id.tv_music_name);
		if(!TextUtils.isEmpty(soundPath)){
			String [] arr = soundPath.split("\\|");
			if(arr != null && arr.length > 1){
				musicNameTv.setText(arr[1]);
			}
		}
		timePicker = (MyTimePickerView) findViewById(R.id.time_layout);
		RadioGroup colorGroup = (RadioGroup) findViewById(R.id.rg_color);
		colorGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				View view = group.findViewById(checkedId);
				color = view.getTag().toString();
			}
		});
		colorGroup.check(R.id.rb_color_white);
		if(alarmLightColor != null){
			color = alarmLightColor.getColor();
			int childCount = colorGroup.getChildCount();
			for(int i = 0 ; i < childCount ; i++){
				RadioButton rb = (RadioButton) colorGroup.getChildAt(i);
				if(rb.getTag().equals(color)){
					rb.setChecked(true);
					break;
				}
			}
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
			if(position < 0){
				position = 6;
			}
			checkedItems[position] = true;
		}
		repeateDayTv.setText(getSelectedDayString(checkedItems));
		newCheckedItems = checkedItems.clone();
	}

	@Override
	public void initListener() {
		findViewById(R.id.btn_del).setOnClickListener(this);
		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.repeate_layout).setOnClickListener(this);
		findViewById(R.id.music_layout).setOnClickListener(this);
	}

	private CharSequence getSelectedDayString(boolean [] selected) {
		int color = getResources().getColor(R.color.color_blue);
		SpannableStringBuilder builder = new SpannableStringBuilder(
				repeateDayTv.getText());
		for(int i = 0 ; i < 7 ; i++){
			ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLACK);
			if(selected[i]){
				colorSpan = new ForegroundColorSpan(color);
			}
			builder.setSpan(colorSpan, 2*i, 2*i + 1,
					Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		return builder;
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
		if(resultCode == RESULT_OK){
			if(requestCode == REQUEST_SELECT_SOUND){
				soundPath = data.getStringExtra(AlarmSoundActivity.EXTRA_SOUND_PATH);
				if(!TextUtils.isEmpty(soundPath)){
					String [] arr = soundPath.split("\\|");
					String name = arr[1];
					musicNameTv.setText(name);
				}
			}
		}
	}

	private void showSelectDaysDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.repeate)
				.setPositiveButton(R.string.ok, dialogClickListener)
				.setNegativeButton(R.string.cancl, dialogClickListener)
				.setMultiChoiceItems(week, checkedItems.clone(), new OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
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
				repeateDayTv.setText(getSelectedDayString(checkedItems));
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
		List<Day> list = new ArrayList<Day>();
		Day[] allDay = Day.values();
		for (int i = 0; i < 7; i++) {
			if (checkedItems[i]) {
				if(i == 6){
					list.add(allDay[0]);
				}else{
					list.add(allDay[i + 1]);
				}
			}
		}
		Day[] selectedDays = new Day[list.size()];
		alarm.setDays(list.toArray(selectedDays));
		alarms.saveAlarm(alarm);
		if(alarmLightColor == null){
			alarmLightColor = new AlarmLightColor();
			alarmLightColor.setAlarm_id(alarm.getId());
		}
		alarmLightColor.setColor(color);
		lightColorDao.saveOrUpdate(alarmLightColor);
	}

	private void delAlarm() {
		alarms.delete(alarm);
	}
}
