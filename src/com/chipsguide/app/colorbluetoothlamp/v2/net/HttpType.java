package com.chipsguide.app.colorbluetoothlamp.v2.net;

public enum HttpType {

	GET_TRANSCCEIVER,//获取电台列表
	//GET_CLASSAGE,//获取发现列表
	GET_TYPEBYMUSIC,//
	GET_MUSICONE,//获取电台频道歌
	GET_MUSICBYNAME,//搜索歌曲。
	//GET_TYPESPECIAL,//专辑
	GET_MUSICBYSPECIAL,//专辑下面的歌曲
	GET_SPECIALBYNAME,//搜索专辑
	GET_VERSION,
	REGISTER, // 注册
	LOGIN, // 登录
	
	GET_COLUMLIST,//获取分类列表
	GET_SPECIALLIST,//获取专辑列表
	
	//搜索专辑
	SEARCH_MUSICBYSPECIAL,
	SEARCH_MUSICBYNAME
}
