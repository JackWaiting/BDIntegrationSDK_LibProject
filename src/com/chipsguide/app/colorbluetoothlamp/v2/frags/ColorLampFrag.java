package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.graphics.Color;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker.OnColorChangeListener;
import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager;

@SuppressWarnings("unused")
public class ColorLampFrag extends BaseFragment implements
		OnColorChangeListener, OnCheckedChangeListener,
		android.widget.RadioGroup.OnCheckedChangeListener {

	private LampManager mLampManager;

	private RadioGroup mButtonGroupRhythm;
	private RadioButton mButtonLightNormal;
	private RadioButton mButtonLightRainbow;
	private RadioButton mButtonLightPusle;
	private RadioButton mButtonLightFlashing;
	private RadioButton mButtonLightCandle;

	private CheckBox mLampCheckBox;
	private CheckBox mLampOnCheckBox;
	private ImageView mColor01;
	private ImageView mColor02;
	private ImageView mColor03;
	private ImageView mColor04;
	private ImageView mColor05;

	private int color;
	private int mEffect = 0;// 当前的灯效

	@Override
	protected void initBase()
	{
		mLampManager = LampManager.getInstance(getActivity());
		mLampManager.init();
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.frag_color_lamp;
	}

	@Override
	protected void initView()
	{
		ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
		colorPicker.setOnColorChangeListener(this);

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
		mColor01 = (ImageView) this.findViewById(R.id.view_color_1);
		mColor02 = (ImageView) this.findViewById(R.id.view_color_2);
		mColor03 = (ImageView) this.findViewById(R.id.view_color_3);
		mColor04 = (ImageView) this.findViewById(R.id.view_color_4);
		mColor05 = (ImageView) this.findViewById(R.id.view_color_5);

		mButtonGroupRhythm.setOnCheckedChangeListener(this);
		mLampCheckBox.setOnCheckedChangeListener(this);
		mLampOnCheckBox.setOnCheckedChangeListener(this);
	}

	@Override
	protected void initData()
	{
	}

	@Override
	public void onColorChange(int alpha, int red, int green, int blue)
	{
		color = Color.argb(alpha, red, green, blue);
		// findViewById(R.id.layout).setBackgroundColor(color);
	}

	@Override
	public void onColorChangeEnd(int alpha, int red, int green, int blue)
	{
		mLampManager.setColor(red, green, blue);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		switch (buttonView.getId())
		{
		case R.id.cb_lamp_active:
			if (isChecked)
			{
				mLampManager.turnCommonOn();
			} else
			{
				mLampManager.turnColorOn();
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

		flog.e(mEffect);
		// 当前的灯效和快慢速度
		mLampManager.setLampEffect(mEffect);
	}

}
