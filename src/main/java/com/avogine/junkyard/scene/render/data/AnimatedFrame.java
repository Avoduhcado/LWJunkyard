package com.avogine.junkyard.scene.render.data;

import org.joml.Matrix4f;

import com.avogine.junkyard.scene.render.util.RenderConstants;

public class AnimatedFrame {

    private final Matrix4f[] jointMatrices;

    public AnimatedFrame() {
        jointMatrices = new Matrix4f[RenderConstants.MAX_JOINTS];
        setMatrices();
    }
    
    public void setMatrices() {
    	for(int i = 0; i < jointMatrices.length; i++) {
    		jointMatrices[i] = new Matrix4f();
    	}
    }

    public Matrix4f[] getJointMatrices() {
        return jointMatrices;
    }

    public void setMatrix(int pos, Matrix4f jointMatrix) {
        jointMatrices[pos] = jointMatrix;
    }
}
