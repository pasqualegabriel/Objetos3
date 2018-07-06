package player

import item._
import item.itemType.Potion
import item.uses.Consumable
import player.attribute.Attribute.Attribute
import player.equipment.SlotPart.SlotPart
import player.attribute.StatisticsPj
import player.buy_sell.{BuyItemHandle, Merchantable, Seller}
import player.equipment.{Equipment, EquipmentHandler, Linkable, Magic}
import player.inventory.{Inventory, Slot, StackableInventory}

class Player(capacity: Integer=100, beltCapacity: Integer = 20, var gold: Integer = 20, val stats: StatisticsPj = new StatisticsPj(10, 10,1,1,1,1)) {

  val inventory: StackableInventory = new StackableInventory(capacity)

  val belt :Inventory[Item] = new Inventory[Item](beltCapacity)

  val slots: Slot = new Slot

  //If there is enough space in the inventory, the item will be added to the player's inventory.
  def grab(item: Item) :Unit = item.addToInventory(inventory)

  // Return if the item fits in the player's inventory.
  def canGrab(item: Item) :Boolean = inventory.freeCapacity() >= item.totalVolume

  // Discards an item from the player's inventory.
  // Precondition: Item must belong to the Inventory
  def throwOut(item: Item) :Unit = inventory.remove(item)

  def use(item: Item with Consumable): Unit = {
    item.applyEffect(this)
    stats.calculateEffects(slots.getParts)
    if (!item.canBeUsed) throwOut(item)
    throwOutThePotionFromTheBelt(item)
  }

  //If there is enough space in the belt, the potion will be added to the player's belt.
  def addToBelt(potion: Potion) :Unit = belt.addItem(potion)

  // Precondition: Potion must belong to the Inventory
  def moveToBelt(potion: Potion) :Unit = {
    addToBelt(potion)
    throwOut(potion)
  }

  // Precondition: Potion must belong to the belt
  def moveToInventory(potion: Potion) :Unit = {
    grab(potion)
    throwOutThePotionFromTheBelt(potion)
  }

  // Discards a potion from the player's belt.
  // Precondition: Potion must belong to the belt
  def throwOutThePotionFromTheBelt(potion: Item with Consumable) :Unit = belt.removeFromInventory(potion)

  // Increase the amount to the attribute
  def increase(attribute: Attribute, amount: Int): Unit = stats.increase(attribute, amount)

  // Precondition: Item must belong to the seller
  def buy(item: Item  with Merchantable, seller: Seller) :Unit = BuyItemHandle.buy(this, item, seller)

  // Precondition: Item must belong to the Inventory
  def sell(item: Item with Merchantable, seller: Seller) :Unit = {
    seller.addItem(item)
    inventory.removeFromInventory(item)
    gold += item.getValueOfSell
  }

  // Given an amount of gold, the player's gold is substracted.
  def subtractGold(amountOfGold: Int): Unit = gold -= amountOfGold

  // Precondition: Equipment must belong to the Inventory
  def equip(aSlot: SlotPart, aEquipment: Item with Equipment): Unit = {
    EquipmentHandler.equip(aSlot,aEquipment,this)
  }

  // Precondition: Slot must be equipped
  def discard(aSlot: SlotPart): Unit = {
    grab(slots.getPart(aSlot))
    throwOutEquipment(aSlot)
    stats.calculateEffects(slots.getParts)
  }

  def getPart(aSlot:SlotPart): Item with Equipment = slots.getPart(aSlot)

  def getSlots: Slot = slots

  def getStats: StatisticsPj = stats

  def throwOutEquipment(aSlot: SlotPart): Unit = {
    slots.throwOutEquipment(aSlot)
    stats.calculateEffects(slots.getParts)
  }

  // Return the value of the attribute with the effects of the slots
  def get(anAttribute: Attribute): Integer = stats.get(anAttribute)

  // Precondition: aRune must belong to the Inventory
  def addJewelOrRune(aEquip: Item  with Linkable, aRune: Item with Linkable): Unit = {
    inventory.remove(aRune)
    aEquip.insert(aRune)
    stats.calculateEffects(slots.getParts)
  }

  def discardJewelOrRune(aEquip: Item  with Linkable, aRune: Item with Linkable): Unit = {
    inventory.addItem(aRune)
    aEquip.discard(aRune)
    stats.calculateEffects(slots.getParts)
  }

  def identify(aMagicItem: Item with Magic, aParchment: Item): Unit = {
    inventory.remove(aParchment)
    aMagicItem.identify()
    stats.calculateEffects(slots.getParts)
  }

}
