package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.adapter;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.activity.EditActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.connect.ConnectMessage;

public class BluetoothDeivcesListAdapter extends BaseAdapter {

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private List<ConnectMessage> list;
	private LayoutInflater mInflater;
	private BluetoothDevice device;
	private Context mContext;

	public BluetoothDeivcesListAdapter(Context context,
			List<ConnectMessage> list)
	{
		mContext = context;
		this.list = list;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setList(List<ConnectMessage> list)
	{
		this.list = list;
		notifyDataSetChanged();
	}

	public void setBluetooth(BluetoothDevice device)
	{
		this.device = device;
	}

	public void setConnectMessage(ConnectMessage msg)
	{
		list.add(msg);
		notifyDataSetChanged();
	}

	/**
	 * 返回所有的layout的数量
	 * 
	 * */
	@Override
	public int getViewTypeCount()
	{
		return TYPE_MAX_COUNT;
	}

	/**
	 * 根据数据源的position返回需要显示的的layout的type
	 * 
	 * type的值必须从0开始
	 * 
	 * */
	@Override
	public int getItemViewType(int position)
	{
		ConnectMessage msg = list.get(position);
		int type = msg.getType();
		return type;
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2)
	{
		final ConnectMessage msg = list.get(position);
		int type = getItemViewType(position);
		ViewHolderItem mViewHolderItem = null;
		ViewHolderItemConnect mViewHolderItemConnect = null;
		if (convertView == null)
		{
			switch (type)
			{
			case TYPE_ITEM:
				mViewHolderItem = new ViewHolderItem();
				convertView = mInflater.inflate(
						R.layout.item_listview_bluetooth_devices, null);
				mViewHolderItem.mTtextView = (TextView) convertView
						.findViewById(R.id.textview_device_name_item_listview_bluetooth_devices);
				if (null == msg.getBluetoothName()
						|| "".equals(msg.getBluetoothName()))
				{
					mViewHolderItem.mTtextView.setText(msg.getAddress());
				} else
				{
					mViewHolderItem.mTtextView.setText(msg.getBluetoothName());
				}
				convertView.setTag(mViewHolderItem);
				break;
			case TYPE_SEPARATOR:
				mViewHolderItemConnect = new ViewHolderItemConnect();
				convertView = mInflater.inflate(
						R.layout.item_listview_connect_bluetooth_devices, null);
				mViewHolderItemConnect.mTimeView = (TextView) convertView
						.findViewById(R.id.textview_device_name_item);
				mViewHolderItemConnect.isConnect = (TextView) convertView
						.findViewById(R.id.textview_device_connect);
				if (null == msg.getBluetoothName()
						|| "".equals(msg.getBluetoothName()))
				{
					mViewHolderItemConnect.mTimeView.setText(msg.getAddress());
				} else
				{
					mViewHolderItemConnect.mTimeView.setText(msg
							.getBluetoothName());
				}
				String messageConnect = mContext.getResources().getString(
						R.string.not_connect);
				if (device != null)
				{
					if (msg.getAddress().equals(device.getAddress()))
					{
						messageConnect = mContext.getResources().getString(
								R.string.connect);
					}
				}
				mViewHolderItemConnect.isConnect.setText(messageConnect);
				mViewHolderItemConnect.mCheckBoxConnect = (CheckBox) convertView
						.findViewById(R.id.device_item_button);
				convertView.setTag(mViewHolderItemConnect);
				mViewHolderItemConnect.mCheckBoxConnect
						.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View v)
							{
								Intent intent = new Intent(mContext,
										EditActivity.class);
								intent.putExtra("btname",
										msg.getBluetoothName());
								intent.putExtra("address", msg.getAddress());
								mContext.startActivity(intent);
							}
						});
				break;
			}
		} else
		{
			switch (type)
			{
			case TYPE_ITEM:
				mViewHolderItem = (ViewHolderItem) convertView.getTag();
				if (null == msg.getBluetoothName()
						|| "".equals(msg.getBluetoothName()))
				{
					mViewHolderItem.mTtextView.setText(msg.getAddress());
				} else
				{
					mViewHolderItem.mTtextView.setText(msg.getBluetoothName());
				}
				break;
			case TYPE_SEPARATOR:
				mViewHolderItemConnect = (ViewHolderItemConnect) convertView
						.getTag();
				if (null == msg.getBluetoothName()
						|| "".equals(msg.getBluetoothName()))
				{
					mViewHolderItemConnect.mTimeView.setText(msg.getAddress());
				} else
				{
					mViewHolderItemConnect.mTimeView.setText(msg
							.getBluetoothName());
				}
				String messageConnect = mContext.getResources().getString(
						R.string.not_connect);
				if (device != null)
				{
					if (msg.getAddress().equals(device.getAddress()))
					{
						messageConnect = mContext.getResources().getString(
								R.string.connect);
					}
				}

				mViewHolderItemConnect.isConnect.setText(messageConnect);
				mViewHolderItemConnect.mCheckBoxConnect = (CheckBox) convertView
						.findViewById(R.id.device_item_button);

				mViewHolderItemConnect.mCheckBoxConnect
						.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View v)
							{
								Intent intent = new Intent(mContext,
										EditActivity.class);
								intent.putExtra("btname",
										msg.getBluetoothName());
								intent.putExtra("address", msg.getAddress());
								mContext.startActivity(intent);
							}
						});
				break;
			}
		}
		return convertView;
	}

	class ViewHolderItem {
		private TextView mTtextView;
	}

	class ViewHolderItemConnect {
		private TextView mTimeView;
		private TextView isConnect;
		private CheckBox mCheckBoxConnect;
	}

}
