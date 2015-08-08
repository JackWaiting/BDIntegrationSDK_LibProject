package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.BluetoothDeivcesListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.connect.ConnectDao;
import com.chipsguide.app.colorbluetoothlamp.v2.connect.ConnectInfo;
import com.chipsguide.app.colorbluetoothlamp.v2.connect.StringUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.view.DisconnectBluetoothDialog;
import com.chipsguide.app.colorbluetoothlamp.v2.view.ErrorToastDialog;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceConnectionStateChangedListener;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceDiscoveryListener;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;
//蓝牙连接
public class BluetoothConnectionActivity extends BaseActivity implements
		OnItemClickListener, OnBluetoothDeviceDiscoveryListener,OnBluetoothDeviceConnectionStateChangedListener {

	private ImageView mImageViewSearsh;//搜索
	private ListView mListView;
	private List<BluetoothDevice> mListBluetoothDevices = new ArrayList<BluetoothDevice>(); // 新搜索的蓝牙列表
	private ArrayList<ConnectInfo> connectBluetoothDevices;// 已经连接成功后的蓝牙列表
	private List<BluetoothDevice> listBluetooth = new ArrayList<BluetoothDevice>();// 过滤已经配对的蓝牙列表
	private BluetoothDeivcesListAdapter mAdapter;

	private CustomApplication application;
	private BluetoothDeviceManager mBluetoothDeviceManager;
	private BluetoothDevice bluetoothDeviceConnected;// 当前连接的蓝牙
	private BluetoothDeviceManagerProxy mManagerProxy;
	private PlayerManager mPlayerManager;
	private RotateAnimation anim;

	private ConnectDao dao;
	
	private boolean background = false;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_bluetooth_conn;
	}

	@Override
	public void initBase()
	{
		application = (CustomApplication) getApplication();
		mBluetoothDeviceManager = application.getBluetoothDeviceManager();
		mBluetoothDeviceManager.setOnBluetoothDeviceDiscoveryListener(this);
		dao = ConnectDao.getDao(this);
		mPlayerManager = PlayerManager.getInstance(this);
		mManagerProxy = BluetoothDeviceManagerProxy.getInstance(this);
	}

	@Override
	public void initUI()
	{
		mImageViewSearsh = (ImageView) this
				.findViewById(R.id.button_searsh);
		mListView = (ListView) this
				.findViewById(R.id.listview_bluetoothdevice_list);

		mImageViewSearsh.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void initData()
	{
		initBluetoothEnviroment();
	}

	/**
	 * 初始化设备的蓝牙环境
	 */
	private void initBluetoothEnviroment()
	{
		bluetoothDeviceConnected = mBluetoothDeviceManager
				.getBluetoothDeviceConnectedSpp();//保持蓝牙连接
		if (bluetoothDeviceConnected != null && bluetoothDeviceConnected.getAddress().startsWith(
				CustomApplication.MAC_ADDRESS_FILTER_PREFIX))
		{
			if(dao != null)
			{
				dao.insert(bluetoothDeviceConnected);
			}
		}
		// 从数据库中获取已经连接成功的蓝牙列表
		connectBluetoothDevices = dao.selectAll();
		mAdapter = new BluetoothDeivcesListAdapter(
				BluetoothConnectionActivity.this,
				StringUtil.getListConnectMessage(connectBluetoothDevices));
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void initListener()
	{//连接状态改变的监听
		mManagerProxy.addOnBluetoothDeviceConnectionStateChangedListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		CustomApplication.addActivity(this);
		background = false;
		connectBluetoothDevices = dao.selectAll();
		if (mBluetoothDeviceManager != null)
		{
			this.mBluetoothDeviceManager.setForeground(true);
			bluetoothDeviceConnected = mBluetoothDeviceManager
					.getBluetoothDeviceConnectedSpp();//保持蓝牙连接
			mAdapter.setBluetooth(bluetoothDeviceConnected);
		}
		mAdapter.setList(StringUtil.getListConnectMessage(
				connectBluetoothDevices, listBluetooth));
	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.button_searsh:
			startDiscovery();
			break;
		}
	}
	
	@Override
	public void onBluetoothDeviceDiscoveryFinished()
	{
		//搜索完毕
		if(anim != null)
		{
			anim.cancel();
		}
		mImageViewSearsh.setImageResource(R.drawable.selector_btn_searsh);
	}

	private void startDiscovery()
	{
		if (mBluetoothDeviceManager != null)
		{
			mBluetoothDeviceManager.startDiscovery();
			mImageViewSearsh.setImageResource(R.drawable.img_bt_searching);
			startAnim();
		}
	}
	
	private void startAnim()
	{
		anim = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(2000);
		mImageViewSearsh.startAnimation(anim);
	}

	@Override
	public void onBluetoothDeviceConnectionStateChanged(
			BluetoothDevice bluetoothDevice, int state)
	{
		switch (state)
		{
		
		// a2dp连接中
		case BluetoothDeviceManager.ConnectionState.A2DP_CONNECTING:
			setText(R.string.audio_connectioning);
			flog.d("A2DP_CONNECTING  a2dp连接中");
			break;

		// a2dp连接失败
		case BluetoothDeviceManager.ConnectionState.A2DP_FAILURE:
			flog.d("A2DP_FAILURE  a2dp连接失败");
			dismissConnectPD();
			break;

		// a2dp配对
		case BluetoothDeviceManager.ConnectionState.A2DP_PAIRING:
			flog.d("A2DP_PAIRING  a2dp配对中");
			break;

		// a2dp连接
		case BluetoothDeviceManager.ConnectionState.A2DP_CONNECTED:
			flog.d("A2DP_CONNECTED  a2dp连接成功");
			setText(R.string.audio_connectionend);
			break;

		// a2dp断开
		case BluetoothDeviceManager.ConnectionState.A2DP_DISCONNECTED:
			flog.d("A2DP_DISCONNECTED  a2dp断开");
			break;

		// spp连接中
		case BluetoothDeviceManager.ConnectionState.SPP_CONNECTING:
			flog.d("SPP_CONNECTING  spp连接中");
			setText(R.string.data_connectioning);
			break;

		// /spp连接成功
		case BluetoothDeviceManager.ConnectionState.SPP_CONNECTED:
			flog.d("SPP_CONNECTED spp连接成功");
			setText(R.string.data_connectionend);
			break;
			
			// spp断开
		case BluetoothDeviceManager.ConnectionState.SPP_DISCONNECTED:
			flog.d("SPP_DISCONNECTED  spp断开");
			break;

		// spp连接失败
		case BluetoothDeviceManager.ConnectionState.SPP_FAILURE:
			flog.d("SPP_FAILURE  spp连接失败");
			dismissConnectPD();
			break;

			
		// 连接
		case BluetoothDeviceManager.ConnectionState.CONNECTED:
			flog.d("CONNECTED  连接成功");
			bluetoothDeviceConnected = bluetoothDevice;
			// //如果连接了蓝牙设备，且匹配不了，就不添加到数据库
			if (bluetoothDevice.getAddress().startsWith(
					CustomApplication.MAC_ADDRESS_FILTER_PREFIX))
			{
				if(dao != null)
				{
					dao.insert(bluetoothDevice);
				}
			}
			listBluetooth.remove(bluetoothDevice);
			
			if(dao != null)
			{
				connectBluetoothDevices = dao.selectAll();
			}
			if(mAdapter!=null)
			{
				mAdapter.setBluetooth(bluetoothDevice);
				mAdapter.setList(StringUtil.getListConnectMessage(
						connectBluetoothDevices, listBluetooth));
			}
			setText(R.string.connectionend);
			dismissConnectPD();
			//连接成功后，返回到主界面
			finish();
			break;
		// 断开
		case BluetoothDeviceManager.ConnectionState.DISCONNECTED:
			flog.d("DISCONNECTED  断开连接");
			if(mAdapter!=null)
			{
				mAdapter.setBluetooth(null);
				mAdapter.notifyDataSetChanged();
			}
			//断开如果正在播放停止音乐
			if(mPlayerManager.isPlaying())
			{
				mPlayerManager.pause();
			}
			dismissConnectPD();
		case BluetoothDeviceManager.ConnectionState.TIMEOUT:
		case BluetoothDeviceManager.ConnectionState.CAN_NOT_CONNECT_INSIDE_APP:
			flog.d("CAN_NOT_CONNECT_INSIDE_APP 未连接成功");
			dismissConnectPD();
			ErrorToastDialog toastDialog = new ErrorToastDialog(this,
					R.style.full_screen);
			if(!background && CustomApplication.isToastDisconn)
			{
				toastDialog.show();
			}
			// 提示，由于系统原因或者未知原因，应用内无法连接蓝牙，请自行在系统中连接设备，回到应用即可。
			break;
				
		}
	}
	
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		background = true;
	}

	@Override
	public void onBluetoothDeviceDiscoveryFound(BluetoothDevice bluetoothDevices)
	{
		flog.d("onBluetoothDeviceFound  扫描发现设备:"
				+ bluetoothDevices.getAddress());
		// 搜索到的新列表
		mListBluetoothDevices.add(bluetoothDevices);
		// 从数据库中获取已经连接成功的蓝牙列表
		connectBluetoothDevices = dao.selectAll();

		if (connectBluetoothDevices.size() == 0)
		{
			listBluetooth = mListBluetoothDevices;
		} else
		{
			// 过滤掉已经配对的设备
			for (int i = 0; i < mListBluetoothDevices.size(); i++)
			{
				String address = mListBluetoothDevices.get(i).getAddress();
				for (int j = 0; j < connectBluetoothDevices.size(); j++)
				{
					String connectaddress = connectBluetoothDevices.get(j)
							.getMac_address();
					if (!address.equals(connectaddress))
					{
						listBluetooth.add(mListBluetoothDevices.get(i));
						break;
					}
				}
			}
		}
		listBluetooth = StringUtil.removeDuplicateWithOrder(listBluetooth);
		flog.d("listBluetooth " +listBluetooth.size());
		mAdapter.setList(StringUtil.getListConnectMessage(
				connectBluetoothDevices, listBluetooth));
	}

	@Override
	public void onBluetoothDeviceDiscoveryStarted()
	{
		//开始搜索
		mListBluetoothDevices.clear();
		connectBluetoothDevices.clear();
		listBluetooth.clear();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		if (connectBluetoothDevices.size() != 0
				|| (this.listBluetooth.size() > 0))
		{
			ConnectInfo connectInfo = null;
			BluetoothDevice bluetoothDevice = null;
			try
			{
				if (connectBluetoothDevices.size() > position)
				{
					connectInfo = connectBluetoothDevices.get(position);
					bluetoothDevice = BluetoothAdapter.getDefaultAdapter()
							.getRemoteDevice(connectInfo.getMac_address());
				} else
				{
					if (this.listBluetooth != null)
					{
						createConnPD();
						// 当点击连接的时候停止搜索
						if (mBluetoothDeviceManager.isDiscovering())
						{
							mBluetoothDeviceManager.cancelDiscovery();
						}
						flog.d(listBluetooth.get(
								position - connectBluetoothDevices.size())
								.getAddress()
								+ "     检测蓝牙连接");
						this.mBluetoothDeviceManager
								.connect(listBluetooth.get(position
										- connectBluetoothDevices.size()));
						return;
					}
				}
			} catch (Exception e)
			{
				flog.e(e);
				return;
			}

			if (BluetoothAdapter.checkBluetoothAddress(bluetoothDevice
					.getAddress()))
			{
				BluetoothDevice deviceConnected = mBluetoothDeviceManager
						.getBluetoothDeviceConnectedSpp();
				if (deviceConnected != null)
				{
					if (bluetoothDevice.getAddress().equals(
							deviceConnected.getAddress()))
					{
						// 断开蓝牙
						 DisconnectBluetoothDialog diacoonctDialog = new DisconnectBluetoothDialog(BluetoothConnectionActivity.this,
						 R.style.register_inform_style,
						 bluetoothDevice, this.mBluetoothDeviceManager);
						 diacoonctDialog.show();
					} else
					{
						if (bluetoothDevice != null)
						{
							// 当点击连接的时候停止搜索
							if (mBluetoothDeviceManager.isDiscovering())
							{
								mBluetoothDeviceManager.cancelDiscovery();
							}
							this.mBluetoothDeviceManager
									.connect(bluetoothDevice);
						}
					}
				} else
				{
					if (bluetoothDevice != null)
					{
						createConnPD();
						// 当点击连接的时候停止搜索
						if (mBluetoothDeviceManager.isDiscovering())
						{
							mBluetoothDeviceManager.cancelDiscovery();
						}
						this.mBluetoothDeviceManager.connect(bluetoothDevice);
					}
				}
			} else
			{
				showToast(R.string.bluetooth_not_available);
			}
		}
	}
	
	@Override
	public void updateConnectState()
	{
	}

	@Override
	public void updateAlarm(int state)
	{
		if(CustomApplication.getActivity() == this)
		{
			if(state == 1)
			{
				createAlarmToast();
			}else if(state == 0)
			{
				dismissAlarmDialog();
			}else
			{
				dismissAlarmDialog();
			}
		}
	}
	
	public void onDestroy() 
	{
		super.onDestroy();
		dismissConnectPD();
		mManagerProxy.removeDeviceConnectedStateChangedListener();
	}
}
