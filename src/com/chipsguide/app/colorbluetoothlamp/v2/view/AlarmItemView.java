package com.chipsguide.app.colorbluetoothlamp.v2.view;

import java.util.Calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.lib.timer.Alarm;

public class AlarmItemView extends FrameLayout {
	private TextView alarmTimeTv;
	private CheckBox toggleCb;
	private Drawable on,off;
	private OnCheckedChangeListener mListener;
	
	public AlarmItemView(Context context) {
		super(context);
		init(context);
	}

	private void init(final Context context) {
		LayoutInflater.from(context).inflate(R.layout.item_alarm_list, this);
		alarmTimeTv = (TextView) findViewById(R.id.tv_alarm_time);
		toggleCb = (CheckBox) findViewById(R.id.cb_toggle);
		on = context.getResources().getDrawable(R.drawable.ic_lamp_blue);
		off = context.getResources().getDrawable(R.drawable.ic_lamp_gray);
		on.setBounds(0, 0, on.getIntrinsicWidth(), on.getIntrinsicHeight());
		off.setBounds(0, 0, off.getIntrinsicWidth(), off.getIntrinsicHeight());
		
		toggleCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				alarmTimeTv.setCompoundDrawables(isChecked ? on : off, null, null, null);
				if(mListener != null){
					mListener.onCheckedChanged(buttonView, isChecked);
				}
			}
		});
	}
	
	public void render(Alarm alarm) {
		Calendar calendar = alarm.getAlarmTime();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		alarmTimeTv.setText(String.format("%02d", hour) + ":" + String.format("%02d",minute));
		boolean active = alarm.getAlarmActive();
		toggleCb.setChecked(active);
		alarmTimeTv.setCompoundDrawables(active ? on : off, null, null, null);
	}
	
	public void setOnToggleListener(OnCheckedChangeListener listener) {
		mListener = listener;
	}
	
	public OnCheckedChangeListener getOnToggleListener() {
		return mListener;
	}
}
