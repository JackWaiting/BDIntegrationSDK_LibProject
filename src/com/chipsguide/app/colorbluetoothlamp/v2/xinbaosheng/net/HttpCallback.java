package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.net;public interface HttpCallback {		/**	 * 开始请求	 * 	 * @param threadName	 */	void onStart(String threadName);		/**	 * 网络请求回调方法	 * 	 * @param success	 *            请求成功返回true，否则返回false	 * @param respond	 *            请求成功返回结果，否则返回错误信息	 * @param threadName 任务名称	 */	void onFinish(boolean success, String respond,HttpType type, String threadName);		/**	 * 取消请求	 * 	 * @param threadName	 */	void onCancel(String threadName);}