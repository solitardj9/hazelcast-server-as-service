package com.example.demo.systemInterface.imdgInterface.service.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.systemInterface.imdgInterface.model.InMemoryStatus;
import com.example.demo.systemInterface.imdgInterface.service.InMemoryManager;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientConnectionStrategyConfig;
import com.hazelcast.client.config.ClientConnectionStrategyConfig.ReconnectMode;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
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
    
    private Boolean isConnectedOnce;
    
    private final Integer logCountLimit = 180;	// 30 min
    private Integer logCount = 0;
    
    @PostConstruct
    public void init() {
        //
    	isConnectedOnce = false;
    	
        inMemoryStatus = InMemoryStatus.DISCOONECTED;
        
        ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
        
        networkConfig.addAddress(StringUtils.split(hostAddresses, ","));
        
        int connectionAttemptPeriod = 1000;
        networkConfig.setConnectionAttemptPeriod(connectionAttemptPeriod);
        
        int connectionTimeout = 2000;
        networkConfig.setConnectionTimeout(connectionTimeout);
        
        ClientConnectionStrategyConfig connectionStrategyConfig = new ClientConnectionStrategyConfig();
        connectionStrategyConfig.setAsyncStart(true);
        connectionStrategyConfig.setReconnectMode(ReconnectMode.ASYNC);
        clientConfig.setConnectionStrategyConfig(connectionStrategyConfig);
        
        isConnectedOnce = connect();
    }
    
    private Boolean connect() {
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
    		logger.info("[InMemoryManager].connect : error = " + e);
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
    
    /**
     * @스케줄러 걸어서 연결 여부 채크하고 미 연결시 무한루프로 연결처리
     */
    @Scheduled(fixedDelay=10000, initialDelay=10000)
    private void checkConnection() {
    	//
    	// hazelcast client is auto-reconnect
    	if (isConnectedOnce) {
    		if (logCount >= logCountLimit) {
    			logCount = 0;
    			logger.info("[InMemoryManager].checkConnection : connection is " + inMemoryStatus.getStatus());
    		}
    	}
    	else {
    		while (true) {
    			isConnectedOnce = connect();
    			
    			if (isConnectedOnce)
    				break;
    			
    			try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    	
    	logCount++;
    }

	@Override
	public IMap<Object, Object> addMap(String mapName) {
		//
		return instance.getMap(mapName);
	}

	@Override
	public IMap<Object, Object> getMap(String mapName) {
		//
		return instance.getMap(mapName);
	}
}