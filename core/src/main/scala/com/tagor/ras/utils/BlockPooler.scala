package com.tagor.ras.utils

import com.badlogic.gdx.utils.{Pools, Pool, ObjectMap}
import com.tagor.ras.models.{ItemToInst, Block, BlockType}

/**
  * Created by rolangom on 12/12/15.
  */
class BlockPooler {

  private lazy val pool = new ObjectMap[BlockType, Pool[Block]]()
  private lazy val itiPool = Pools.get(classOf[ItemToInst])

  private def get(btype: BlockType): Block = {
    if (!pool.containsKey(btype)) {
      pool.put(btype, new Pool[Block]() {
        protected override def newObject(): Block =
          new Block(WorldFactory.newBlock(btype), btype)
      })
    }
    pool.get(btype).obtain()
  }

  def get(iti: ItemToInst): Block =
    get(iti.dimen, iti.size).init(iti)

  def get(dimen: Int, size: Int): Block =
    get(BlockConst.BlockTypes(dimen)(size))

  def free(block: Block): Unit =
    pool.get(block.btype).free(block)

  def getIti: ItemToInst = itiPool.obtain()

  def free(iti: ItemToInst):Unit = itiPool.free(iti)

}
