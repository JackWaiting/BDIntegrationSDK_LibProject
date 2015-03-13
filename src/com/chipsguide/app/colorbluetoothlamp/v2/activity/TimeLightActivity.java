package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import java.util.Calendar;
import java.util.UUID;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.AlarmListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.view.TitleView;
import com.chipsguide.lib.timer.Alarm;
import com.chipsguide.lib.timer.Alarms;

public class TimeLightActivity extends BaseActivity implements OnItemClickListener{
	private Alarms alarms;
	private AlarmListAdapter alarmListAdapter;
	
	@Override
	public int getLayoutId() {
		return R.layout.activity_time_light;
	}

	@Override
	public void initBase() {
		alarms = Alarms.getInstance(getApplicationContext());
		alarmListAdapter = new AlarmListAdapter(this);
	}

	@Override
	public void initUI() {
		TitleView titleView = (TitleView) findViewById(R.id.titleView);
		titleView.setRightBtnImageRes(R.drawable.selector_add_btn);
		ListView alarmLv = (ListView) findViewById(R.id.lv_alarm_list);
		alarmListAdapter.setOnItemClickListener(this);
		alarmLv.setAdapter(alarmListAdapter);
	}

	@Override
	public void initData() {
	}

	@Override
	public void initListener() {
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.right_btn:
			Alarm alarm = new Alarm();
			alarm.setAlarmTime(Calendar.getInstance());
			alarm.setAlarmActive(true);
			alarm.setAlarmTonePath("");
			startLightSettingActivity(alarm);
			break;
		default:
			super.onClick(v);
			break;
		}
	}
	
	private void startLightSettingActivity(Alarm alarm) {
		Intent intent = new Intent(this, TimeLightSettingActivity.class);
		intent.putExtra(TimeLightSettingActivity.EXTRA_ALARM, alarm);
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		alarmListAdapter.setAlarms(alarms.getAllAlarm());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Alarm alarm = (Alarm) view.getTag();
		startLightSettingActivity(alarm);
	}

}
