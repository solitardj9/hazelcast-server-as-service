package com.example.demo.systemInterface.imdgInterface.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.systemInterface.imdgInterface.service.InMemoryManager;
import com.example.demo.systemInterface.imdgInterface.service.InMemoryWatcher;

/**
 * 
 * @author solitardj9 gmail.com
 * reference : https://www.baeldung.com/spring-boot-app-as-a-service
 *
 */
@Service("inMemoryWatcher")
public class InMemoryWatcherImpl implements InMemoryWatcher {

	private static final Logger logger = LoggerFactory.getLogger(InMemoryWatcherImpl.class);

    @Autowired
    InMemoryManager inMemoryManager;
    
    @Value("${hazelcastClient.systemInterface.imdgInterface.imdgWatcher.batch.start}")
    private String batchStart;
    
    @Value("${hazelcastClient.systemInterface.imdgInterface.imdgWatcher.batch.stop}")
    private String batchStop;
    
    @Value("${hazelcastClient.systemInterface.imdgInterface.imdgWatcher.server.port}")
    private Integer serverPort;
    
    private final Integer retryLimit = 3;
    
    @PostConstruct
    public void init() {
    	//
    	logger.info("[InMemoryWatcher].init : InMemoryWatcher initialization starts.");
    	
    	while (true) {
    		logger.info("[InMemoryWatcher].init : hazelcast server try to stop before start.");
    		//stop();
	    	
	    	try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    
	    	logger.info("[InMemoryWatcher].init : hazelcast server try to start.");
	    	startImdg();
	    	
	    	try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	
	    	Boolean isConnected = false;
	    	Integer retryCount = 0;
	    	while (true) {
	    		//
	    		if (retryCount >= retryLimit) {
	    			logger.error("[InMemoryWatcher].init : hazelcast client connection retry is over limit.");
	    			break;
	    		}
	    		
	    		isConnected = inMemoryManager.connect();
	    		if (isConnected) {
	    			break;
	    		}
	    		
	    		logger.error("[InMemoryWatcher].init : hazelcast client try to connect.(retryCount = " + retryCount + ")");
	    		retryCount++;
	    		
	    		try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
	    	
	    	if (isConnected)
	    		break;
    	}
	    	
	    logger.info("[InMemoryWatcher].init : hazelcast client is connected.");
	    logger.info("[InMemoryWatcher].init : InMemoryWatcher initialization stops.");
    }
    
    @PreDestroy
    public void destroy() {
		//
    	logger.info("[InMemoryWatcher].destroy : InMemoryWatcher stops.");
    	stop();
	}
    
	@Override
	public void start() {
		//
		stop();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		startImdg();
	}

	@Override
	public void stop() {
		//
		for (int i = 0 ; i < 3 ; i++) {
			stopImdg();
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Boolean isConnected() {
		//
		return inMemoryManager.isConnected();
	}
	
    
    private void startImdg() {
    	//
    	//String[] command = {"cmd", "/c", "C:\\workspace\\Sample Codes\\Hazelcast\\Server\\hazelcast-3.12.9\\bin\\start.bat"};
    	String[] command = {"cmd", "/c", batchStart};
		try {
			Process process = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	private void stopImdg() {
		//
		//ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/C", "netstat -n -o -a -p | findstr :5701");
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/C", "netstat -n -o -a | findstr 0.0.0.0:" + serverPort.toString() + " | findstr LISTENING");
		
	    Process process;
		try {
			process = builder.start();
			process.waitFor();
			
			int bytesRead = -1;
		    byte[] bytes = new byte[1024];
		    String output = "";
		    while ((bytesRead = process.getInputStream().read(bytes)) > -1) {
		        output = output + new String(bytes, 0, bytesRead);
		    }
		    logger.info("[InMemoryWatcher].stopImdg : The netstat command response is " + output);
		    
		    String pid = output.split("\\s+")[5];
		    logger.info("[InMemoryWatcher].stopImdg : pid is " + pid);
		    ProcessBuilder taskkill = new ProcessBuilder().inheritIO();
		    taskkill.command("taskkill.exe", "/pid", pid).start();
		    
		} catch (IOException | InterruptedException e) {
			logger.info("[InMemoryWatcher].stopImdg : error = " + e);
		}
		
		//String[] command = {"cmd", "/c", "C:\\workspace\\Sample Codes\\Hazelcast\\Server\\hazelcast-3.12.9\\bin\\stop.bat"};
//		String[] command = {"cmd", "/c", batchStop};
//		try {
//			Process process = Runtime.getRuntime().exec(command);
//			process.waitFor();
//		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
//		}
	}
}