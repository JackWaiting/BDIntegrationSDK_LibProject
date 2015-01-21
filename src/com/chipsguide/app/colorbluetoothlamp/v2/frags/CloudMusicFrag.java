package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.AlbumListActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.AlbumGridViewAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Column;
import com.chipsguide.app.colorbluetoothlamp.v2.view.GridHeaderView;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.HeaderGridView;

public class CloudMusicFrag extends BaseFragment implements OnItemClickListener{
	private HeaderGridView gridView;
	private AlbumGridViewAdapter adapter;
	private List<Column> albumList;
	private String[] ximalaya_names;
	private String[] ximalaya_codes;
	
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
		GridHeaderView headerView = new GridHeaderView(getActivity());
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
	
}
