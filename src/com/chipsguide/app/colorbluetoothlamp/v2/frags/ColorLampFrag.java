package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.graphics.Color;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.ColorUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager.LampListener;
import com.chipsguide.app.colorbluetoothlamp.v2.view.ToastIsConnectDialog;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorImageView;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker.OnColorChangeListener;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker.OnSecondArcChangeListener;
import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager;
import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceCommonLampManager;
import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceCommonLampManager.OnBluetoothDeviceColdAndWarmWhiteChangedListener;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;
import com.chipsguide.lib.timer.util.MyLog;

@SuppressWarnings("unused")
public class ColorLampFrag extends BaseFragment implements
OnColorChangeListener, LampListener, OnClickListener,OnSecondArcChangeListener {
	// OnColorChangeListener, LampListener, OnClickListener, AnimationListener {
	// private PreferenceUtil mPreference;
	private LampManager mLampManager;
	private String TAG="ColorLampFrag";
	// private GridViewDIYColorAdapter diyColorAdapter;
	private ColorPicker mColorPicker;
	private CheckBox mLampCheckBox;
	private CheckBox mLampOnCheckBox;
	private RadioGroup mButtonGroupRhythm;
	private RadioButton mButtonLightNormal;
	private RadioButton mButtonLightRainbow;
	private RadioButton mButtonLightPusle;
	private RadioButton mButtonLightFlashing;
	private RadioButton mButtonLightCandle;
	// private GridView mGridViewDIYColor;
	// private ImageView mImageAddColor;

	private ColorImageView mColorImageViewr;
	private ColorImageView mColorImageViewg;
	private ColorImageView mColorImageViewb;
	private ColorImageView mColorImageViewy;

	private LinearLayout mLayoutSeekbar;
	private Animation shake;
	// private boolean isShake = false;

	private int mEffect = 0;// 当前的灯效
	private int color = -1;
	private boolean hmcolor = false;// 是否是手动控制颜色的变化。
	// 是白色吗
	private boolean isWhiteFlag = false;// 滑动的时候不在更新ui，只有在遥控器操作的时候才更新ui

	//	private SeekBar mSeekBarHeating;
	private int mSeekBarNum;
	// private List<String> colors = new ArrayList<String>();

	@Override
	protected void initBase() {
		mLampManager = LampManager.getInstance(getActivity());
		mLampManager.init();//蓝牙连接准备
		mLampManager.addOnBluetoothDeviceLampListener(this);//灯效监听状态
	}



	@Override
	protected int getLayoutId() {
		return R.layout.frag_color_lamp;
	}

	@Override
	protected void initView() {
		mColorPicker = (ColorPicker) findViewById(R.id.colorPicker);//色盘。亮度
		mColorPicker.setOnColorChangeListener(this);
		mColorPicker.setOnSecondArcListener(this);//冷暖白的进度条的监听

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

		mLampCheckBox = (CheckBox) this.findViewById(R.id.cb_lamp_active);//颜色盘开关
		mLampOnCheckBox = (CheckBox) this.findViewById(R.id.cb_lamp_on);//灯的开关
		mColorImageViewr = (ColorImageView) this.findViewById(R.id.color_r);//红
		mColorImageViewg = (ColorImageView) this.findViewById(R.id.color_g);//绿
		mColorImageViewb = (ColorImageView) this.findViewById(R.id.color_b);//蓝
		mColorImageViewy = (ColorImageView) this.findViewById(R.id.color_y);//黄
		mColorImageViewr.setImageviewColor(getResources().getColor(
				R.color.color_r));
		mColorImageViewg.setImageviewColor(getResources().getColor(
				R.color.color_g));
		mColorImageViewb.setImageviewColor(getResources().getColor(
				R.color.color_b));
		mColorImageViewy.setImageviewColor(getResources().getColor(
				R.color.color_y));

		mColorImageViewr.setOnClickListener(this);
		mColorImageViewg.setOnClickListener(this);
		mColorImageViewb.setOnClickListener(this);
		mColorImageViewy.setOnClickListener(this);
		mButtonLightNormal.setOnClickListener(this);
		mButtonLightRainbow.setOnClickListener(this);
		mButtonLightPusle.setOnClickListener(this);
		mButtonLightFlashing.setOnClickListener(this);
		mButtonLightCandle.setOnClickListener(this);
		mLampCheckBox.setOnClickListener(this);
		mLampOnCheckBox.setOnClickListener(this);
	}

	@Override//停止拖动
	public void onStopTrackingTouch(ColorPicker picker) {
		MyLog.i(TAG, "冷暖白灯的mSeekBarNum-->"+mSeekBarNum);
		System.out.println("冷暖白灯的停止拖动mSeekBarNum-->"+mSeekBarNum);
		mLampManager.setColdAndWarmWhite(mSeekBarNum);

	}
	@Override//拖动中
	public void onArcChanged(ColorPicker picker, int progress,
			boolean fromUser) {
		if(!fromUser){
			return;
		}
		
		mSeekBarNum=progress;
		System.out.println("打印当前拖动的值--------="+mSeekBarNum);

	}
	// 刷新冷暖白条
	private void refresh(int mSeekBarNum) {
		mColorPicker.setSecondProgress(mSeekBarNum);
	}

	@Override
	public void OnLampSeekBarNum(int SeekBarNum) {
		refresh(mSeekBarNum);
	}

	//是否白灯
	@Override
	public void LampSupportColdAndWhite(boolean filament) {
				MyLog.i(TAG,"判断是否白灯filament-----+="+filament);
						mColorPicker.setSecondProgressVisibility(filament);			
	}
	

	@Override
	public void onClick(View v) {
		// shake.cancel();
		if (v instanceof RadioButton) {
			effect(v);	//设置灯效
		}
		checkedbox(v.getId());//开关
		ColorImageView(v.getId());//设置颜色
		// addColor(v);
	}

	private void ColorImageView(int id) {
		switch (id) {
		case R.id.color_r:
			mLampManager.setColor(getResources().getColor(R.color.color_r));
			break;
		case R.id.color_g:
			mLampManager.setColor(getResources().getColor(R.color.color_g));
			break;
		case R.id.color_b:
			mLampManager.setColor(getResources().getColor(R.color.color_b));
			break;
		case R.id.color_y:
			mLampManager.setColor(getResources().getColor(R.color.color_y));
			break;
		}
	}
	//设置灯效
	private void effect(View v) {
		switch (v.getId()) {
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
	protected void initData() {
	}

	// 目前是根据a2dp来判断是否连接上的，用spp判断目前还存在问题
	// 现在的问题是spp还没有连接上就开始获取了，所以，获取的是null
	//刷新界面，要弹出连接蓝牙提示框
	@Override
	public void onResume() {
		super.onResume();
		if (CustomApplication.isFirstConnect
				&& (mLampManager.getBluetoothDevice() == null)) {
			ToastIsConnectDialog toastDialog = new ToastIsConnectDialog(
					getActivity());
			toastDialog.show();
			CustomApplication.isFirstConnect = false;
		}
	}

	private float[] colorHSV = new float[] { 0f, 0f, 1f };

	@Override
	public void onColorChange(int red, int green, int blue) {
	}

	@Override//颜色变化end
	public void onColorChangeEnd(int red, int green, int blue) {
		color = Color.rgb(red, green, blue);
		Color.RGBToHSV(red, green, blue, colorHSV);
		if (colorHSV[0] == 0 && colorHSV[1] == 0) { // 说明为白色
			float value = colorHSV[2];
			int rank = (int) (value * 16); // 等级1-16
			// TODO 调节等级
			// 白灯等级为1-16
			if (rank >= 16) {
				rank = 15;
			}
			hmcolor = true;
			isWhiteFlag = true;
			mLampManager.setBrightness(rank + 1);
		} else {
			mLampManager.setColor(red, green, blue);
		}
	}

	private void checkedbox(int id) {
		switch (id) {
		case R.id.cb_lamp_active:
			if (mLampCheckBox.isChecked()) {
				mLampManager.turnColorOn();//选中彩灯开
			} else {
				mLampManager.turnCommonOn();//未选中普通灯开  色值白色
				mColorPicker.setColor(getResources().getColor(R.color.white));
			}
			break;
		case R.id.cb_lamp_on: //灯的开关
			if (mLampOnCheckBox.isChecked()) {
				mLampManager.lampOn();
			} else {
				mLampManager.lampOff();
			}
			break;
		}
	}
	//查询 和回调

	@Override
	public void onLampStateInqiryBackChange(boolean colorState, boolean OnorOff) {
		flog.d("colorstate " + colorState + " OnorOff " + OnorOff);
		backChange(colorState, OnorOff, true);
	}
	@Override
	public void onLampStateFeedBackChange(boolean colorState, boolean OnorOff) {
		backChange(colorState, OnorOff, false);
	}

	/**
	 * @param colorState彩灯开关
	 * @param OnorOff总开关
	 * @param isWhite是否打开白灯
	 */
	private void backChange(boolean colorState, boolean OnorOff, boolean isWhite) {
		if (mLampCheckBox != null && mLampOnCheckBox != null) {
			mLampCheckBox.setChecked(colorState);
			mLampOnCheckBox.setChecked(OnorOff);
			if (!colorState) {//非彩灯
				if (isWhite || !hmcolor) {//白或者非自定义颜色，设置白色
					mColorPicker.setColor(getResources()
							.getColor(R.color.white));
				}
				if (!mButtonLightNormal.isChecked()) {
					mButtonLightNormal.setChecked(true);
				}
			}
		}
		hmcolor = false;
	}
	//灯效变化
	@Override
	public void onLampRhythmChange(int rhythm) {
		flog.d("rhythm  " + rhythm);
		switch (rhythm) {
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

	//设置灯的颜色
	@Override
	public void onLampColor(int red, int green, int blue) {
		mColorPicker.setColor(ColorUtil.int2Color(red, green, blue));
	}
	//设置灯的亮度
	@Override
	public void onLampBrightness(int brightness) {
		//转化为颜色会有一些误差的存在
		if(!isWhiteFlag)
		{
			if(!mLampManager.isColorLamp())
			{
				if(brightness == 1)
				{
					mColorPicker.setBrightness(0,CustomApplication.lampMax);
					return;
				}
				mColorPicker.setBrightness(brightness,CustomApplication.lampMax);
			}

		}else
		{
			isWhiteFlag = false;
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		mLampManager.removeOnBluetoothDeviceLampListener(this);
	}
}
