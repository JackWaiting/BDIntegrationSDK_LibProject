package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.MusicListActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.SearchActivity.OnSearchListener;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.AlbumListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Album;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.AlbumEntity;
import com.chipsguide.app.colorbluetoothlamp.v2.net.HttpCallback;
import com.chipsguide.app.colorbluetoothlamp.v2.net.HttpFactory;
import com.chipsguide.app.colorbluetoothlamp.v2.net.HttpType;
import com.chipsguide.app.colorbluetoothlamp.v2.view.Footer4List;
import com.google.gson.Gson;

public class AlbumListFragment extends BaseFragment implements OnSearchListener{
	public static final String QUERY_TYPE = "query_type";
	public static final String QUERY_TYPE_ALBUM_CODE = "album_code";
	public static final String QUERY_TYPE_ALBUM_NAME = "album_name";
	public static final String EXTRA_DATA = "extra_data";

	private List<Album> albumlist = new ArrayList<Album>();
	
	private Footer4List footView;
	private AlbumListAdapter adapter;
	private ListView albumListLv;
	private boolean loading;
	private int limitNum = 12;

	private String queryType;
	private String extraData;
	private int currentPage = 1;
	private int totalPage = 1;
	private int lastItem;

	@Override
	protected void initBase() {
		adapter = new AlbumListAdapter(getActivity());
		Bundle bundle = getArguments();
		if(bundle != null){
			queryType = bundle.getString(QUERY_TYPE);
			extraData = bundle.getString(EXTRA_DATA);
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.common_listview_layout;
	}

	@Override
	protected void initView() {
		albumListLv = (ListView) findViewById(R.id.listview);
		albumListLv.setOnScrollListener(scrollListener);
		footView = new Footer4List(getActivity());
		albumListLv.addFooterView(footView);
		albumListLv.setAdapter(adapter);
		albumListLv.setOnItemClickListener(itemClickListener);
	}

	@Override
	protected void initData() {
		getAlbumList(currentPage);
	}
	
	private void getAlbumList(int page) {
		if (checkNetwork(true)) {
			if (!loading && !TextUtils.isEmpty(queryType)) {
				loading = true;
				if (QUERY_TYPE_ALBUM_CODE.equals(queryType)) {
					HttpFactory.getSpecialList(getActivity(), callback,
							extraData, page, limitNum);
				} else {
					HttpFactory.searchMusicBySpecial(getActivity(), callback,
							"1", extraData, page, limitNum);
				}
			}else{
				albumListLv.removeFooterView(footView);
			}
		} else {
			footView.hideProgressBar();
			footView.setText(R.string.hint_loading_failed);
		}
	}

	private HttpCallback callback = new HttpCallback() {
		@Override
		public void onStart(String threadName) {
		}

		@Override
		public void onFinish(boolean success, String respond, HttpType type,
				String threadName) {
			loading = false;
			if (success) {
				AlbumEntity albumEntity = parse(respond, AlbumEntity.class);
				if (albumEntity != null
						&& albumEntity.getContent().getList() != null) {
					List<Album> list = albumEntity.getContent().getList();
					totalPage = albumEntity.getContent().getCountPage();
					currentPage = albumEntity.getContent().getPage();
					if (list != null && list.size() > 0) {
						albumlist.addAll(list);
						adapter.setList(albumlist);
						if (currentPage >= totalPage) {
							albumListLv.removeFooterView(footView);
						}
					}else if(currentPage == 1){
						footView.hideProgressBar();
						footView.setText(R.string.search_no_result);
					}
				}
			}
		}

		@Override
		public void onCancel(String threadName) {
			loading = false;
		}
	};

	private OnScrollListener scrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView arg0, int scrollState) {
			if (OnScrollListener.SCROLL_STATE_IDLE == scrollState
					&& lastItem == adapter.getCount() + 1
					&& (currentPage < totalPage)) {
				getAlbumList(currentPage + 1);
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
			if(!checkNetwork(true)){
				return;
			}
			//点击footer
			if(position > albumlist.size() - 1){
				return;
			}
			Album album = albumlist.get(position);
			Intent intent = new Intent(getActivity(), MusicListActivity.class);
			intent.putExtra(MusicListActivity.EXTRA_ALBUM, album);
			startActivity(intent);
		}
	};

	
	protected <T> T parse(String json, Type cls) {
		try {
			Gson gson = new Gson();
			return gson.fromJson(json, cls);
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void onSearchTextChanged(String keyWords) {
	}

	@Override
	public void onStartSearch(String keyWords) {
		if(albumlist != null){
			albumlist.clear();
		}
		//如果albumListLv为null，是否需要更新
		if(albumListLv != null)
		{
			albumListLv.removeFooterView(footView);
			albumListLv.addFooterView(footView);
			footView.setText(R.string.text_loading);
			footView.showProgressBar();
			queryType = QUERY_TYPE_ALBUM_NAME;
			extraData = keyWords;
			getAlbumList(currentPage);
		}
	}
}
