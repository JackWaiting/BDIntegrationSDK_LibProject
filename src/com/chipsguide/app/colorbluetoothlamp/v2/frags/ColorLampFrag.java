package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager.LampListener;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PreferenceUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.view.ToastIsConnectDialog;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.CircleImageView;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker.OnColorChangeListener;
import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager;

@SuppressWarnings("unused")
public class ColorLampFrag extends BaseFragment implements
		OnColorChangeListener, OnCheckedChangeListener, LampListener,
		android.widget.RadioGroup.OnCheckedChangeListener, OnClickListener,OnLongClickListener {

	private PreferenceUtil mPreference;
	private LampManager mLampManager;

	private ColorPicker mColorPicker;
	private CheckBox mLampCheckBox;
	private CheckBox mLampOnCheckBox;
	private CircleImageView mColor01;
	private CircleImageView mColor02;
	private CircleImageView mColor03;
	private CircleImageView mColor04;
	private CircleImageView mColor05;
	private RadioGroup mButtonGroupRhythm;
	private RadioButton mButtonLightNormal;
	private RadioButton mButtonLightRainbow;
	private RadioButton mButtonLightPusle;
	private RadioButton mButtonLightFlashing;
	private RadioButton mButtonLightCandle;

	private int color;

	private int mEffect = 0;// 当前的灯效

	@Override
	protected void initBase()
	{
		mLampManager = LampManager.getInstance(getActivity());
		mLampManager.init();
		mLampManager.setLampListener(this);
		mPreference = PreferenceUtil.getIntance(getActivity());
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.frag_color_lamp;
	}

	@Override
	protected void initView()
	{
		mColorPicker = (ColorPicker) findViewById(R.id.colorPicker);
		mColorPicker.setOnColorChangeListener(this);

		mButtonGroupRhythm = (RadioGroup) root
				.findViewById(R.id.radiogroup_rhythm_effect);
		mButtonLightNormal = (RadioButton) root
				.findViewById(R.id.readioButton_button_light_normal);
		mButtonLightRainbow = (RadioButton) root
				.findViewById(R.id.readioButton_button_light_rainbow);
		mButtonLightPusle = (RadioButton) root
				.findViewById(R.id.readioButton_button_light_pulse);
		mButtonLightFlashing = (RadioButton) root
				.findViewById(R.id.readioButton_button_light_flashing);
		mButtonLightCandle = (RadioButton) root
				.findViewById(R.id.readioButton_button_light_candle);

		mLampCheckBox = (CheckBox) this.findViewById(R.id.cb_lamp_active);
		mLampOnCheckBox = (CheckBox) this.findViewById(R.id.cb_lamp_on);
		mColor01 = (CircleImageView) this.findViewById(R.id.view_color_1);
		mColor02 = (CircleImageView) this.findViewById(R.id.view_color_2);
		mColor03 = (CircleImageView) this.findViewById(R.id.view_color_3);
		mColor04 = (CircleImageView) this.findViewById(R.id.view_color_4);
		mColor05 = (CircleImageView) this.findViewById(R.id.view_color_5);

		mColor01.setOnClickListener(this);
		mColor02.setOnClickListener(this);
		mColor03.setOnClickListener(this);
		mColor04.setOnClickListener(this);
		mColor05.setOnClickListener(this);
		mColor01.setOnLongClickListener(this);
		mColor02.setOnLongClickListener(this);
		mColor03.setOnLongClickListener(this);
		mColor04.setOnLongClickListener(this);
		mColor05.setOnLongClickListener(this);
		mButtonGroupRhythm.setOnCheckedChangeListener(this);
		
		mLampCheckBox.setOnCheckedChangeListener(this);
		mLampOnCheckBox.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.view_color_1:

			break;
		case R.id.view_color_2:

			break;
		case R.id.view_color_3:

			break;
		case R.id.view_color_4:

			break;
		case R.id.view_color_5:
//			mColor01.setImageResource(R.color.);
//			mColor01.setImageBitmap(new Bitmap);
//			mColor01.setImageDrawable(new BitmapDrawable());
//			mPreference.saveLampColor1(color);
			break;
		}
	}
	
	@Override
	public boolean onLongClick(View v)
	{
		switch (v.getId())
		{
		case R.id.view_color_1:

			break;
		case R.id.view_color_2:

			break;
		case R.id.view_color_3:

			break;
		case R.id.view_color_4:

			break;
		case R.id.view_color_5:

			break;
		}
		return false;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		switch (checkedId)
		{
		case R.id.readioButton_button_light_normal:
			flog.d("normal");
			mEffect = BluetoothDeviceColorLampManager.Effect.NORMAL;
			break;
		case R.id.readioButton_button_light_rainbow:
			flog.d("rainbow");
			mEffect = BluetoothDeviceColorLampManager.Effect.RAINBOW;
			break;
		case R.id.readioButton_button_light_pulse:
			flog.d("pusle");
			mEffect = BluetoothDeviceColorLampManager.Effect.PULSE;
			break;
		case R.id.readioButton_button_light_flashing:
			flog.d("flashing");
			mEffect = BluetoothDeviceColorLampManager.Effect.FLASHING;
			break;
		case R.id.readioButton_button_light_candle:
			flog.d("candle");
			mEffect = BluetoothDeviceColorLampManager.Effect.CANDLE;
			break;
		}

		// 当前的灯效和快慢速度
		mLampManager.setLampEffect(mEffect);
	}

	@Override
	protected void initData()
	{
	}
	
	//目前是根据a2dp来判断是否连接上的，用spp判断目前还存在问题
	//现在的问题是spp还没有连接上就开始获取了，所以，获取的是null
	@Override
	public void onResume()
	{
		super.onResume();
		if(CustomApplication.isFirstConnect && (mLampManager.getBluetoothDevice() == null))
		{
			ToastIsConnectDialog toastDialog = new ToastIsConnectDialog(getActivity());
			toastDialog.show();
			CustomApplication.isFirstConnect = false;
		}
	}

	private float[] colorHSV = new float[] { 0f, 0f, 1f };

	@Override
	public void onColorChange(int red, int green, int blue)
	{
		color = Color.argb(0, red, green, blue);
		Color.RGBToHSV(red, green, blue, colorHSV);
		if (colorHSV[0] == 0 && colorHSV[1] == 0)
		{ // 说明为白色
			float value = colorHSV[2];
			int rank = (int) (value * 16); // 等级0-16
			// TODO 调节等级
			mLampManager.setBrightness(rank + 1);
		}
	}

	@Override
	public void onColorChangeEnd(int red, int green, int blue)
	{
		color = Color.argb(0, red, green, blue);
		Color.RGBToHSV(red, green, blue, colorHSV);
		if (colorHSV[0] == 0 && colorHSV[1] == 0)
		{ // 说明为白色
		} else
		{
			mLampManager.setColor(red, green, blue);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		switch (buttonView.getId())
		{
		case R.id.cb_lamp_active:
			if (isChecked)
			{
				mLampManager.turnColorOn();
			} else
			{
				mLampManager.turnCommonOn();
				mColorPicker.setColor(getResources().getColor(R.color.white));
			}
			break;
		case R.id.cb_lamp_on:
			if (isChecked)
			{
				mLampManager.lampOn();
			} else
			{
				mLampManager.lampOff();
			}
			break;
		}
	}

	@Override
	public void onLampStateInqiryBackChange(boolean colorState, boolean OnorOff)
	{
		flog.d("colorstate " + colorState + " OnorOff " + OnorOff);
		if (mLampCheckBox != null && mLampOnCheckBox != null)
		{
			mLampCheckBox.setChecked(colorState);
			mLampOnCheckBox.setChecked(OnorOff);
		}
	}

	@Override
	public void onLampStateFeedBackChange(boolean colorState, boolean OnorOff)
	{
		onLampStateInqiryBackChange(colorState, OnorOff);
	}

}
