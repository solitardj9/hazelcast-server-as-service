package com.solitardj9.hazelcastServer.systemInterface.imdgInterface.service.impl;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hazelcast.config.Config;
import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastDistributedObjectNameConflict;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastIMapNotFound;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastServerAlreadyClosed;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastServerAlreadyOpened;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastServerConfigError;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.service.InMemoryManager;

@Service("inMemoryManager")
public class InMemoryManagerImpl implements InMemoryManager {
	//
	private static final Logger logger = LoggerFactory.getLogger(InMemoryManagerImpl.class);
	
	private static HazelcastInstance hazelcastInstance = null;
	
	private static Config config = null; 
	
	private String configPath = "config/hazelcast.xml";

	@PostConstruct
	public void init() {
		//
		try {
			start();
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			logger.info("[InMemoryManager].init : error = " + e);
		}
		
		logger.info("[InMemoryManager].init : Hazelcast Server is loaded.");
	}
	
    private void start() throws FileNotFoundException {
    	//
    	logger.info("[InMemoryManager].start : Hazelcast Server try to start.");
    	
    	try {
    		config = new FileSystemXmlConfig(configPath);
    	} catch (Exception e) {
    		logger.error("[InMemoryManager].start : error = " + e);
    		throw new FileNotFoundException();
    	}
        
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        
        logger.info("[InMemoryManager].start : Hazelcast Server start.");
        
        Set<Member> members = hazelcastInstance.getCluster().getMembers();
        if (members!=null && !members.isEmpty()) {
            for(Member member : members) {
            	logger.info("[InMemoryManager].start : Hazelcast Server member = " + member);
            }
        }
    }

    private void stop() {
    	//
        if (hazelcastInstance != null) {
        	hazelcastInstance.shutdown();
        	hazelcastInstance = null;
        	
        	logger.info("[InMemoryManager].stop : Hazelcast Server is stop.");
        }
    }



	@Override
	public Boolean startServer() throws ExceptionHazelcastServerAlreadyOpened, ExceptionHazelcastServerConfigError {
		//
		if (hazelcastInstance != null)
			throw new ExceptionHazelcastServerAlreadyOpened();
		
		try {
			start();
			return true;
		} catch (FileNotFoundException e) {
			logger.error("[InMemoryManager].startServer : error = " + e);
    		throw new ExceptionHazelcastServerConfigError();
		}
	}



	@Override
	public Boolean stopServer() throws ExceptionHazelcastServerAlreadyClosed {
		//
		if (hazelcastInstance == null)
			throw new ExceptionHazelcastServerAlreadyClosed();
		
		stop();
		
		return true;
	}
	
	@Override
	public IMap<Object, Object> createMap(String map) throws ExceptionHazelcastServerAlreadyClosed, ExceptionHazelcastDistributedObjectNameConflict {
		//
		if (hazelcastInstance == null)
			throw new ExceptionHazelcastServerAlreadyClosed();
		
		if (isExistNameWithOtherDistributedObject(map))
			throw new ExceptionHazelcastDistributedObjectNameConflict();
		
		return hazelcastInstance.getMap(map);
	}

	@Override
	public IMap<Object, Object> getMap(String map) throws ExceptionHazelcastServerAlreadyClosed, ExceptionHazelcastIMapNotFound {
		//
		if (hazelcastInstance == null)
			throw new ExceptionHazelcastServerAlreadyClosed();
		
		if (isExistMap(map))
			return hazelcastInstance.getMap(map);
		else
			throw new ExceptionHazelcastIMapNotFound();
	}
	
	private Boolean isExistMap(String map) {
		//
		Collection<DistributedObject> distributedObjects = hazelcastInstance.getDistributedObjects();
		
		for (DistributedObject distributedObject : distributedObjects) {
			//
			if (distributedObject.getName().equals(map) && distributedObject.toString().startsWith("IMap")) {
				return true;
			}
		}
		
		return false;
	}
	
	private Boolean isExistNameWithOtherDistributedObject(String map) {
		//
		Collection<DistributedObject> distributedObjects = hazelcastInstance.getDistributedObjects();
		
		for (DistributedObject distributedObject : distributedObjects) {
			//
			if (distributedObject.getName().equals(map)) {
				if (!distributedObject.toString().startsWith("IMap"))
					return true;
			}
		}
		
		return false;
	}
	
	public void clearMap(String map) throws ExceptionHazelcastServerAlreadyClosed, ExceptionHazelcastIMapNotFound {
		//
		if (hazelcastInstance == null)
			throw new ExceptionHazelcastServerAlreadyClosed();
		
		if (isExistMap(map))
			hazelcastInstance.getMap(map).clear();
		else
			throw new ExceptionHazelcastIMapNotFound();
	}
}