package com.faota.sdk.server.connect.service;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.faota.sdk.server.connect.Message;
import com.faota.sdk.server.connect.tcp.ProgressListener;
import com.faota.sdk.server.connect.tcp.TCPService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ConcurrentSet;

/**
 * 连接包装
 */
public class Client {
	
	private String ip;
	private String key="";
	private Object msg;//来自客户端的消息
	private ChannelHandlerContext context;//发送器
	private ConcurrentSet<Client> list;//在线连接列表
	
	/**
	 * 构造
	 * @param msg
	 * @param context
	 * @param list
	 */
	public Client(final ConcurrentSet<Client> list) {
		this.list=list;
	}
	
	/**
	 * 返回消息
	 * @return
	 */
	public Object getMessage() {
		return msg;
	}
	
	/**
	 * 发送消息
	 * @param msg
	 */
	public void sendContent(final Object msg) {
		context.writeAndFlush(msg);
	}
	
	/**
	 * 向客户端发送文件
	 * @param path 文件路径
	 * @param msg 文件介绍
	 * @param listener 进度回调
	 */
	public void sendContent(final String path,final String info,final ProgressListener listener) {
		int port=TCPService.getIdelPort();
		new TCPService(port,path, listener);
		int fileLength=0;//文件长度
		try {
			FileInputStream in=new FileInputStream(new File(path));
			fileLength=in.available();
			in.close();
		} catch (Exception e) {
		}
		String[] temp=path.split("\\\\");
		String name=temp[temp.length-1];
		
		try {
			Message msg=new Message();
			msg.setCommand(-1);
			msg.put("ip", InetAddress.getLocalHost().getHostAddress());
			msg.put("port", String.valueOf(port));
			msg.put("fileName", name);
			msg.put("info", info);
			msg.put("length", String.valueOf(fileLength));
			sendContent(msg);
		} catch (UnknownHostException e) {
		}
	}
	
	/**
	 * 设置标记
	 * 此标记用于达到点对点,点对多点
	 * 简单点说:像游戏 公聊与私聊
	 * @param key
	 */
	public void setKey(final String key) {
		for (Client c : list) {
			if (c.getContext()==context) {
				c.setKey(key);
			}
		}
	}
	
	public String getKey() {
		return key;
	}
	
	protected void setMessage(final Object msg) {
		this.msg=msg;
	}
	
	protected void setContext(final ChannelHandlerContext context) {
		this.context=context;
		String[] temp=context.channel().remoteAddress().toString().split(":");
		ip=temp[0];
	}
	
	public ChannelHandlerContext getContext() {
		return context;
	}

	public String getIp() {
		return ip;
	}
}
