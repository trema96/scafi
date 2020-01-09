/*
 * Copyright (C) 2016-2017, Roberto Casadei, Mirko Viroli, and contributors.
 * See the LICENCE.txt file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package it.unibo.scafi.renderer3d.node

import javafx.scene.{Group, Node}
import javafx.scene.shape.{MeshView, Sphere}
import javafx.scene.text.Text
import org.scalafx.extras.onFX

/** Helper object for [[SimpleNetworkNode]] with various utility methods. */
private[node] object NetworkNodeHelper {

  /** Sets the provided sphere's radius as requested. If the radius is zero, it removes if from the scene for performance
   * reasons.
   * @param sphere the sphere to modify
   * @param radius the new radius
   * @param group the group that should contain the provided sphere */
  def setSphereRadius(sphere: Sphere, radius: Double, group: Group): Unit = onFX {
    if(radius < 1){
      group.getChildren.remove(sphere)
    } else if(!group.getChildren.contains(sphere)) {
      group.getChildren.add(sphere)
      sphere.setRadius(radius)
    }
  }

  /** Shows or hides the cone that is seen when the node is moving.
   * @param show whether the cone should be rendered or not
   * @param cone the cone to render
   * @param group the group that should contain the provided cone */
  def showMovement(show: Boolean, node: Node, cone: MeshView, group: Group): Unit = onFX {
    if(show && !cone.isVisible) {
      group.getChildren.add(cone)
    } else if(!show && cone.isVisible) {
      group.getChildren.remove(cone)
    }
    node.setVisible(!show)
    cone.setVisible(show)
  }

  /** Sets a new text to the label and adds the label to the provided group.
   * @param text the new text of the label
   * @param label the label to modify
   * @param group the group that should contain the provided label */
  def addAndSetLabel(text: String, label: Text, group: Group): Unit = {
    label.setText(text)
    label.setVisible(true)
    group.getChildren.add(label)
  }

}
