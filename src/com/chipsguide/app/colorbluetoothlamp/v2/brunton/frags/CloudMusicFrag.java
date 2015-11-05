package com.chipsguide.app.colorbluetoothlamp.v2.brunton.frags;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.activity.AlbumListActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.activity.SearchActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.adapter.AlbumGridViewAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.been.Column;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.view.GridHeaderView;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.widget.HeaderGridView;

public class CloudMusicFrag extends BaseFragment implements OnItemClickListener, OnClickListener{
	private HeaderGridView gridView;
	private AlbumGridViewAdapter adapter;
	private List<Column> albumList;
	private String[] ximalaya_names;
	private String[] ximalaya_codes;
	private GridHeaderView headerView;
	
	@Override
	protected void initBase() {
		adapter = new AlbumGridViewAdapter(getActivity());
		ximalaya_names = getResources().getStringArray(R.array.ximalaya_name);
		ximalaya_codes = getResources().getStringArray(R.array.ximalaya_code);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_cloud_music;
	}

	@Override
	protected void initView() {
		headerView = new GridHeaderView(getActivity());
		headerView.setId(headerView.hashCode());
		headerView.setOnClickListener(this);
		gridView = (HeaderGridView) findViewById(R.id.headerGridView);
		gridView.setOnItemClickListener(this);
		gridView.setNumColumns(3);
		gridView.addHeaderView(headerView);
		gridView.setAdapter(adapter);
	}

	@Override
	protected void initData() {
		loadLocal();
	}
	
	private void loadLocal() {
		albumList = new ArrayList<Column>();
		for(int i = 0 ; i < ximalaya_codes.length ; i++){
			Column column = new Column();
			column.setId(ximalaya_codes[i]);
			column.setName(ximalaya_names[i]);
			column.setCode(ximalaya_codes[i]);
			albumList.add(column);
		}
		adapter.setList(albumList);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(!checkNetwork(true)){
			return;
		}
		Intent intent = new Intent();
		intent.putExtra(AlbumListActivity.EXTRA_ALBUM_NAME, ximalaya_names[position]);
		intent.putExtra(AlbumListActivity.EXTRA_ALBUM_CODE, ximalaya_codes[position]);
		intent.setClass(getActivity(), AlbumListActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		int id = headerView.getId();
		if(v.getId() == id){
			startActivity(SearchActivity.class);
		}
	}
	
}
