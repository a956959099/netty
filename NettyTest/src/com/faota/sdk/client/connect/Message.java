package com.faota.sdk.client.connect;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 协议对象
 * 
 * 协议对象必须实现 Serializable
 * 
 *
 */

public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int command;//命令
	
	private Map<String, String> values=new HashMap<String, String>();

	public int getCommand() {
		return command;
	}

	public Message setCommand(final int command) {
		this.command = command;
		return this;
	}

	public Message put(final String key,final String value) {
		values.put(key, value);
		return this;
	}

	public String getValue(final String key) {
		return values.get(key);
	}
	
	@Override
	public String toString() {
		return "<"+command+">"+values;
	}
	

}
