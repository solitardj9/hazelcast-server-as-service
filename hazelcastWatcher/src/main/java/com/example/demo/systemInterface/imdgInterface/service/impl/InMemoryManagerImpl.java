package com.example.demo.systemInterface.imdgInterface.service.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.systemInterface.imdgInterface.model.InMemoryStatus;
import com.example.demo.systemInterface.imdgInterface.service.InMemoryManager;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleEvent.LifecycleState;
import com.hazelcast.core.LifecycleListener;

import lombok.Getter;
import lombok.Setter;

@Service("inMemoryManager")
public class InMemoryManagerImpl implements InMemoryManager, LifecycleListener {
    //
	private static final Logger logger = LoggerFactory.getLogger(InMemoryManagerImpl.class);
	
    @Value("${hazelcastClient.systemInterface.imdgInterface.imdgManager.host.addresses}")
    private String hostAddresses;
    
    private ClientConfig clientConfig = new ClientConfig();
    private HazelcastInstance instance;
    
    @Getter @Setter
    private InMemoryStatus inMemoryStatus;
    
    @PostConstruct
    public void init() {
        //
        inMemoryStatus = InMemoryStatus.DISCOONECTED;
        
        ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
        
        networkConfig.addAddress(StringUtils.split(hostAddresses, ","));
        
        int connectionAttemptPeriod = 1000;
        networkConfig.setConnectionAttemptPeriod(connectionAttemptPeriod);
        
        int connectionTimeout = 2000;
        networkConfig.setConnectionTimeout(connectionTimeout);
        
        //ClientConnectionStrategyConfig connectionStrategyConfig = new ClientConnectionStrategyConfig();
        //connectionStrategyConfig.setAsyncStart(true);
        //connectionStrategyConfig.setReconnectMode(ReconnectMode.ASYNC);
        //clientConfig.setConnectionStrategyConfig(connectionStrategyConfig);
    }
    
    @Override
    public Boolean connect() {
        //
    	try {
	        instance = HazelcastClient.newHazelcastClient(clientConfig);
	        instance.getLifecycleService().addLifecycleListener(this);
	        
	        try {
	            Thread.sleep(100);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        
	        if (isConnected()) {
	            inMemoryStatus = InMemoryStatus.COONECTED;
	            logger.info("[InMemoryManager].connect : " + inMemoryStatus.toString());
	            return true;
	        }
	        return false;
    	} catch (Exception e) {
    		logger.error("[InMemoryManager].connect : error = " + e);
    		return false;
    	}
    }

    @Override
    public Boolean isConnected() {
        return instance.getLifecycleService().isRunning();
    }
    
    @Override
    public void stateChanged(LifecycleEvent event) {
        //
        System.out.println("[InMemoryManager].stateChanged : event={" + event.toString() + "}");
        
        if (LifecycleState.STARTED == event.getState()) {
            inMemoryStatus = InMemoryStatus.COONECTED;
        }
        else if (LifecycleState.SHUTTING_DOWN == event.getState()) {
            inMemoryStatus = InMemoryStatus.DISCOONECTED;
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            connect();
        }
    }
}