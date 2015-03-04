package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.net;import java.lang.reflect.Field;import android.util.Log;/** *  *  */public final class HttpConfig {	/** 接口测试地址 */	 protected static final String HOST = "http://121.199.167.212";	 protected static final String PORT = ":80";	 protected static final String INC = "/music/interface.action?method=";	protected static final String GET_TRANSCCEIVER = "getTranscceiver";//获取电台列表	protected static final String GET_COLUMLIST = "categories";//获取分类列表	protected static final String GET_SPECIALLIST = "categoriesList";//获取专辑列表	protected static final String GET_MUSICONE = "getMusicOne";//获取列表	protected static final String GET_MUSICBYNAME = "getMusicByname";//获取列表	protected static final String GET_TYPESPECIAL = "getTypeSpecial";//获取列表	protected static final String GET_MUSICBYSPECIAL = "categoriesDetails";//获得专辑下面歌曲列表		protected static final String GET_SPECIALBYNAME = "getSpecialByname";//获取列表	protected static final String REGISTER = "register";// 注册	protected static final String LOGIN = "login";// 登录	public static final String GET_VERSION = "getVersion";	public static final String SEARCH_MUSICBYSPECIAL = "searchMusicBySpecial";//搜索专辑	public static final String SEARCH_MUSICBYNAME="searchMusicByname";		private static final String TAG = null;	HttpConfig() {	}	/**	 * 获取对应的URL地址	 * 	 * @param type	 *            HttpType	 * @return String	 * 	 * @see HttpType	 */	public static String url(HttpType type) {		String value = "";		try {			Log.i(TAG, "type:" + type);			Field f = HttpConfig.class.getDeclaredField(type.name());			value = f.get(type).toString();		} catch (Exception e) {			Log.e(TAG, e.getMessage());		}		return HOST + PORT + INC + value;	}	}