package com.solitardj9.hazelcastServer.serviceInterface.imdgManagerInterface.model;

public class ResponseDefualt {
	//
	private Integer status;
	
	private String message;
	
	public ResponseDefualt() {
		
	}
	
	public ResponseDefualt(Integer status, String message) {
		this.status = status;
		this.message = message;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ResponseDefualt [status=" + status + ", message=" + message + "]";
	}
}