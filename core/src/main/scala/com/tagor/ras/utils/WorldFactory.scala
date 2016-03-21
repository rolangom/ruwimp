package com.tagor.ras.utils

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d._
import com.tagor.ras.models.{Block, BlockType}
import com.tagor.ras.utils.Const._
/**
 * Created by rolangom on 6/8/15.
 */
object WorldFactory {

  val world = new World(new Vector2(0, -17), true)

  def dispose(): Unit = {
    val bodies = new com.badlogic.gdx.utils.Array[Body]()
    world.getBodies(bodies)
    bodies.items.foreach(b =>
      try {world.destroyBody(b)}
      catch {case e: Exception => e.printStackTrace()})
    world.dispose()
  }

  def newBlock(btype: BlockType): Body =
    createBody(btype, 0f, 0f)

  def createBody(btype: BlockType,
                 x: Float,
                 y: Float): Body = {
    val bdef = new BodyDef
    val fdef = new FixtureDef
    val shape = new PolygonShape

    bdef.position.set(x / PPM, y * PPM)
    bdef.fixedRotation = true
    bdef.`type` = BodyDef.BodyType.KinematicBody
    bdef.active = false

    val body = world createBody bdef
    shape.setAsBox(
      btype.width * btype.scale / 2 / PPM,
      btype.height * btype.scale / 2 / PPM)
    fdef.shape = shape
    fdef.filter.categoryBits = btype.category
    fdef.filter.maskBits = btype.mask
    fdef.restitution = 0f
    fdef.friction = 0f
    body.createFixture(fdef)
      .setUserData(GroundStrType)

    shape.dispose()
    body
  }

  def createTopBody(x: Float,
                    y: Float,
                    w: Float): Body = {
    val bdef = new BodyDef
    val fdef = new FixtureDef
    val shape = new PolygonShape

    bdef.position.set(x / PPM, y / PPM)
    bdef.fixedRotation = true
    bdef.`type` = BodyDef.BodyType.KinematicBody

    val body = world createBody bdef
    shape.setAsBox(w / 2 / PPM, 1 / PPM)
    fdef.shape = shape
    fdef.filter.categoryBits = CategoryTop
    fdef.filter.maskBits = MaskTop
    fdef.restitution = 0f
    fdef.friction = 0f
    fdef.density = 1f
    body.createFixture(fdef)
      .setUserData(TopStrType)

    shape.dispose()
    body
  }

  def newRunner(): Body =
    createRunner(Const.RunnerX, Const.RunnerY)

  def createRunner(x : Float, y : Float): Body = {
    val bodyDef = new BodyDef
    bodyDef.`type` = BodyDef.BodyType.DynamicBody
    bodyDef.position.set(x / PPM, y / PPM)
    bodyDef.fixedRotation = true

    val body = world createBody bodyDef
    body setGravityScale RunnerGravityScale

    createRunnerFixture(
      body,
      createPlayerShape(UpScale),
      CategoryPlayerUp,
      MaskPlayerUp,
      false,
      PlayerStrType)

    createRunnerFixture(
      body,
      createPlayerFootShape(UpScale),
      CategoryPlayerUp,
      MaskPlayerUp,
      true,
      FootStrType)

    body.resetMassData()
    body
  }

  private def createRunnerFixture(body:Body,
                          shape: Shape,
                          categBit: Short,
                          maskBit: Short,
                          isSensor: Boolean,
                          userData: String): Body = {
    val fixt = new FixtureDef
    fixt.isSensor = isSensor
    fixt.shape = shape
    fixt.filter.categoryBits = categBit
    fixt.filter.maskBits = maskBit
    fixt.friction = 0f
    fixt.restitution = 0f
    body.createFixture(fixt).setUserData(userData)
    shape.dispose()
    body
  }

  private def createPlayerShape(scale: Float): Shape = {
    val shape = new PolygonShape
    val ns = RunnerHeight * scale / 2 / PPM
    shape.setAsBox(ns / 2, ns)
    shape
  }

  private def createPlayerFootShape(scale: Float): Shape = {
    val shape = new CircleShape()
    shape.setRadius(RunnerWidth * 1.25f * scale / 2 / PPM)
    shape.setPosition(
      new Vector2(0f, - RunnerHeight * .75f * scale / 2 / PPM))
    shape
  }

  private def getCategMask(isGoingUp: Boolean): (Short, Short) =
    if (isGoingUp) (CategoryPlayerUp, MaskPlayerUp)
    else (CategoryPlayerDown, MaskPlayerDown)

  def configBodyBits(body: Body, isDimenUp: Boolean): Unit = {
    val (category, mask) = getCategMask(isDimenUp)
    configFixtureBits(body, category, mask)
  }

  def configBodyBitsOnAir(body: Body): Unit =
    configFixtureBits(body, CategoryPlayerMiddle, MaskPlayerMiddle)

  private def configFixtureBits(body: Body,
                                category: Short,
                                mask: Short): Unit = {
    val iter = body.getFixtureList.iterator()
    while (iter.hasNext) {
      val fixture = iter.next()
      val filter = fixture.getFilterData
      filter.categoryBits = category
      filter.maskBits = mask
      fixture.setFilterData(filter)
    }
  }

  def scaleFixtures(body:Body, isDimenUp:Boolean): Unit = {
    val scale = if (isDimenUp) UpScale else DownScale
    val iter = body.getFixtureList.iterator()
    while (iter.hasNext) {
      val fix = iter.next()
      fix.getShape match {
        case cs: CircleShape =>
          cs.setRadius(RunnerWidth * scale / 2 / PPM)
        case ps: PolygonShape =>
          val ns = RunnerHeight * scale / 2 / PPM
          ps.setAsBox(ns / 2, ns)
        case _ => ()
      }
    }
    body.resetMassData()
  }

  def blockIfLanded(fixtureA: Fixture, fixtureB: Fixture): Option[Block] = {
    (fixtureA.getUserData, fixtureB.getUserData) match {
      case (Const.FootStrType, Const.GroundStrType) =>
        Some(fixtureB.getBody.getUserData.asInstanceOf[Block])
      case (Const.GroundStrType, Const.FootStrType) =>
        Some(fixtureA.getBody.getUserData.asInstanceOf[Block])
      case _ => None
    }
  }

  def isPlayerAndGround(fixtureA: Fixture, fixtureB: Fixture): Boolean = {
    (fixtureA.getUserData, fixtureB.getUserData) match {
      case (_, Const.FootStrType) | // Const.GroundStrType
           (Const.FootStrType, _) => true // Const.GroundStrType
      case _ => false
    }
  }
}
