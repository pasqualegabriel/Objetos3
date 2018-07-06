package player.inventory

import item._
import player.equipment.SlotPart.SlotPart
import player.equipment.{Equipment, SlotPart}

class Slot {

  var slots: Map[SlotPart, Item with Equipment] = Map(SlotPart.Helmet -> new EmptySlot, SlotPart.Armor -> new EmptySlot,
    SlotPart.LeftHand -> new EmptySlot, SlotPart.RightHand -> new EmptySlot, SlotPart.Glove -> new EmptySlot)

  def addPart(aSlotPart: SlotPart, aEquipment: Item with Equipment): Unit = slots += (aSlotPart -> aEquipment)

  def throwOutEquipment(aSlotPart: SlotPart): Unit = slots += (aSlotPart -> new EmptySlot)

  def getPart(aSlotPart: SlotPart): Item with Equipment = slots(aSlotPart)

  def getParts: List[Item with Equipment] = slots.values.filter(!_.isEmptySlot).toList

}
