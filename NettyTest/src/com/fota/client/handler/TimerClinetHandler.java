package com.fota.client.handler;



import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimerClinetHandler extends ChannelHandlerAdapter {

	private ByteBuf firstMessage;
	private int counter;
	private byte [] req;
	public TimerClinetHandler() {
	 req = ("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		ByteBuf messae=null;
		for(int i =0; i<100;i++){
			messae=Unpooled.buffer(req.length);
			messae.writeBytes(req);
			ctx.writeAndFlush(messae);
		}
		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		ByteBuf buf = (ByteBuf) msg;
//		byte[] req = new byte[buf.readableBytes()];
//		buf.readBytes(req);
		String body = (String) msg;
		System.out.println("Now is：" + body+";the counter is: "+ ++counter);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		System.out.println(cause.getMessage());
	}
}
