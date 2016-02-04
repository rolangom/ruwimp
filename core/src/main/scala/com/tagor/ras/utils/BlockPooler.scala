package com.tagor.ras.utils

import com.badlogic.gdx.utils.{Pools, Pool, ObjectMap}
import com.tagor.ras.models.{ItemToInst, Block, BlockType}

import scala.collection.mutable

/**
  * Created by rolangom on 12/12/15.
  */
class BlockPooler {

  private val pool = mutable.Map[BlockType, Pool[Block]]()
  private val itiPool = Pools.get(classOf[ItemToInst])

  private def get(btype: BlockType): Block = {
    if (!pool.contains(btype)) {
      pool.put(btype, new Pool[Block]() {
        protected override def newObject(): Block =
          new Block(WorldFactory.newBlock(btype), btype)
      })
    }
    pool(btype).obtain()
  }

  def get(iti: ItemToInst): Block =
    get(iti.dimen, iti.size).init(iti)

  def get(dimen: Int, size: Int): Block =
    get(BlockConst.BlockTypes(dimen)(size))

  def free(block: Block): Unit =
    pool(block.btype).free(block)

  def getIti: ItemToInst = itiPool.obtain()

  def free(iti: ItemToInst):Unit = itiPool.free(iti)

  def init(): Unit = {
    pool.foreach {
      case (t, p) =>
        var i = p.getFree
        while (i > 0) {
          val b = p.obtain()
          b.init()
          p.free(b)
          print(s"$i,")
          i -= 1
        }
    }
  }

}
