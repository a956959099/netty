package com.faota.sdk.server.connect.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ConcurrentSet;

/**
 * 此对象为消息中转器
 */
public class Repeater {

	private ConcurrentSet<Client> list;//在线连接列表
	
	/**
	 * 构造
	 * @param list
	 */
	public Repeater(final ConcurrentSet<Client> list) {
		this.list=list;
	}
	
	/**
	 *公屏聊天
	 * 此方法向所有在线连接发送消息
	 * @param msg
	 */
	public void sendContent(final Object msg) {
		for (Client c : list) {
			c.getContext().writeAndFlush(msg);
		}
	}
	
	/**
	 * 此方法向指定连接发送消息
	 * @param key
	 * @param msg
	 */
	public void sendContent(final String key,final Object msg) {
		for (Client c : list) {
			if (c.getKey().equals(key)) {
				c.getContext().writeAndFlush(msg);
			}
		}
	}
	
	/**
	 * 判断此连接是否在线
	 * @param key
	 * @return
	 */
	public boolean isOnline(final String key) {
		for (Client c : list) {
			if (c.getKey().equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 用于遍历连接属性
	 * @param ctx
	 * @return
	 */
	public Client getProperty(final ChannelHandlerContext ctx) {
		for(Client c:list){
			if (c.getContext()==ctx) {
				return c;
			}
		}
		return null;
	}
	
	public ConcurrentSet<Client> getList() {
		return list;
	}
}
