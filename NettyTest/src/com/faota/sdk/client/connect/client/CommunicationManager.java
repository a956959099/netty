package com.faota.sdk.client.connect.client;

import com.faota.sdk.client.connect.Decoder;
import com.faota.sdk.client.connect.Encoder;
import com.faota.sdk.client.connect.Message;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 客户端通讯器
 */
public class CommunicationManager {
	
	/**
	 * 调试 (默认关闭)
	 */
	public static boolean DEBUG=false;
	
	private Client client;//ip,端口
	private ResponseListener listener;//消息回调监听
	private ChannelHandlerContext context;//消息发送器
	private EventLoopGroup group;
	private boolean isOpen;//是否连接上

	/**
	 * @param client  客户端通讯器配置对象
	 * @param listener 消息回调
	 */
	public CommunicationManager(final Client client,final ResponseListener listener) {
		this.client=client;
		this.listener=listener;
		reConnect(null);
	}
	
	/**
	 * 重连
	 * @param msg 消息
	 */
	public void reConnect(final Object msg) {
		
		new Thread(new Runnable() {
			
			public void run() {
				 group=new NioEventLoopGroup();
					
					try {
						Bootstrap b=new Bootstrap();
						 b.group(group)
						.channel(NioSocketChannel.class)
						.option(ChannelOption.TCP_NODELAY, true)
						.handler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel arg0) throws Exception {
								
								arg0.pipeline().addLast(new Encoder());//编码器
								arg0.pipeline().addLast(new Decoder());//解码器
								
								arg0.pipeline().addLast(new IdleStateHandler(10,5,0));//心跳
								
								arg0.pipeline().addLast(new HeartbeatHandler());
								
								arg0.pipeline().addLast(new ChannelInboundHandlerAdapter(){
									
									//连接通道准备就绪
									@Override
									public void channelActive(ChannelHandlerContext ctx)
											throws Exception {
											context=ctx;
											isOpen=true;
											if (msg!=null) {ctx.writeAndFlush(msg);}	
									}
									
									//有消息
									@Override
									public void channelRead(ChannelHandlerContext ctx,
											Object msg) throws Exception {
											if (listener!=null) {
												if (msg instanceof Message) {
													
													Message message=(Message) msg;
													if (message.getCommand()==-1) {//文件
														//解析发送端ip地址
														String ip=(String) message.getValue("ip");
														//解析发送端端口
														int port=Integer.valueOf((String) message.getValue("port"));
														//解析文件名
														String fileName=(String) message.getValue("fileName");
														//解析文件关于
														String info=(String) message.getValue("info");
														//解析文件长度
														int length=Integer.valueOf((String) message.getValue("length"));
														//消息回调
														listener.OnReceive(ip, port, info,fileName, length);
													}else {
														listener.OnMessage(msg);
													}
													return;
												}
												listener.OnMessage(msg);
											}	
										}
									
									//异常
									@Override
									public void exceptionCaught(
											ChannelHandlerContext ctx, Throwable cause)
											throws Exception {
											println("异常:"+cause.getMessage());
									}
									
									//断开连接
									@Override
									public void handlerRemoved(
											ChannelHandlerContext ctx)
											throws Exception {
										isOpen=false;
									}
									
								});
							}
						});
						
						try {
						b.connect(client.getIp(), client.getPort()).channel().closeFuture().sync();
						} catch (InterruptedException e) {
					}
				} finally {
					isOpen=false;
					group.shutdownGracefully();
					if (listener!=null) {listener.OnBreak();}
				}	
			}
		}).start();
		//连接未就绪一直阻塞
		while (context==null) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 发送消息
	 * @param msg 协议对象
	 */
	public void sendContent(final Object msg) { 
		if (!isOpen) {
			if (listener!=null) {listener.OnBreak();}
		}else {
			context.writeAndFlush(msg);
		}
	}
	
	
	/**
	 * 断开连接
	 */
	public void disConnect() {
		group.shutdownGracefully();
	}
	
	
class HeartbeatHandler extends ChannelDuplexHandler {
	
	private int size=0;
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			
			if (msg instanceof String) {
				size=0;
			}else {
				ctx.fireChannelRead(msg);
			}
		}
		
		@Override  
	    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)  
	            throws Exception {  
	        if (evt instanceof IdleStateEvent) {  
	            IdleStateEvent e = (IdleStateEvent) evt;  
	            if (e.state() == IdleState.READER_IDLE) {
	            	
	            	size++;
	            	
	            	if (size>2) {
						ctx.close();
						isOpen=false;
						size=0;
					}
	            	println("读超时");
	            }else if (e.state()==IdleState.WRITER_IDLE) {
	            	ctx.writeAndFlush(new String("ping"));
					println("发送心跳包");
				}else if (e.state()==IdleState.ALL_IDLE) {
					ctx.close();
					println("总超时");
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

