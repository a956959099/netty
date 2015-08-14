package com.fota.echo.client;

import com.fota.Constant;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class EchoClient {
	public void connect(String host, int port) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group);
			b.option(ChannelOption.TCP_NODELAY, true);
			b.channel(NioSocketChannel.class);
			b.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
					ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new EchoClientHandler());
				}
			});
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			group.shutdownGracefully();
		}

	}

	public static void main(String[] args) {
		new EchoClient().connect(Constant.host, Constant.port);
	}
}
