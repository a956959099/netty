package com.faota.sdk.client.connect;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 解码器
 * 
 * 编码方式:类型(1byte),长度(4byte)
 * 
 * 类型: 0 心跳 , 1 文件 , 2 协议
 *
 */
public class Decoder extends ByteToMessageDecoder{
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) throws Exception {
		
		if (msg.readableBytes()<1) {
			return;
		}
		//设置标记
		msg.markReaderIndex();
		
		//消息类型
		byte type=msg.readByte();
		
		if (type==0x0) {//心跳包
			out.add(new String("ping"));
			return;
		}
		
		if (msg.readableBytes()<4) {
			return;
		}
		
		//数据长度
		int dataLength=msg.readInt();
		
		 
		//如果没有接收完整 回到标记位置
		if (msg.readableBytes()<dataLength) {
			msg.resetReaderIndex();
			return;
		}
		 	
		//反序列化协议对象
			byte[] data=new byte[dataLength];
			msg.readBytes(data);
			Object o=ByteObjConverter.ByteToObject(data);
			out.add(o);
	}

}
