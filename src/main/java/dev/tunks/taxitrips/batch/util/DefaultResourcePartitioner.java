package dev.tunks.taxitrips.batch.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;

public class DefaultResourcePartitioner extends MultiResourcePartitioner {
	private Map<String,Object> contextConfig = new HashMap<String,Object>();
	
	public DefaultResourcePartitioner(Map<String, Object> config) {
		super();
		this.contextConfig = config;
	}

	public DefaultResourcePartitioner(Map<String, Object> config, Resource[] resources) 
	{
		super();
		this.contextConfig = config;
		this.setResources(resources);
	}
	
	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> map =  super.partition(gridSize);
		if(!contextConfig.isEmpty()) {
			Set<String> partitionKeys = map.keySet();
			for(Map.Entry<String, Object> en: contextConfig.entrySet()) {
				for(String key: partitionKeys) {
					map.get(key).put(en.getKey(), en.getValue());
				}
			}	
		}
		return map;
	}

}
