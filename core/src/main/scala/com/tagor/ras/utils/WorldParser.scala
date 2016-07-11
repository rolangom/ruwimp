package com.tagor.ras.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils._
/**
  * Created by rolangom on 5/7/16.
  */
class WorldParser(pool: BlockPooler) {

  private val jsonReader = new JsonReader

  def objsFromLevel2(level: Int, num: Int, xSpan: Float = 0): Unit = {
    val jsonValue = jsonReader.parse(Gdx.files.internal(s"levels/level${level}_$num.json"))
    val layers = jsonValue.get("layers")

    var i = 0
    while (i < layers.size) {
      val objs: JsonValue = layers.get(i).get("objects")

      var j = 0
      while (j < objs.size) {
        val obj = objs.get(j)

        val props = obj.get("properties")
        val dimen = props.getInt("dimen")
        val size = props.getInt("size")
        val rotation = obj.getFloat("rotation") * -1

        val w = BlockConst.Width(dimen, size)
        val x = obj.getFloat("x") + wAng(w, rotation) * .5f + xSpan
        val y = -obj.getFloat("y") + Const.Height - BlockConst.Height(dimen) * BlockConst.Scales(dimen) + hAng(w, rotation) * .5f
        val isLast = props.getBoolean("isLast", false)

        post { () =>
          RxMgr.onItiAdded.onNext(pool.getIti.init(
            dimen,
            size,
            x,
            y,
            rotation,
            isLast // i == layers.size - 1 && j == objs.size -1
          ))
        }
        j += 1
      }
      i += 1
    }
  }
}
