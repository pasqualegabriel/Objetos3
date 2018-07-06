package item.uses

import item.Item
import player.Player

trait Consumable extends Item {

  def consume(): Unit

  def canBeUsed: Boolean = uses > 0

  var value: Int = 1

  def applyEffect(player: Player): Unit = consume()
}
