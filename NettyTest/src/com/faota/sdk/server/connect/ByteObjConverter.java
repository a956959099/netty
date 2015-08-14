package com.faota.sdk.server.connect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *字节&对象 转换器
 */
public class ByteObjConverter {
	
	/**
	 * 反序列化
	 * @param bytes 字节数组
	 * @return 反序列化后的对象
	 */
    public static Object ByteToObject(byte[] bytes) {  
        Object obj = null;  
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);  
        ObjectInputStream oi = null;  
        try {  
            oi = new ObjectInputStream(bi);  
            obj = oi.readObject();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                bi.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
            try {  
                oi.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return obj;  
    }  
  
    /**
     * 序列化
     * @param obj 被序列化对象
     * @return 字节数组
     */
    public static byte[] ObjectToByte(Object obj) {  
        byte[] bytes = null;  
        ByteArrayOutputStream bo = new ByteArrayOutputStream();  
        ObjectOutputStream oo = null;  
        try {  
            oo = new ObjectOutputStream(bo);  
            oo.writeObject(obj);  
            bytes = bo.toByteArray();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                bo.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
            try {  
                oo.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return (bytes);  
    }  
}  