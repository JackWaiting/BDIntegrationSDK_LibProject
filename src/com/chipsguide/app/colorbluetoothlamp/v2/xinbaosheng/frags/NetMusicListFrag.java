package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.frags;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.activity.SearchActivity.OnSearchListener;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.adapter.NetMusicListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.bean.Album;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.bean.MusicBoby;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.bean.MusicEntity;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.bean.SearchEntity;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.listener.SimpleMusicPlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.media.PlayerManager.PlayType;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.net.HttpCallback;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.net.HttpFactory;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.net.HttpType;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view.Footer4List;
import com.google.gson.Gson;

public class NetMusicListFrag extends BaseFragment implements OnSearchListener, SimpleMusicPlayListener{
	public static final String EXTRA_QUERY_TYPE = "query_type";
	public static final String QUERY_TYPE_BY_NAME = "query_by_name";
	public static final String QUERY_TYPE_BY_ALBUM = "query_by_album";
	public static final String EXTRA_DATA = "extra_data";
	
	private static final int LIMITED_NUM = 12;
	private NetMusicListAdapter adapter;
	private int currentPage = 1;
	private int lastItem;
	private Footer4List footer;
	private ListView musicListLv;
	private List<Music> musiclist;
	private boolean loading;
	
	private String queryType;
	private String searchName;
	private Album mAlbum;
	private int currentPosition;
	private PlayerManager playerManager;
	private Music currentMusic;
	
	public List<Music> getMusiclist() {
		return musiclist;
	}
	
	public int getCurrentPosition(){
		return currentPosition;
	}
	
	
	@Override
	protected void initBase() {
		playerManager = PlayerManager.getInstance(getActivity().getApplicationContext());
		Bundle bundle = getArguments();
		if(bundle != null){
			queryType = bundle.getString(EXTRA_QUERY_TYPE);
			if(QUERY_TYPE_BY_ALBUM.equals(queryType)){
				mAlbum = (Album) bundle.getSerializable(EXTRA_DATA);
			}else{
				searchName = bundle.getString(EXTRA_DATA);
			}
		}
		musiclist = new ArrayList<Music>();
		adapter = new NetMusicListAdapter(getActivity());
	}

	@Override
	protected int getLayoutId() {
		return R.layout.common_listview_layout;
	}

	@Override
	protected void initView() {
		footer = new Footer4List(getActivity());
		
		musicListLv = (ListView) findViewById(R.id.listview);
		musicListLv.setOnScrollListener(scrollListener);
		musicListLv.setOnItemClickListener(itemClickListener);
		musicListLv.addFooterView(footer);
		musicListLv.setAdapter(adapter);
	}

	private void updateUI() {
		if(adapter == null){
			return;
		}
		if(PlayType.Net == PlayerManager.getPlayType()){
			currentMusic = playerManager.getCurrentMusic();
			adapter.setSelected(currentMusic.getPath());
			if(!userClick && musicListLv != null){
				//musicListLv.setSelection(adapter.getSelected());
			}
			userClick = false;
		}
	}
	
	@Override
	protected void initData() {
		getMusicList(currentPage);
	}
	
	private void getMusicList(int page) {
		if (checkNetwork(true)) {
			if(!loading && !TextUtils.isEmpty(queryType)){
				loading = true;
				if(QUERY_TYPE_BY_ALBUM.equals(queryType) && mAlbum != null){
					HttpFactory.getMusicBySpecial(getActivity(), httpCallback, mAlbum.getType(), mAlbum.getId(), page, LIMITED_NUM);
				}else{
					HttpFactory.searchMusicByname(getActivity(), httpCallback, "1", searchName, page, LIMITED_NUM);
				}
			}else{
				musicListLv.removeFooterView(footer);
			}
		}else{
			footer.hideProgressBar();
			footer.setText(R.string.hint_loading_failed);
		}
	}
	
	/**
	 * 网络请求回调
	 */
	private HttpCallback httpCallback = new HttpCallback() {
		@Override
		public void onStart(String threadName) {
		}
		@Override
		public void onCancel(String threadName) {
			loading = false;
		}
		
		@Override
		public void onFinish(boolean success, String response, HttpType type,
				String threadName) {
			loading = false;
			if(success){
				if(QUERY_TYPE_BY_ALBUM.equals(queryType)){
					forMusicEntity(response);
				}else{
					forSearchEntity(response);
				}
			}else{
			}
		}
	};
	
	private void forMusicEntity(String response) {
		MusicEntity music2Entity = parse(response, MusicEntity.class);
		if (music2Entity != null) {
			MusicBoby music2boby = music2Entity.getContent();
			if(music2boby != null && music2boby.getLists() != null){
				List<Music> list = music2boby.getLists().getList();
				int totalPage = music2boby.getCountPage();
				currentPage = music2boby.getPage();
				if(list != null && list.size() > 0){
					for (int i = 0; i < list.size(); i++) {
						list.get(i).setAlbumCoverpath(mAlbum.getCoverpath_l());;
					}
					musiclist.addAll(list);
					adapter.setMusicList(musiclist);
					updateUI();
					if (currentPage >= totalPage) {
						musicListLv.removeFooterView(footer);
					}
				}
			}
		}
	}
	
	private void forSearchEntity(String response) {
		SearchEntity searchEntity = parse(response, SearchEntity.class);
		if (searchEntity != null && searchEntity.getContent() != null
				&& searchEntity.getContent().getList() != null) {
			List<Music> list = searchEntity.getContent().getList();
			int totalPage = searchEntity.getContent().getCountPage();
			currentPage = searchEntity.getContent().getPage();
			if (list != null && list.size() > 0) {
				musiclist.addAll(list);
				adapter.setMusicList(musiclist);
				if (currentPage >= totalPage) {
					musicListLv.removeFooterView(footer);
				}
				updateUI();
			}else if(currentPage == 1){
				showToast(R.string.search_no_result);
				footer.hideProgressBar();
				footer.setText(R.string.search_no_result);
			}
		}
	}
	
	/**
	 * ListView滚动监听，实现加载更多
	 */
	private OnScrollListener scrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				if (OnScrollListener.SCROLL_STATE_IDLE == scrollState
						&& adapter != null && lastItem == adapter.getCount() + 1) {
					getMusicList(currentPage + 1);
				}
			}
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			lastItem = firstVisibleItem + visibleItemCount;
		}
	};

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			//点击footer
			if(position > musiclist.size() - 1){
				return;
			}
			userClick = true;
			adapter.setSelected(position);
			currentPosition = position;
			startMusicPlayerActivity(musiclist, currentPosition, PlayType.Net);
		}
	};
	
	protected <T> T parse(String json, Type cls) {
		T t = null;
		try {
			Gson gson = new Gson();
			t = gson.fromJson(json, cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public void onSearchTextChanged(String keyWords) {
	}

	@Override
	public void onStartSearch(String keyWords) {
		if(musiclist != null){
			musiclist.clear();
		}
		musicListLv.removeFooterView(footer);
		musicListLv.addFooterView(footer);
		footer.showProgressBar();
		footer.setText(R.string.text_loading);
		queryType = QUERY_TYPE_BY_NAME;
		searchName = keyWords;
		getMusicList(currentPage);
	}

	@Override
	public void onMusicProgress(long duration, long currentDuration, int percent) {
	}

	@Override
	public void onMusicPlayStateChange(boolean playing) {
		
	}

	private boolean userClick;
	@Override
	public void onMusicChange() {
		if(PlayType.Net == PlayerManager.getPlayType()){
			updateUI();
		}
	}

}
