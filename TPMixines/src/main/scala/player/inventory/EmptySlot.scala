package player.inventory

import item.Item
import item.itemType.ItemType
import player.equipment.Equipment

class EmptySlot extends Item(ItemType.Empty, 0) with Equipment {
  override def isEmptySlot = true
}
