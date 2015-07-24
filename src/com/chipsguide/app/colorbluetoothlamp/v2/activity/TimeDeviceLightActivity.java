package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.view.CustomDialog;
import com.chipsguide.app.colorbluetoothlamp.v2.view.MyTextView;
import com.chipsguide.app.colorbluetoothlamp.v2.view.TitleView;
import com.chipsguide.lib.bluetooth.entities.BluetoothDeviceAlarmEntity;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceAlarmManagerReadyListener;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceAlarmManager;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceAlarmManager.OnBluetoothDeviceAlarmUIChangedListener;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class TimeDeviceLightActivity extends BaseActivity implements
		OnItemClickListener {
	private AlarmClockAdapter mAlarmClockAdapter;// 闹钟适配器
	private View addBtn;
	private TitleView titleView;
	private TextView mTextViewToas;
	private boolean maxSize;
	private String formatStr;
	/**
	 * 最多闹钟数
	 */
	private static final int MAX_ALARM_SIZE = 4;
	private BluetoothDeviceManagerProxy mManagerProxy;
	private BluetoothDeviceManager mBluetoothDeviceManager;
	private BluetoothDeviceAlarmManager mBluetoothDeviceAlarmManager;

	private List<AlarmClockNode> mAlarmEntriesList = new ArrayList<AlarmClockNode>();

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_time_light;
	}

	@Override
	public void initBase()
	{
		CustomApplication.addActivity(this);
		formatStr = getResources().getString(R.string.text_loading);
		mManagerProxy = BluetoothDeviceManagerProxy.getInstance(this);
		mManagerProxy.changeToAlarm();
		mBluetoothDeviceManager = ((CustomApplication) getApplication())
				.getBluetoothDeviceManager();
		showLoadingMusicDialog();
	}

	private CustomDialog dialog;

	private void showLoadingMusicDialog()
	{
		if (dialog != null)
		{
			dialog.dismiss();
			dialog = null;
		}
		dialog = new CustomDialog(this, R.style.Dialog_Fullscreen_dim);
		String str = String.format(formatStr, 0);
		dialog.setMessage(str);
		dialog.show();
	}

	@Override
	public void initUI()
	{
		titleView = (TitleView) findViewById(R.id.titleView);
		titleView.setShowToastTv(true);
		titleView.setHoldTime(2000);
		titleView.setRightBtnImageRes(R.drawable.selector_add_btn);
		ListView alarmLv = (ListView) findViewById(R.id.lv_alarm_list);
		mAlarmClockAdapter = new AlarmClockAdapter();
		setOnItemClickListener(this);
		alarmLv.setAdapter(mAlarmClockAdapter);
		addBtn = findViewById(R.id.right_btn);
		addBtn.setOnClickListener(this);
		mTextViewToas = (TextView) this
				.findViewById(R.id.textview_toast_notlist);
	}

	@Override
	public void initData()
	{
	}

	@Override
	public void initListener()
	{
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.left_btn:
			finish();
			break;
		case R.id.right_btn:
			if (maxSize)
			{
				titleView
						.setToastText(R.string.reach_max_device_alarm_reminder);
			} else
			{
				changeToAlarmSetting(creatNewAlarmEntry());
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		changeToAlarmSetting(mAlarmEntriesList.get(position).getAlarmEntry());
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		refreshAlarmEntries();
		notifyDataChanged();
		if (mBluetoothDeviceManager != null)
		{
			mBluetoothDeviceManager
					.setOnBluetoothDeviceAlarmManagerReadyListener(new OnBluetoothDeviceAlarmManagerReadyListener()
					{
						@Override
						public void onBluetoothDeviceAlarmManagerReady()
						{
							mBluetoothDeviceAlarmManager = mBluetoothDeviceManager
									.getBluetoothDeviceAlarmManager();
							initAlarmUiListener();
							refreshAlarmEntries();
							notifyDataChanged();
						}
					});
		}
		if (mBluetoothDeviceAlarmManager != null
				&& mManagerProxy.getBluetoothManagerMode() != BluetoothDeviceManager.Mode.ALARM)
		{
			finish();
		}
	}

	// 刷新闹钟个数
	public void refreshAlarmEntries()
	{
		if (mBluetoothDeviceAlarmManager != null)
		{
			mAlarmEntriesList.clear();
			for (BluetoothDeviceAlarmEntity entry : mBluetoothDeviceAlarmManager
					.getCurrentAlarmList())
			{
				final AlarmClockNode alarmClockNode = new AlarmClockNode(entry);
				mAlarmEntriesList.add(alarmClockNode);
			}
		}
		if (mAlarmEntriesList.size() == 0)
		{
			mTextViewToas.setVisibility(View.VISIBLE);
		} else
		{
			mTextViewToas.setVisibility(View.GONE);
		}
		if (mAlarmEntriesList != null
				&& mAlarmEntriesList.size() >= MAX_ALARM_SIZE)
		{
			maxSize = true;
		} else
		{
			maxSize = false;
		}
	}

	private void notifyDataChanged()
	{
		mAlarmClockAdapter.notifyDataSetChanged();
		if (dialog != null)
		{
			dialog.dismiss();
		}
	}

	private void initAlarmUiListener()
	{
		if(mBluetoothDeviceAlarmManager != null)
		{
			final AlertDialog dialog = createAlarmDialog();
			mBluetoothDeviceAlarmManager
			.setOnBluetoothDeviceAlarmUIChangedListener(new OnBluetoothDeviceAlarmUIChangedListener()
			{
				@Override
				// 蓝牙闹钟的状态
				public void onBluetoothDeviceAlarmUIChanged(int state)
				{
					flog.e((CustomApplication.getActivity() instanceof TimeDeviceLightActivity));
					if((CustomApplication.getActivity() instanceof TimeDeviceLightActivity))
					{
						if (state == 1)
						{
							// showAlarmDialog(createAlarmDialog());
							dialog.show();
						} else
						{
							// dismissAlarmDialog();
							dialog.dismiss();
						}
					}
				}
			});
		}
	}

	private AlertDialog createAlarmDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alarmclock);
		builder.setPositiveButton(R.string.alarmclock_snooze,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						mBluetoothDeviceAlarmManager.delay();
						dialog.dismiss();
						finish();
					}
				});
		builder.setNegativeButton(R.string.alarmclock_turnoff,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						mBluetoothDeviceAlarmManager.turnOff();
						dialog.dismiss();
					}
				});
		AlertDialog dg = builder.create();
		return dg;
	}

	private BluetoothDeviceAlarmEntity creatNewAlarmEntry()
	{
		boolean isHolded;
		int index = 1;
		for (int i = 1; i < 256; i++)
		{
			isHolded = false;
			for (BluetoothDeviceAlarmEntity entry : mBluetoothDeviceAlarmManager
					.getCurrentAlarmList())
			{
				if (entry.index == i)
				{
					isHolded = true;
					break;
				}
			}

			if (!isHolded)
			{
				index = i;
				break;
			}
		}
		BluetoothDeviceAlarmEntity entry = new BluetoothDeviceAlarmEntity();
		entry.title = getString(R.string.alarm_clock_title);// 闹钟
		entry.index = index;
		entry.ringType = 0;
		Calendar calendar = Calendar.getInstance();
		entry.hour = calendar.get(Calendar.HOUR_OF_DAY);
		entry.minute = calendar.get(Calendar.MINUTE);
		return entry;
	}

	// 跳转编辑闹钟界面
	private void changeToAlarmSetting(BluetoothDeviceAlarmEntity entry)
	{
		Intent intent = new Intent(this, TimeDeviceLightSettingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(IPCKey.ALARM_ENTRY, entry);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private class AlarmClockNode {

		public BluetoothDeviceAlarmEntity alarmEntry;

		AlarmClockNode(BluetoothDeviceAlarmEntity alarmEntry)
		{
			this.alarmEntry = alarmEntry;
		}

		public BluetoothDeviceAlarmEntity getAlarmEntry()
		{
			return alarmEntry;
		}
	}

	public class AlarmClockAdapter extends BaseAdapter {

		@Override
		public int getCount()
		{
			return mAlarmEntriesList.size();
		}

		@Override
		public AlarmClockNode getItem(int position)
		{
			return mAlarmEntriesList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		final class ViewHolder {
			TextView time;
			CheckBox off;
			RelativeLayout rl;
		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent)
		{
			ViewHolder holder = null;
			if (convertView == null)
			{
				convertView = LayoutInflater.from(TimeDeviceLightActivity.this)
						.inflate(R.layout.item_alarm_list, null);

				holder = new ViewHolder();
				holder.time = (MyTextView) convertView
						.findViewById(R.id.tv_alarm_time);
				holder.off = (CheckBox) convertView
						.findViewById(R.id.cb_toggle);
				holder.rl = (RelativeLayout) convertView
						.findViewById(R.id.relativelayout);
				convertView.setTag(holder);
			} else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			String minute = getItem(position).alarmEntry.minute + "";
			if (minute.length() == 1)
			{
				minute = "0" + minute;
			}
			String time = getItem(position).alarmEntry.hour + ":" + minute;
			holder.time.setText(time);
			holder.off.setChecked(getItem(position).alarmEntry.state);
			holder.rl.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					if (mListener != null)
					{
						mListener.onItemClick((AdapterView<?>) parent, v,
								position, v.getId());
					}
				}
			});
			final ViewHolder holder2 = holder;
			holder.off.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					// 如果是打开的话，点击后就会关闭所以，state ＝ true;
					if (holder2.off.isChecked())
					{
						getItem(position).alarmEntry.state = true;
					} else
					{
						getItem(position).alarmEntry.state = false;
					}
					mBluetoothDeviceAlarmManager
							.remove(getItem(position).alarmEntry);
					mBluetoothDeviceAlarmManager
							.set(getItem(position).alarmEntry);
				}
			});
			return convertView;
		}
	}

	private OnItemClickListener mListener;

	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mListener = listener;
	}

	public static class IPCKey {
		public static final String ALARM_ENTRY = "alarm.entry";
	}

}
