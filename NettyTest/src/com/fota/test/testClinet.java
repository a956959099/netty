package com.fota.test;

import com.faota.sdk.client.connect.Message;
import com.faota.sdk.client.connect.client.Client;
import com.faota.sdk.client.connect.client.CommunicationManager;
import com.faota.sdk.client.connect.client.ResponseListener;

public class testClinet implements ResponseListener {
	
	public testClinet() {
		Client c=new Client("192.168.18.3", 2324);
		CommunicationManager comm=new CommunicationManager(c, this);
		comm.DEBUG=true;
		comm.sendContent(new Message().put("tets", "etet"));
	}
	@Override
	public void OnMessage(Object msg) {
			System.out.println(msg.toString());
	}
	@Override
	public void OnReceive(String host, int port, String msg, String fileName, int fileLength) {
		System.err.println();
	}
	@Override
	public void OnBreak() {
		System.out.println();
	}
	public static void main(String[] args) {
		new testClinet();
	}
}
