package com.faota.sdk.client.connect.tcp;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * TCP服务端 (发送文件)
 * 
 *打开端口监听 等着发送文件
 *当有TCP客户端接入发送文件,
 *发送完毕关闭监听
 */
public class TCPService {
	
	//监听超时时间 (20秒)
	public static int OUTTIME=20000;
	
	public TCPService(final int port,final String path,final ProgressListener listener) {
		new Thread(new Runnable() {
			public void run() {
				try {
					final ServerSocket ss=new ServerSocket(port);
					
					final Timer timer=new Timer();
					//超时关闭任务 目的清除文件端口占用
					timer.schedule(new TimerTask() {
						int time=0;
						@Override
						public void run() {
							time+=1000;
							if (time>=OUTTIME) {
								try {
									ss.close();
									timer.cancel();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}, 0,1000);
					
					Socket socket=ss.accept();
					timer.cancel();
					//有连接接入 发送文件
					DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
					FileInputStream fis=new FileInputStream(new File(path));
					int all=fis.available();
					dos.writeInt(all);//先发送4字节文件长度
					int length,current = 0;
					byte[] data=new byte[1024];
					while ((length=fis.read(data))!=-1) {
						dos.write(data, 0, length);
						dos.flush();
						current+=length;
							if (listener!=null) {
								listener.OnProgress(current,all);
							}
							}
							dos.close();
							fis.close();
							socket.close();
							ss.close();
					} catch (Exception e){
						
				}	
			}
		}).start();
	}
	
/**
 * 返回空闲端口
 * @return
 */
	public static int getIdelPort() {
		int min=2000;
		int max=65535;
		
		for (int i = min; i < max; i++) {
			try {
				Socket socket=new Socket("127.0.0.1", i);
				socket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				return i;
			}
			
		}
		return 0;
	}
}
