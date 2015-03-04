package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.connect.ConnectDao;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view.RemoveDeviceDialog;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view.RenameDialog;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view.RemoveDeviceDialog.UpdateRemoveDebiceDialogClickListener;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view.RenameDialog.UpdateDialogClickListener;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class EditActivity extends BaseActivity implements OnClickListener {

	private TextView mRename;
	private TextView mDeleteDevice;

	private String btname = "BTLED";// 蓝牙名称
	private String address;
	private ConnectDao dao;
	private BluetoothDeviceManager sBluetoothDeviceManager;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_edit;
	}

	@Override
	public void initBase()
	{
		Intent intent = getIntent();
		btname = intent.getStringExtra("btname");
		address = intent.getStringExtra("address");

		dao = ConnectDao.getDao(this);
		sBluetoothDeviceManager = ((CustomApplication) getApplicationContext())
				.getBluetoothDeviceManager();
	}

	@Override
	public void initUI()
	{
		mRename = (TextView) this.findViewById(R.id.textview_rename);
		mDeleteDevice = (TextView) this
				.findViewById(R.id.textview_deletedevice);

		mRename.setOnClickListener(this);
		mDeleteDevice.setOnClickListener(this);
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
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.textview_rename:
			final RenameDialog renameDialog = new RenameDialog(EditActivity.this, btname);
			renameDialog
					.setOnUpdateDialogClickListener(new UpdateDialogClickListener()
					{
						@Override
						public void ItemDialogClickListener()
						{
							if (renameDialog != null)
							{
								String tag = renameDialog.getTag();
								if (tag.equals("") || tag == null)
								{
									showToast(R.string.name_not_null);
									return;
								}
								dao.updateChangeName(address, tag);
								dao.close();
								mRename.setText(tag);
							}
						}
					});
			renameDialog.show();
			break;
		case R.id.textview_deletedevice:
			final RemoveDeviceDialog removeDeviceDialog = new RemoveDeviceDialog(
					EditActivity.this, btname);
			removeDeviceDialog
					.setOnUpdateDialogClickListener(new UpdateRemoveDebiceDialogClickListener()
					{
						@Override
						public void ItemDialogClickListener()
						{
							dao.delete(address);
							dao.close();
							if (sBluetoothDeviceManager != null)
							{
								if (sBluetoothDeviceManager
										.getBluetoothDeviceConnectedSpp() == null)
								{

								} else if (address
										.equals(sBluetoothDeviceManager
												.getBluetoothDeviceConnectedSpp()
												.getAddress()))
								{
									if (sBluetoothDeviceManager != null)
									{
										sBluetoothDeviceManager
												.disconnect(sBluetoothDeviceManager
														.getBluetoothDeviceConnectedSpp());
									} else
									{
										sBluetoothDeviceManager = ((CustomApplication) getApplicationContext())
												.getBluetoothDeviceManager();
										sBluetoothDeviceManager
												.disconnect(sBluetoothDeviceManager
														.getBluetoothDeviceConnectedSpp());
									}
								}
							}
							finish();
						}
					});
			removeDeviceDialog.show();
			break;
		}
	}
}
