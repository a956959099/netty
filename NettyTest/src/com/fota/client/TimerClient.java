package com.fota.client;


import com.fota.client.handler.TimerClinetHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimerClient {

	public void connect(int port,String host){
		//配置NIO线程组
		EventLoopGroup group =new NioEventLoopGroup();
		try {
			Bootstrap b=new Bootstrap();
			b.group(group);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.TCP_NODELAY, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new TimerClinetHandler());
				}
			});
			//发起异步连接操作
			ChannelFuture f = b.connect(host, port).sync();
			//等待客户端链路关闭
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			System.out.println(e.toString());
		}finally{
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		new TimerClient().connect(1346, "127.0.0.1");
	}
}
