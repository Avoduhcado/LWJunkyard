package com.avogine.junkyard.scene;

import java.util.HashMap;
import java.util.Map;

import com.avogine.junkyard.scene.entity.EntityComponent;

public class ComponentMap extends HashMap<Class<?>, EntityComponent> {
	private static final long serialVersionUID = 1L;

	public ComponentMap(Map<Class<?>, EntityComponent> components) {
		super(components);
	}

	public <T> T getAs(Class<T> key) {
		if(!containsKey(key)) {
			return null;
		}
		EntityComponent component = get(key);
		if(key.isAssignableFrom(component.getClass())) {
			return key.cast(component);
		}
		throw new IllegalArgumentException("Somehow the object: " + key.getName() + " is not assignable from: " + component.getClass().getName());
	}
	
}
