package com.faota.sdk.server.connect.service;

/**
 *Service对象
 */
public class Service {
	
	private int port;//监听的端口号
	
	/**
	 * @param port 端口
	 */
	public Service(final int port) {
		this.port=port;
	}
	
	/**
	 * @return 端口号
	 */
	public int getPort() {
		return port;
	}
	
}
