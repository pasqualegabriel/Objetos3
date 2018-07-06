package player.equipment

import exceptions.{CantBeEquippedExceptionEquipmentNotIdentified, CantBeEquippedExceptionPlayerDoesNotHaveTheRequirmentsForTheEquipment, CantBeEquippedExceptionSlotIsAlreadyEquipped}
import item.Item
import player.Player
import SlotPart.SlotPart

object EquipmentHandler {

  val equipmentStates: List[Equip]= List(NotIdentifiable,RequirmentsNotMet, OcupiedSlot, ReadyToEquip)


  def equip(aSlot: SlotPart, aEquipment: Item with Equipment, player: Player): Unit = {
    this.equipmentStates.find(_.takeCharge(aSlot,aEquipment,player)).get.equip(aSlot, aEquipment, player)
  }

  trait Equip {
    def takeCharge(aSlot: SlotPart, aEquipment: Item with Equipment, player: Player): Boolean

    def equip(aSlot: SlotPart, aEquipment: Item with Equipment, player: Player): Unit
  }

  object NotIdentifiable extends Equip {

    override def takeCharge(aSlot: SlotPart, aEquipment: Item with Equipment, player: Player): Boolean = !aEquipment.isIdentified

    override def equip(aSlot: SlotPart, aEquipment: Item with Equipment, player: Player): Unit = throw new CantBeEquippedExceptionEquipmentNotIdentified
  }

  object RequirmentsNotMet extends Equip{
    override def takeCharge(aSlot: SlotPart, aEquipment: Item with Equipment, player: Player): Boolean = !aEquipment.comply(player, aSlot)

    override def equip(aSlot: SlotPart, aEquipment: Item with Equipment, player: Player): Unit = throw new CantBeEquippedExceptionPlayerDoesNotHaveTheRequirmentsForTheEquipment
  }

  object OcupiedSlot extends Equip{
    override def takeCharge(aSlot: SlotPart, aEquipment: Item with Equipment, player: Player): Boolean = !player.getPart(aSlot).isEmptySlot

    override def equip(aSlot: SlotPart, aEquipment: Item with Equipment, player: Player): Unit =  throw new CantBeEquippedExceptionSlotIsAlreadyEquipped
  }


  object ReadyToEquip extends Equip {
    override def takeCharge(aSlot: SlotPart, aEquipment: Item with Equipment, player: Player): Boolean = aEquipment.isIdentified && aEquipment.comply(player, aSlot) && player.getPart(aSlot).isEmptySlot

    override def equip(aSlot: SlotPart, aEquipment: Item with Equipment, player: Player): Unit = {
      player.throwOut(aEquipment)
      player.getSlots.addPart(aSlot, aEquipment)
      player.getStats.calculateEffects(player.getSlots.getParts)
    }
  }

}
