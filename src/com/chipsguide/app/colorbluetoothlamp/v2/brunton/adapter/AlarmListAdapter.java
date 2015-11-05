package com.chipsguide.app.colorbluetoothlamp.v2.brunton.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.view.AlarmItemView;
import com.chipsguide.lib.timer.Alarm;
import com.chipsguide.lib.timer.Alarms;

public class AlarmListAdapter extends BaseAdapter {
	private Context context;
	private List<Alarm> alarms = new ArrayList<Alarm>();
	private Alarms alarmMan;
	
	public AlarmListAdapter(Context context){
		this.context = context;
		alarmMan = Alarms.getInstance(context);
	}
	
	public void setAlarms(List<Alarm> alarms) {
		if(alarms == null){
			return;
		}
		this.alarms = alarms;
		this.notifyDataSetChanged();
	}
	
	public List<Alarm> getAlarms() {
		return alarms;
	}

	@Override
	public int getCount() {
		return alarms.size();
	}

	@Override
	public Alarm getItem(int position) {
		return alarms.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position,final View convertView, final ViewGroup parent) {
		AlarmItemView itemView = null;
		if(convertView == null){
			itemView = new AlarmItemView(context);
		}else{
			itemView = (AlarmItemView) convertView;
		}
		itemView.render(getItem(position));
		itemView.setTag(getItem(position));
		OnCheckedChangeListener listener = itemView.getOnToggleListener();
		if(listener == null){
			itemView.setOnToggleListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Alarm alarm = getItem(position);
					alarm.setAlarmActive(isChecked);
					alarmMan.saveAlarm(alarm);
				}
			});
		}
		itemView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mListener != null){
					mListener.onItemClick((AdapterView<?>) parent, v, position, v.getId());
				}
			}
		});
		return itemView;
	}
	
	private OnItemClickListener mListener;
	public void setOnItemClickListener(OnItemClickListener listener) {
		mListener = listener;
	}

}
