package com.faota.sdk.server.connect.service;

/**
 *消息回调接口
 */
public interface ResponseListener {
	
	/**
	 * 有来自客户端消息的时候触发
	 * @param r : 消息中转器  
	 * @param c : 连接包装对象
	 */
	void OnMessage(final Repeater r,final Client c);
	
	/**
	 * 断开连接时触发
	 * 前提 断开的连接必须标记有key才会触发
	 * @param key
	 */
	void OnBreak(final String key);
	
}
