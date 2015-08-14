package com.faota.sdk.client.connect.client;

public interface ResponseListener {
	/**
	 * 消息回调
	 * @param msg 反序列化后的协议对象
	 */
	void OnMessage(final Object msg);
	
	/**
	 * 文件接收回调 发送端会启动socket服务(默认20秒超时) 文件接收方法: new TCPClient(填写相应参数);
	 * @param host ip地址
	 * @param port 端口
	 * @param msg 文件简介
	 * @param fileName 文件名
	 * @param fileLength 文件大小(字节)
	 */
	void OnReceive(final String host,final int port,final String msg,final String fileName,final int fileLength);
	
	/**
	 * 连接断开回调
	 */
	void OnBreak();
	
}
