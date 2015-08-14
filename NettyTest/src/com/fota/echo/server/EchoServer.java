package com.fota.echo.server;

import com.fota.Constant;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {

	public void bind(int port) {

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGrop = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGrop);
			b.channel(NioServerSocketChannel.class);
			b.option(ChannelOption.SO_BACKLOG, 100);
			b.handler(new LoggingHandler(LogLevel.INFO));
			b.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
					ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new EchoServerHandler());
				}
			});
			ChannelFuture f = b.bind(port).sync().channel().closeFuture().sync();
		} catch (Exception e) {

		} finally {
			bossGroup.shutdownGracefully();
			workerGrop.shutdownGracefully();
		}
	}
	public static void main(String[] args) {
		new EchoServer().bind(Constant.port);
	}
}
