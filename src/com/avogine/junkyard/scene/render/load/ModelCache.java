package com.avogine.junkyard.scene.render.load;

import java.util.HashMap;
import java.util.Map;

import com.avogine.junkyard.scene.render.data.ModelData;

public class ModelCache {
	
	private static Map<String, ModelData> models = new HashMap<>();
	
	public static ModelData getModel(String modelName) {
		if(models.containsKey(modelName)) {
			return models.get(modelName);
		}
		
		ModelData model = null;
		try {
			model = AnimatedModelLoader.loadAnimatedModel(modelName);
			System.out.println("Loaded an animated model for " + modelName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		models.put(modelName, model);
		return model;
	}
	
	public static ModelData getStaticModel(String modelName) {
		if(models.containsKey(modelName)) {
			return models.get(modelName);
		}
		
		ModelData model = null;
		try {
			model = StaticModelLoader.loadStaticModel(modelName);
			System.out.println("Loaded a static model for " + modelName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		models.put(modelName, model);
		return model;
	}
	
}
