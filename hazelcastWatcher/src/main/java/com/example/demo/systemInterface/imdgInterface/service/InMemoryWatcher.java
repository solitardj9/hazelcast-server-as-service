package com.example.demo.systemInterface.imdgInterface.service;

public interface InMemoryWatcher {
	
	public void start();
	
	public void stop();
	
	public Boolean isConnected();

	void destroy() throws Exception;
}