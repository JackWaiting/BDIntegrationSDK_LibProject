package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.MainFragment;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.NavFrag;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.MainFragment.OnMainPageChangeListener;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.NavFrag.OnNavItemClickListener;
import com.chipsguide.app.colorbluetoothlamp.v2.view.TextSwitcherTitleView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class MainActivity extends BaseActivity implements OnNavItemClickListener, OnMainPageChangeListener{
	private FragmentManager fragManager;
	private NavFrag navFrag;
	
	private TextSwitcherTitleView titleView;
	
	@Override
	public int getLayoutId() {
		return R.layout.activity_main;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			FragmentTransaction transaction = fragManager.beginTransaction();
			navFrag = new NavFrag();
			transaction.replace(R.id.menu_frame, navFrag);
			transaction.commit();
		} else {
			navFrag = (NavFrag) this.getSupportFragmentManager()
					.findFragmentById(R.id.menu_frame);
		}
		navFrag.setOnItemClickListener(this);
	}
	
	private void initBehindSlidingMenu() {
		SlidingMenu sm = getSlidingMenu();
		sm.setFadeEnabled(false);
		sm.setBehindOffset(getResources().getDisplayMetrics().widthPixels / 7 * 2);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

		sm.setBackgroundImage(R.drawable.bg_sidebar);

		sm.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.05);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});
	}
	
	
	@Override
	public void initBase() {
		fragManager = getSupportFragmentManager();
		initBehindSlidingMenu();
	}

	@Override
	public void initUI() {
		titleView = (TextSwitcherTitleView) findViewById(R.id.titleView);
		titleView.setOnClickListener(this);
		titleView.setTitleText(R.string.color_lamp);
		
		MainFragment mainFrag = new MainFragment();
		fragManager.beginTransaction().replace(R.id.content_layout, mainFrag).commit();
	}

	@Override
	public void initData() {
	}

	@Override
	public void initListener() {
	}

	@Override
	public void onItemClick(int position, String title) {
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_btn:
			getSlidingMenu().showMenu(true);
			break;
		case R.id.right_btn:
			//TODO 开启播放界面
			startMusicPlayerActivity();
			break;
		default:
			break;
		}
	}

	@Override
	public void onMainPageChanged(int position, String title) {
		titleView.setTitleText(title);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BluetoothDeviceManagerProxy.getInstance(this).destory();
	}
}
