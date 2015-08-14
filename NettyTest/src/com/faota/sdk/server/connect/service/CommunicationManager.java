package com.faota.sdk.server.connect.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.ConcurrentSet;
import java.util.Iterator;

import com.faota.sdk.server.connect.Decoder;
import com.faota.sdk.server.connect.Encoder;

/**
 * 服务端通讯器
 */
public class CommunicationManager{
	
	private ConcurrentSet<Client> list=new ConcurrentSet<Client>();//在线连接列表
	
	/**
	 * 同一ip地址 最大连接数 超过拒绝连接 (默认10)
	 */
	public static int MAX_SAME_IP=10;
	
	/**
	 * 调试 (默认关闭)
	 */
	public static boolean DEBUG=false;
	
/**
 * 服务端通讯器
 * @param service 端口号
 * @param listener 消息回调
 */
	public CommunicationManager(final Service service,final ResponseListener listener) {
		
		new Thread(new Runnable() {
			
			public void run() {
				EventLoopGroup bossGroup=new NioEventLoopGroup();
				EventLoopGroup workerGroup=new NioEventLoopGroup();
				
				final Repeater r=new Repeater(list);
				final Client c=new Client(list);
				
				try {
					
					ServerBootstrap b=new ServerBootstrap();
					b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel sc) throws Exception {
							
							sc.pipeline().addLast(new Encoder());//编码器
							sc.pipeline().addLast(new Decoder());//解码器
							
							sc.pipeline().addLast(new IdleStateHandler(10,5,0));//空闲检测
							
							sc.pipeline().addLast(new HeartbeatHandler());//心跳
							
							
							
							sc.pipeline().addLast(new ChannelInboundHandlerAdapter(){
								
								
								
								@Override
								public void channelActive(ChannelHandlerContext ctx) 
										throws Exception {
									
									/**
									 * 连接规则
									 * 
									 * 同一个ip地址
									 * 不允许超过20条连接
									 * 否则 拒绝连接
									 * 
									 * 防御高并发连接攻击
									 */
									
									int size=0;
									
									String[] temp=String.valueOf(ctx.channel().remoteAddress()).split(":");
									
									Iterator<Client> it=list.iterator();
									
									while (it.hasNext()) {
										Client c=it.next();
										if (temp[0].equals(c.getIp())) {
											size++;
										}
									}
									
									if (size<=MAX_SAME_IP) {
										c.setContext(ctx);
										list.add(c);
									}else {
										println("已断开恶意连接:"+ctx.channel().remoteAddress());
										ctx.disconnect();	
									}
								};
								
								
								
								@Override
								public void channelRead(ChannelHandlerContext ctx,
										Object msg) throws Exception {
									
										if (listener!=null) {
											c.setMessage(msg);
											c.setContext(ctx);
											listener.OnMessage(r, c);
										}
									}
								
								public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
										throws Exception {
										
									
								};
								
								@Override
								public void handlerRemoved(
										ChannelHandlerContext ctx)
										throws Exception {
										
										Iterator<Client> it=list.iterator();
										
										while (it.hasNext()) {
											Client c=it.next();
											if (c.getContext()==ctx) {
												if (listener!=null&&c.getKey().length()>0) {
													listener.OnBreak(c.getKey());
												}
												it.remove();
											}
											
										}
										ctx.close();
									}
								});
							}
						});
					
					try {
						
						b.bind(service.getPort()).channel().closeFuture().sync();;
						
					} catch (InterruptedException e) {
						
					}
					
				} finally {
					//退出 释放线程池资源
					bossGroup.shutdownGracefully();
					workerGroup.shutdownGracefully();
					
				}
				
			}
		}).start();
				
				
				
		
		
	}
	
	class HeartbeatHandler extends ChannelDuplexHandler {
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			
			if (msg instanceof String) {
				ctx.writeAndFlush(new String("pong"));
			}else {
				ctx.fireChannelRead(msg);
			}
		}
		
		@Override  
	    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)  
	            throws Exception {  
	        if (evt instanceof IdleStateEvent) {  
	            IdleStateEvent e = (IdleStateEvent) evt;  
	            if (e.state() == IdleState.ALL_IDLE) {
	            	println("此连接总超时关闭:"+ctx.channel());
	                ctx.close();
	            } 
	        }  
	    }  
	 }
	
	/**
	 * 打印消息到控制台
	 * @param msg
	 */
	public void println(final Object msg) {
		if (DEBUG) {
			System.out.println(msg);
		}
	}
}
