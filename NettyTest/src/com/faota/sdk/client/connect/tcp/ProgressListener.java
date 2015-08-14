package com.faota.sdk.client.connect.tcp;

/**
 * 进度监听
 * 
 *用于发送文件 接收文件的进度回调
 */
public interface ProgressListener {
	
	/**
	 * 进度回调
	 * @param current 当前
	 * @param total 总共
	 */
	void OnProgress(final int current,final int total);
	
}
