package com.solitardj9.hazelcastServer.serviceInterface.imdgManagerInterface.model;

import com.hazelcast.core.IMap;

public class ResponseIMap extends ResponseDefualt {
	//
	private IMap<Object, Object> map;

	public ResponseIMap(IMap<Object, Object> map) {
		this.map = map;
	}
	
	public ResponseIMap(Integer status, String message, IMap<Object, Object> map) {
		this.map = map;
		setStatus(status);
		setMessage(message);
	}

	public IMap<Object, Object> getMap() {
		return map;
	}

	public void setMap(IMap<Object, Object> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return "ResponseIMap [map=" + map + ", toString()=" + super.toString() + "]";
	}
}