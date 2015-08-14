package com.faota.sdk.client.connect.client;

/**
 * 客户端通讯器配置对象
 */
public class Client {

	private String ip;
	private int port;
	
	public Client(final String ip,final int port) {
		this.ip=ip;
		this.port=port;
	}
	
	public String getIp() {
		return ip;
	}
	
	public int  getPort() {
		return port;
	}
}
