package com.tagor.ras.models

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * Created by rolangom on 7/8/15.
 */
abstract class B2dActor(val body : Body)
  extends Actor {
  body.setUserData(this)

  /**
    * set Z index 1 because of the background actor
    */
  override def toBack(): Unit = setZIndex(1)

}
