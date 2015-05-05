package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.FormatHelper;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.view.TitleView;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class SleepAssistantActivity extends BaseActivity implements
		OnCheckedChangeListener {

	private TextView mShowTimeTextView;
	private ProgressBar mProgressBar;
	private TextView mConfirmationTextView;
	private RadioGroup mSleepTimeRadiogroup;
	private RadioButton mButton10ReadioButton;
	private RadioButton mButton20ReadioButton;
	private RadioButton mButton30ReadioButton;
	private RadioButton mButton60ReadioButton;
	private RadioButton mButton90ReadioButton;
	private TitleView mSleepTitleview;

	private LinearLayout mVisibilityProgressbarLinearLayout;
	private LinearLayout mVisibilitySelectorTimeLinearLayout;

	private PlayerManager playerManager;
	private LampManager mLampManager;
	private AudioManager mAudioManager;
	private CustomApplication application;
	private BluetoothDeviceManager mBluetoothDeviceManager;
	private int current;// 当前音量
	private int max;// 最大音量
	private MyCount mCount;
	private int mColorTextDown;
	private int mColorTextNor;
	private int time = 10;
	private int TIME_GAP = 6;// 分6段减小音量
	private String [] sleep_time;

	private boolean mSleepMode = false;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_sleep_assistant;
	}

	@Override
	public void initBase()
	{
		mColorTextDown = getResources().getColor(R.color.color_blue);
		mColorTextNor = getResources().getColor(R.color.white);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		playerManager = PlayerManager.getInstance(getApplicationContext());
		mLampManager = LampManager.getInstance(getApplicationContext());
		sleep_time = getResources().getStringArray(R.array.sleep_time);
		application = (CustomApplication) getApplication();
		mBluetoothDeviceManager = application.getBluetoothDeviceManager();
	}

	@Override
	public void initUI()
	{
		mSleepTitleview = (TitleView) this.findViewById(R.id.titleview_sleep);
		mShowTimeTextView = (TextView) this
				.findViewById(R.id.textview_show_time);
		mShowTimeTextView.setTypeface(Typeface.createFromAsset(getAssets(),"font/digifaw.ttf"));
		mProgressBar = (ProgressBar) this.findViewById(R.id.progressBar);
		mConfirmationTextView = (TextView) this
				.findViewById(R.id.textview_confirmation);
		mSleepTimeRadiogroup = (RadioGroup) this
				.findViewById(R.id.radiogroup_slect_sleeptime);
		mButton10ReadioButton = (RadioButton) this
				.findViewById(R.id.readioButton_button_10);
		mButton20ReadioButton = (RadioButton) this
				.findViewById(R.id.readioButton_button_20);
		mButton30ReadioButton = (RadioButton) this
				.findViewById(R.id.readioButton_button_30);
		mButton60ReadioButton = (RadioButton) this
				.findViewById(R.id.readioButton_button_60);
		mButton90ReadioButton = (RadioButton) this
				.findViewById(R.id.readioButton_button_90);

		mVisibilityProgressbarLinearLayout = (LinearLayout) this
				.findViewById(R.id.linearlayout_visibility_progressbar);
		mVisibilitySelectorTimeLinearLayout = (LinearLayout) this
				.findViewById(R.id.linearlayout_visibility_selector_time);

		mSleepTimeRadiogroup.setOnCheckedChangeListener(this);
		mConfirmationTextView.setOnClickListener(this);
	}

	@Override
	public void initData()
	{
		mSleepTitleview.setRightBtnVisibility(false);
		mButton10ReadioButton.setText(sleep_time[0]);
		mButton20ReadioButton.setText(sleep_time[1]);
		mButton30ReadioButton.setText(sleep_time[2]);
		mButton60ReadioButton.setText(sleep_time[3]);
		mButton90ReadioButton.setText(sleep_time[4]);
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
		case R.id.textview_confirmation:
			if (mSleepMode)
			{
				cancelSleep();
				mSleepMode = false;
			} else
			{
				mConfirmationTextView.setText(getResources().getString(
						R.string.confirmation_cancel));
				mSleepTitleview.setLiftBtnVisibility(false);
				mVisibilityProgressbarLinearLayout.setVisibility(View.VISIBLE);
				mVisibilitySelectorTimeLinearLayout
						.setVisibility(View.INVISIBLE);
				if (mCount != null)
				{
					mCount.cancel();
					mCount = null;
				}
				mCount = new MyCount(60000 * time, 1000);
				mShowTimeTextView.setText(FormatHelper
						.formatLongToTimeMinuteStr((long) time * 60000));
				mCount.start();
				mSleepMode = true;
			}
			break;
		}
	}

	private void cancelSleep()
	{
		mConfirmationTextView.setText(getResources().getString(
				R.string.confirmation));
		mSleepTitleview.setLiftBtnVisibility(true);
		mVisibilityProgressbarLinearLayout
				.setVisibility(View.INVISIBLE);
		mVisibilitySelectorTimeLinearLayout.setVisibility(View.VISIBLE);
		if (mCount != null)
		{
			mCount.cancel();
		}
		mShowTimeTextView.setText("88:88");
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		switch (group.getId())
		{
		case R.id.radiogroup_slect_sleeptime:
			selectTime(checkedId);
			break;
		}
	}

	private void selectTime(int checkedId)
	{
		mButton10ReadioButton.setTextColor(mColorTextNor);
		mButton20ReadioButton.setTextColor(mColorTextNor);
		mButton30ReadioButton.setTextColor(mColorTextNor);
		mButton60ReadioButton.setTextColor(mColorTextNor);
		mButton90ReadioButton.setTextColor(mColorTextNor);
		switch (checkedId)
		{
		case R.id.readioButton_button_10:
			time = 10;
			mButton10ReadioButton.setTextColor(mColorTextDown);
			break;
		case R.id.readioButton_button_20:
			time = 20;
			mButton20ReadioButton.setTextColor(mColorTextDown);
			break;
		case R.id.readioButton_button_30:
			time = 30;
			mButton30ReadioButton.setTextColor(mColorTextDown);
			break;
		case R.id.readioButton_button_60:
			time = 60;
			mButton60ReadioButton.setTextColor(mColorTextDown);
			break;
		case R.id.readioButton_button_90:
			time = 90;
			mButton90ReadioButton.setTextColor(mColorTextDown);
			break;
		}
		mProgressBar.setProgress(0);
		mShowTimeTextView.setText(FormatHelper
				.formatLongToTimeMinuteStr((long) time * 60 * 1000));
	}

	private void setSleepSound(long millis)
	{
		int cl = (time * 60) / TIME_GAP;
		for (int i = 1; i <= TIME_GAP; i++)
		{
			if ((millis == cl * i))
			{
				if(mSubject.getConnectState())
				{
					current = mSubject.getVolume();
					// 如果分段的段数超过了音量最大可以加上这个，最大音量为15
					if (current < TIME_GAP && TIME_GAP < max)
					{
						current = TIME_GAP;
					}
					if(mBluetoothDeviceManager!=null)
					{
						mBluetoothDeviceManager.setVolume((current / TIME_GAP) * i);
					}
				}else
				{
					flog.d("音量为：" + (current / TIME_GAP) * i + " i: " + i
							+ " current: " + current);
					// 如果分段的段数超过了音量最大可以加上这个，最大音量为15
					if (current < TIME_GAP && TIME_GAP < max)
					{
						current = TIME_GAP;
					}
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							(current / TIME_GAP) * i, 0);
				}
			}
		}
	}

	/* 定义一个倒计时的内部类 */
	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish()
		{
			cancelSleep();
			playerManager.pause();
			mLampManager.lampOff();
		}

		@Override
		public void onTick(long millisUntilFinished)
		{
			long millis = millisUntilFinished / 1000;
			double x = (time * 60 - (int) millis) / 1.0;// 预留小数点
			mProgressBar.setProgress((int) ((x / (time * 60)) * 100));
			mShowTimeTextView.setText(FormatHelper
					.formatLongToTimeMinuteStr(millisUntilFinished));
			setSleepSound(millis);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return true;
	}

	// 屏蔽返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(mSleepMode)
			{
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void updateVolume(int volume)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void updateConnectState(boolean isConnect)
	{
		// TODO 蓝牙断开处理
		if(!isConnect)
		{
			cancelSleep();
			mSleepMode = false;
		}
	}
	
}
