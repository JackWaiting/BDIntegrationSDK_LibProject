package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.GridViewDIYColorAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager.LampListener;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PreferenceUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.view.ToastIsConnectDialog;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker.OnColorChangeListener;
import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager;

@SuppressWarnings("unused")
public class ColorLampFrag extends BaseFragment implements
		OnColorChangeListener, LampListener, OnClickListener,AnimationListener {

	private PreferenceUtil mPreference;
	private LampManager mLampManager;
	private GridViewDIYColorAdapter diyColorAdapter;

	private ColorPicker mColorPicker;
	private CheckBox mLampCheckBox;
	private CheckBox mLampOnCheckBox;
	private RadioGroup mButtonGroupRhythm;
	private RadioButton mButtonLightNormal;
	private RadioButton mButtonLightRainbow;
	private RadioButton mButtonLightPusle;
	private RadioButton mButtonLightFlashing;
	private RadioButton mButtonLightCandle;
//	private GridView mGridViewDIYColor;
//	private ImageView mImageAddColor;
	
	private Animation shake;
	private boolean isShake = false;
	
	private int mEffect = 0;// 当前的灯效
	private int color = -1;
	
	private List<Integer> colors = new ArrayList<Integer>();

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
		
//		mGridViewDIYColor = (GridView)root.findViewById(R.id.gridview_diy_color);
//		diyColorAdapter = new GridViewDIYColorAdapter(getActivity());
//		mImageAddColor = (ImageView)this.findViewById(R.id.imageview_diy_addcolor);
//		mGridViewDIYColor.setAdapter(diyColorAdapter);
		
		shake = AnimationUtils.loadAnimation(getActivity(), R.anim.color_shake);//加载动画资源文件
		shake.setAnimationListener(this);
//		mGridViewDIYColor.setOnItemClickListener(new OnItemClickListener()
//		{
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View v, int position,
//					long arg3)
//			{
////				View view = (View)arg0.getItemAtPosition(position);
//			}
//		});
//		
//		mGridViewDIYColor.setOnItemLongClickListener(new OnItemLongClickListener()
//		{
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View v,
//					int position, long arg3)
//			{
//				View view = arg0.getChildAt(position);
//				if(!isShake)
//				{
//					view.startAnimation(shake);
//				}else
//				{
//					shake.cancel();
//				}
//				return false;
//			}
//		});
		
		
		mButtonLightNormal.setOnClickListener(this);
		mButtonLightRainbow.setOnClickListener(this);
		mButtonLightPusle.setOnClickListener(this);
		mButtonLightFlashing.setOnClickListener(this);
		mButtonLightCandle.setOnClickListener(this);

		mLampCheckBox.setOnClickListener(this);
		mLampOnCheckBox.setOnClickListener(this);
//		mImageAddColor.setOnClickListener(this);
	}
	
	

	@Override
	public void onClick(View v)
	{
		shake.cancel();
		if (v instanceof RadioButton)
		{
			effect(v);
		}
		checkedbox(v.getId());
		addColor(v);
	}

	private void addColor(View v)
	{
		switch (v.getId())
		{
		case R.id.imageview_diy_addcolor:
			if(colors.size()>=4)
			{
				//超出范围
			}else
			{
				colors.add(color);
				diyColorAdapter.setList(colors);
			}
			break;
		}
	}

	private void effect(View v)
	{
		switch (v.getId())
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

	// 目前是根据a2dp来判断是否连接上的，用spp判断目前还存在问题
	// 现在的问题是spp还没有连接上就开始获取了，所以，获取的是null
	@Override
	public void onResume()
	{
		super.onResume();
		if (CustomApplication.isFirstConnect
				&& (mLampManager.getBluetoothDevice() == null))
		{
			ToastIsConnectDialog toastDialog = new ToastIsConnectDialog(
					getActivity());
			toastDialog.show();
			CustomApplication.isFirstConnect = false;
		}
	}

	private float[] colorHSV = new float[] { 0f, 0f, 1f };

	@Override
	public void onColorChange(int red, int green, int blue)
	{
	}

	@Override
	public void onColorChangeEnd(int red, int green, int blue)
	{
		color = Color.rgb(red, green, blue);
		Color.RGBToHSV(red, green, blue, colorHSV);
		if (colorHSV[0] == 0 && colorHSV[1] == 0)
		{ // 说明为白色
			float value = colorHSV[2];
			int rank = (int) (value * 16); // 等级0-16
			// TODO 调节等级
			mLampManager.setBrightness(rank + 1);
		} else
		{
			mLampManager.setColor(red, green, blue);
		}
	}

	private void checkedbox(int id)
	{
		switch (id)
		{
		case R.id.cb_lamp_active:
			if (mLampCheckBox.isChecked())
			{
				mLampManager.turnColorOn();
			} else
			{
				mLampManager.turnCommonOn();
				mColorPicker.setColor(getResources().getColor(R.color.white));
			}
			break;
		case R.id.cb_lamp_on:
			if (mLampOnCheckBox.isChecked())
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

	@Override
	public void onLampRhythmChange(int rhythm)
	{
		flog.e("rhythm  " + rhythm);
		switch (rhythm)
		{
		case BluetoothDeviceColorLampManager.Effect.NORMAL:
			mButtonLightNormal.setChecked(true);
			break;
		case BluetoothDeviceColorLampManager.Effect.RAINBOW:
			mButtonLightRainbow.setChecked(true);
			break;
		case BluetoothDeviceColorLampManager.Effect.PULSE:
			mButtonLightPusle.setChecked(true);
			break;
		case BluetoothDeviceColorLampManager.Effect.FLASHING:
			mButtonLightFlashing.setChecked(true);
			break;
		case BluetoothDeviceColorLampManager.Effect.CANDLE:
			mButtonLightCandle.setChecked(true);
			break;
		default:
			mButtonLightNormal.setChecked(true);
			break;
		}
	}

	@Override
	public void onAnimationEnd(Animation animation)
	{
		isShake = false;
	}

	@Override
	public void onAnimationRepeat(Animation animation)
	{
		
	}

	@Override
	public void onAnimationStart(Animation animation)
	{
		isShake = true;
	}

	@Override
	public void onLampColor(int red, int green, int blue)
	{
		//mColorPicker.setColor(Color.rgb(red, green, blue));
	}

}
