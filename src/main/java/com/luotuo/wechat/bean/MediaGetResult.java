package com.luotuo.wechat.bean;

import lombok.Data;

@Data
public class MediaGetResult extends BaseResult {
	private String filename;
	private String contentType;
	private byte[] bytes;
	private String video_url;	//如果返回的是视频消息素材
}
