package com.avogine.junkyard.scene;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.avogine.junkyard.scene.entity.EntityComponent;

public class Cast {

	public static final int CAMERA_ID = -1;
	public static final int DUMMY_BODY_ID = -2;
	public static final int DUMMY_LIGHT_ID = -3;

	private AtomicInteger aInt;
	private Map<Integer, ComponentMap> entityComponentMap = new HashMap<>();
	
	public Cast() {
		aInt = new AtomicInteger();
	}

	public int newEntity() {
		return aInt.incrementAndGet();
	}
	
	public void addComponent(Integer ID, EntityComponent component) {
		if(entityComponentMap.containsKey(ID)) {
			entityComponentMap.get(ID).put(component.toSuperclass(), component);
		} else {
			entityComponentMap.put(ID, new ComponentMap(Map.of(component.toSuperclass(), component)));
		}
		
		System.out.println("Added a new " + component.toSuperclass().getSimpleName() + " to " + ID);
	}
	
	/**
	 * Will search for a component owned by the entity with the specified ID.<br>
	 * This method searches by the top level component class, so for example if you want a KineticBody or StaticBody you will need to pass in Body as your class.<br>
	 * <br>
	 * TODO Change the map to store arbitrary sub types maybe? It might cause conflicts if you attach two different subclasses of the same component type though.
	 * 
	 * @param ID
	 * @param clazz
	 * @return The relevant class component correctly cast to whatever type you passed in
	 */
	public <T extends EntityComponent> Optional<T> getComponent(Integer ID, Class<T> clazz) {
		if(!entityComponentMap.containsKey(ID) || entityComponentMap.get(ID).keySet().stream().noneMatch(clazz::isAssignableFrom)) {
			return Optional.empty();
		}
		
		return Optional.of(entityComponentMap.get(ID).getAs(clazz));
	}
	
	/* TODO Make this return a list of optionals? 
	 * Might be unnecessary since this will find entities strictly with all of the listed components
	 * Could be useful to just try and bulk fetch all components we can for a given entity?
	 */
	
	public Set<ComponentMap> getEntitiesWithComponent(Class<? extends EntityComponent> clazz) {
		return entityComponentMap.entrySet().stream()
			.map(Map.Entry::getValue)
			.filter(e -> e.containsKey(clazz))
			.collect(Collectors.toSet());
	}
	
	public Set<ComponentMap> getEntitiesWithComponent(Class<? extends EntityComponent> clazz, int limit) {
		return entityComponentMap.entrySet().stream()
			.map(Map.Entry::getValue)
			.filter(e -> e.containsKey(clazz))
			.limit(limit)
			.collect(Collectors.toSet());
	}
	
	public Set<ComponentMap> getEntitiesWithComponents(Class<?>...classes) {
		return entityComponentMap.entrySet().stream()
				.map(Map.Entry::getValue)
				.filter(e -> containsAllKeys(e, classes))
				.collect(Collectors.toSet());
	}
	
	public Set<ComponentMap> getEntitiesWithComponents(int limit, Class<?>...classes) {
		return entityComponentMap.entrySet().stream()
				.map(Map.Entry::getValue)
				.filter(e -> containsAllKeys(e, classes))
				.limit(limit)
				.collect(Collectors.toSet());
	}
	
	public boolean containsAllKeys(Map<Class<?>, EntityComponent> componentMap, Class<?>...classes) {
		for(Class<?> clazz : classes) {
			if(!componentMap.containsKey(clazz)) {
				return false;
			}
		}
		return true;
	}
	
}
