package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.graphics.Color;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager.LampListener;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker.OnColorChangeListener;

public class ColorLampFrag extends BaseFragment implements
		OnColorChangeListener,OnCheckedChangeListener ,LampListener{
	
	private LampManager mLampManager;
	
	private ColorPicker mColorPicker;
	private CheckBox mLampCheckBox;
	private CheckBox mLampOnCheckBox;
	private ImageView mColor01;
	private ImageView mColor02;
	private ImageView mColor03;
	private ImageView mColor04;
	private ImageView mColor05;

	private int color;
	
	@Override
	protected void initBase()
	{
		mLampManager = LampManager.getInstance(getActivity());
		mLampManager.init();
		mLampManager.setLampListener(this);
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
		
		mLampCheckBox = (CheckBox)this.findViewById(R.id.cb_lamp_active);
		mLampOnCheckBox = (CheckBox)this.findViewById(R.id.cb_lamp_on);
		mColor01 = (ImageView)this.findViewById(R.id.view_color_1);
		mColor02 = (ImageView)this.findViewById(R.id.view_color_2);
		mColor03 = (ImageView)this.findViewById(R.id.view_color_3);
		mColor04 = (ImageView)this.findViewById(R.id.view_color_4);
		mColor05 = (ImageView)this.findViewById(R.id.view_color_5);
		mLampCheckBox.setOnCheckedChangeListener(this);
		mLampOnCheckBox.setOnCheckedChangeListener(this);
	}

	@Override
	protected void initData()
	{
	}

	private float[] colorHSV = new float[] { 0f, 0f, 1f };
	@Override
	public void onColorChange(int red, int green, int blue)
	{
		color = Color.argb(0, red, green, blue);
		Color.RGBToHSV(red, green, blue, colorHSV);
		if(colorHSV[0] == 0 && colorHSV[1] == 0){ //说明为白色
			float value = colorHSV[2];
			int rank = (int)(value * 16); //等级0-16
			//TODO 调节等级
			mLampManager.setBrightness(rank+1);
		}
	}

	@Override
	public void onColorChangeEnd(int red, int green, int blue)
	{
		color = Color.argb(0, red, green, blue);
		Color.RGBToHSV(red, green, blue, colorHSV);
		if(colorHSV[0] == 0 && colorHSV[1] == 0)
		{ //说明为白色
		}else
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
			if(isChecked)
			{
				mLampManager.turnColorOn();
			}else
			{
				mLampManager.turnCommonOn();
				mColorPicker.setColor(getResources().getColor(R.color.white));
			}
			break;
		case R.id.cb_lamp_on:
			if(isChecked)
			{
				mLampManager.lampOn();
			}else
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
