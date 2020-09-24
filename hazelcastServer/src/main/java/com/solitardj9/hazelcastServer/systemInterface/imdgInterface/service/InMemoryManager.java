package com.solitardj9.hazelcastServer.systemInterface.imdgInterface.service;

import com.hazelcast.core.IMap;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastDistributedObjectNameConflict;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastIMapNotFound;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastServerAlreadyClosed;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastServerAlreadyOpened;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastServerConfigError;

public interface InMemoryManager {
	//
	public Boolean startServer() throws ExceptionHazelcastServerAlreadyOpened, ExceptionHazelcastServerConfigError;
	
	public Boolean stopServer() throws ExceptionHazelcastServerAlreadyClosed;
	
	public IMap<Object, Object> createMap(String map) throws ExceptionHazelcastServerAlreadyClosed, ExceptionHazelcastDistributedObjectNameConflict;
	
	public IMap<Object, Object> getMap(String map) throws ExceptionHazelcastServerAlreadyClosed, ExceptionHazelcastIMapNotFound;
	
	public void clearMap(String map) throws ExceptionHazelcastServerAlreadyClosed, ExceptionHazelcastIMapNotFound;
}