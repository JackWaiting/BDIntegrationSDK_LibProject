package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.graphics.Color;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker.OnColorChangeListener;

public class ColorLampFrag extends BaseFragment implements
		OnColorChangeListener,OnCheckedChangeListener {
	
	private LampManager mLampManager;
	
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

	@Override
	public void onColorChange(int alpha, int red, int green, int blue)
	{
		color = Color.argb(alpha, red, green, blue);
//		findViewById(R.id.layout).setBackgroundColor(color);
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
			if(isChecked)
			{
				mLampManager.turnColorOn();
			}else
			{
				mLampManager.turnCommonOn();
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

}
