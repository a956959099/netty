package com.fota.server;

import com.fota.server.handler.ChildChannelHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {

	public void bind(int port) {
		// 配置服务端的NIO
		EventLoopGroup bossGrop = new NioEventLoopGroup();
		EventLoopGroup workGrop = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGrop, workGrop);
			b.channel(NioServerSocketChannel.class);
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.childHandler(new ChildChannelHandler());
			//绑定端口，同步等待成功
			ChannelFuture channelFuture = b.bind(port).sync();
			//等待服务器端 监听端口关闭
			channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			//释放线程池资源
			bossGrop.shutdownGracefully();
			workGrop.shutdownGracefully();
		}

	}
	public static void main(String[] args) {
		new TimeServer().bind(1346);
	}
}
