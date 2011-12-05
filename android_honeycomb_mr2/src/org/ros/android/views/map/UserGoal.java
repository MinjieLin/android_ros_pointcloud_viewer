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

package org.ros.android.views.map;

import android.graphics.Point;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class UserGoal extends Goal {

  private float scaleFactor;

  @Override
  public void draw(GL10 gl) {
    System.err.print("Drawing user goal.");
    gl.glPushMatrix();
    gl.glDisable(GL10.GL_CULL_FACE);
    gl.glFrontFace(GL10.GL_CW);
    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
    gl.glTranslatef((float) pose.position.x, (float) pose.position.y, 0);
    gl.glRotatef(theta - 90, 0, 0, 1);
    gl.glScalef(getScaleFactor(), getScaleFactor(), getScaleFactor());
    gl.glColor4f(0.847058824f, 0.243137255f, 0.8f, 1f);
    gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, goalIndexCount);
    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    gl.glPopMatrix();
  }

  @Override
  public void init() {
    float[] goalVertices = new float[10 * 3];
    goalVertices[0] = 0f;
    goalVertices[1] = 0f;
    goalVertices[2] = 0f;
    goalVertices[3] = 0f;
    goalVertices[4] = 21f;
    goalVertices[5] = 0f;
    goalVertices[6] = 3f;
    goalVertices[7] = 3f;
    goalVertices[8] = 0f;
    goalVertices[9] = 10.5f;
    goalVertices[10] = 0f;
    goalVertices[11] = 0f;
    goalVertices[12] = 3f;
    goalVertices[13] = -3f;
    goalVertices[14] = 0f;
    goalVertices[15] = 0f;
    goalVertices[16] = -10.5f;
    goalVertices[17] = 0f;
    goalVertices[18] = -3f;
    goalVertices[19] = -3f;
    goalVertices[20] = 0f;
    goalVertices[21] = -10.5f;
    goalVertices[22] = 0f;
    goalVertices[23] = 0f;
    goalVertices[24] = -3f;
    goalVertices[25] = 3f;
    goalVertices[26] = 0f;
    // Repeat of point 1
    goalVertices[27] = 0f;
    goalVertices[28] = 21f;
    goalVertices[29] = 0f;
    ByteBuffer goalVertexByteBuffer =
        ByteBuffer.allocateDirect(goalVertices.length * Float.SIZE / 8);
    goalVertexByteBuffer.order(ByteOrder.nativeOrder());
    vertexBuffer = goalVertexByteBuffer.asFloatBuffer();
    vertexBuffer.put(goalVertices);
    vertexBuffer.position(0);
  }

  public float getScaleFactor() {
    return scaleFactor;
  }

  public void setScaleFactor(float scaleFactor) {
    this.scaleFactor = scaleFactor;
  }

  /**
   * Update the location of the goal that the user is trying to specify.
   * 
   * @param realWorldLocation
   *          The real world coordinates (in meters) representing the location
   *          of the user's desired goal.
   */
  public void updateUserGoalLocation(Point realWorldLocation) {
    pose.position.x = -realWorldLocation.x;
    pose.position.y = -realWorldLocation.y;
  }

  /**
   * Update the orientation of the goal that the user is trying to specify.
   * 
   * @param theta
   *          The orientation of the desired goal in degrees.
   */
  public void updateUserGoalOrientation(float theta) {
    this.theta = theta;
  }
}