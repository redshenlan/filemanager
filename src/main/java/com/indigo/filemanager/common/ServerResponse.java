package com.indigo.filemanager.common;

import lombok.Data;

@Data
public class ServerResponse {
	
	private boolean success;
	
	private String code;
	
	private String msg;
	
	private Object data;
	
	public ServerResponse() {
		this.success = true;
		this.code = "100";
		this.msg = "成功";
		this.data = null;
	}
	
	public ServerResponse(boolean success, String code, String msg, Object data) {
		this.success = success;
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	
	public static ServerResponse success() {
		return new ServerResponse();
	}
	
	public static ServerResponse success(Object data) {
		ServerResponse resp = new ServerResponse();
		resp.setData(data);
		return resp;
	}
	
	public static ServerResponse failure(String code, String msg) {
		return new ServerResponse(false, code, msg, null);
	}

}
