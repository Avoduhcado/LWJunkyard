package com.avogine.junkyard.scene.entity.render;

import com.avogine.junkyard.scene.entity.Model;
import com.avogine.junkyard.scene.entity.event.EntityEvent;
import com.avogine.junkyard.scene.render.data.ModelData;
import com.avogine.junkyard.scene.render.load.ModelCache;
import com.avogine.junkyard.scene.render.load.ModelInfo;

public class StaticModel extends Model {

	public StaticModel(int entity, ModelInfo modelInfo) {
		super(entity, modelInfo);
	}

	@Override
	public void loadMeshes(ModelInfo modelInfo) {
		try {
			ModelData modelData = ModelCache.getStaticModel(modelInfo.getModelName());
			meshes = modelData.getMeshes();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void prepare() {
	}

	@Override
	public void fireEvent(EntityEvent event) {
	}

}
