package com.faota.sdk.client.connect.tcp;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *TCP客户端 (接收文件)
 *连接成功后接收文件
 *接收完毕 关闭连接
 */
public class TCPClient {

	/**
	 * 
	 * @param ip ip地址
	 * @param port 端口号
	 * @param path 文件保存路径
	 * @param name 文件名
	 * @param listener 进度回调
	 */
	public TCPClient(final String ip,final int port,final String path,final String name,final ProgressListener listener) {
		new Thread(new Runnable() {
			
			public void run() {
				
				try {
				Socket socket=new Socket(ip, port);
				File dir = new File(path);
		            if(!dir.exists()&&!dir.isDirectory()){//判断文件目录是否存在  
		                dir.mkdirs();  
		            }
		         File file=new File(path+"\\"+name+".tmp");
		         DataInputStream dis=new DataInputStream(socket.getInputStream());
		         FileOutputStream fos=new FileOutputStream(file);
		         int n,current=0;
		         byte[] data=new byte[1024];
		         int length=dis.readInt();
		         while ((n=dis.read(data))!=-1) {
					fos.write(data, 0, n);
					fos.flush();
					current+=n;
					if (listener!=null) {
						listener.OnProgress(current,length);
					}
					if (current==length) {
						fos.close();
						if (!file.renameTo(new File(path+"\\"+name))) {
							for (int i = 1; i <100; i++) {
								if (file.renameTo(new File(path+"\\"+"["+i+"]"+name))) {
									break;
								}
							}
						}
						
					}
				}
		        dis.close();
		        fos.close();
		        socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
			}
		}).start();
	}
}
