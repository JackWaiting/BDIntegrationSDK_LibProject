package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.AlarmListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PreferenceUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.view.TitleView;
import com.chipsguide.lib.timer.Alarm;
import com.chipsguide.lib.timer.Alarms;
import com.nineoldandroids.view.ViewHelper;

public class TimeLightActivity extends BaseActivity implements OnItemClickListener{
	private Alarms alarms;
	private AlarmListAdapter alarmListAdapter;
	private PopupWindow popupwindow;
	private View addBtn;
	private PreferenceUtil preference;
	private TitleView titleView;
	/**
	 * 最多闹钟数
	 */
	private static final int MAX_ALARM_SIZE = 10;
	
	@Override
	public int getLayoutId() {
		return R.layout.activity_time_light;
	}

	@Override
	public void initBase() {
		preference = PreferenceUtil.getIntance(getApplicationContext());
		alarms = Alarms.getInstance(getApplicationContext());
		alarmListAdapter = new AlarmListAdapter(this);
	}

	@Override
	public void initUI() {
		titleView = (TitleView) findViewById(R.id.titleView);
		titleView.setShowToastTv(true);
		titleView.setHoldTime(2000);
		titleView.setRightBtnImageRes(R.drawable.selector_add_btn);
		ListView alarmLv = (ListView) findViewById(R.id.lv_alarm_list);
		alarmListAdapter.setOnItemClickListener(this);
		alarmLv.setAdapter(alarmListAdapter);
		addBtn = findViewById(R.id.right_btn);
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
			if(maxSize){
				titleView.setToastText(R.string.reach_max_alarm_reminder);
			}else{
				Alarm alarm = new Alarm();
				alarm.setAlarmTime(Calendar.getInstance());
				alarm.setAlarmActive(true);
				alarm.setAlarmTonePath("");
				startLightSettingActivity(alarm);
			}
			break;
		case R.id.btn_ok:
			popupwindow.dismiss();
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
	
	private void showCaseview() {
		int [] location = new int[2];
		addBtn.getLocationInWindow(location);
		
		popupwindow = new PopupWindow(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View contentView = LayoutInflater.from(this).inflate(R.layout.layout_caseview, null);
		View inside = contentView.findViewById(R.id.content_view);
		ViewHelper.setY(inside, location[1] + addBtn.getHeight());
		contentView.findViewById(R.id.btn_ok).setOnClickListener(this);
		popupwindow.setContentView(contentView);
		popupwindow.setAnimationStyle(android.R.style.Animation_Dialog);
		popupwindow.setFocusable(true);
		popupwindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#99000000")));
		popupwindow.showAtLocation(findViewById(R.id.parent), Gravity.NO_GRAVITY, 0, 0);
		popupwindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				preference.setFirstEnterAlarm(false);
			}
		});
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus){
			boolean first = preference.isFirstEnterAlarm();
			if(first){
				showCaseview();
			}
		}
	}
	
	private boolean maxSize;
	@Override
	protected void onResume() {
		super.onResume();
		List<Alarm> list = alarms.getAllAlarm();
		alarmListAdapter.setAlarms(list);
		if(list != null && list.size() >= MAX_ALARM_SIZE){
			maxSize = true;
		}else{
			maxSize = false;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Alarm alarm = (Alarm) view.getTag();
		startLightSettingActivity(alarm);
	}

}
