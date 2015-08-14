package com.faota.sdk.server.connect;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 * 
 * 编码方式:类型(1byte),长度(4byte)
 * 
 * 类型: 0 心跳 , 1 协议
 *
 */
 
	public class Encoder extends MessageToByteEncoder<Object>{
 
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg,
			ByteBuf out) throws Exception {
		 
		if (msg instanceof String) {//心跳
			out.writeByte(0x00);
			return;
		}
			//协议
			byte[] data=ByteObjConverter.ObjectToByte(msg);
			out.writeByte(0x01);
			out.writeInt(data.length);
			out.writeBytes(data);
		
	}
}
