/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.android.view.visualization;

import android.opengl.GLSurfaceView;
import org.ros.android.view.visualization.layer.Layer;
import org.ros.android.view.visualization.layer.TfLayer;
import org.ros.namespace.GraphName;
import org.ros.rosjava_geometry.FrameTransformTree;
import org.ros.rosjava_geometry.Transform;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Renders all layers of a navigation view.
 * 
 * @author moesenle@google.com (Lorenz Moesenlechner)
 */
public class XYOrthographicRenderer implements GLSurfaceView.Renderer {
  /**
   * List of layers to draw. Layers are drawn in-order, i.e. the layer with
   * index 0 is the bottom layer and is drawn first.
   */
  private List<Layer> layers;

  private FrameTransformTree frameTransformTree;

  private Camera camera;

  public XYOrthographicRenderer(FrameTransformTree frameTransformTree, Camera camera) {
    this.frameTransformTree = frameTransformTree;
    this.camera = camera;
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    Viewport viewport = new Viewport(width, height);
    viewport.apply(gl);
    camera.setViewport(viewport);
    gl.glMatrixMode(GL10.GL_MODELVIEW);
    gl.glLoadIdentity();
    gl.glEnable(GL10.GL_BLEND);
    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    gl.glEnable(GL10.GL_POINT_SMOOTH);
    gl.glDisable(GL10.GL_LIGHTING);
    gl.glDisable(GL10.GL_DEPTH_TEST);
    gl.glEnable(GL10.GL_COLOR_MATERIAL);
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    gl.glLoadIdentity();
    camera.apply(gl);
    drawLayers(gl);
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
  }

  private void drawLayers(GL10 gl) {
    if (layers == null) {
      return;
    }
    for (Layer layer : getLayers()) {
      gl.glPushMatrix();
      if (layer instanceof TfLayer) {
        GraphName layerFrame = ((TfLayer) layer).getFrame();
        // TODO(moesenle): throw a warning that no transform could be found and
        // the layer has been ignored.
        if (layerFrame != null
            && frameTransformTree.canTransform(layerFrame, camera.getFixedFrame())) {
          Transform transform =
              frameTransformTree.newFrameTransform(layerFrame, camera.getFixedFrame())
                  .getTransform();
          OpenGlTransform.apply(gl, transform);
          layer.draw(gl);
        }
      } else {
        layer.draw(gl);
      }
      gl.glPopMatrix();
    }
  }

  public List<Layer> getLayers() {
    return layers;
  }

  public void setLayers(List<Layer> layers) {
    this.layers = layers;
  }
}