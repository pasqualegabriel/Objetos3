package itemEffects

import item.uses.Consumable
import player._
import player.attribute.Attribute

trait DefenseIncrease extends Consumable {

  override def applyEffect(player: Player): Unit = {
    super.applyEffect(player)
    player.increase(Attribute.Defense, value)
  }
}
