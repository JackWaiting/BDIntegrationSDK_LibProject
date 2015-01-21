package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.util.ArrayList;
import java.util.List;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.AlbumGridViewAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Column;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.HeaderGridView;

public class CloudMusicFrag extends BaseFragment {
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
		gridView = (HeaderGridView) findViewById(R.id.headerGridView);
		gridView.setNumColumns(3);
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
	
}
