package item

import item.itemType.ItemType
import item.itemType.ItemType.ItemType

import player.inventory.StackableInventory

class Item(val itemType: ItemType = ItemType.Empty, val volume: Int= 1, var uses: Int = 0,
           var valueOfBuy: Int = 10, var valueOfSell: Int = 15, var size: Int=1){

  def stack(item: Stackable): Unit = {}

  def freeCapacity = 0

  def totalVolume: Int = volume

  def addToInventory(inventory: StackableInventory): Unit = inventory.addItem(this)

  def getValueOfSell: Int = valueOfSell

  def getValueOfBuy: Int = valueOfBuy

  def removeFromInventory(inventory: StackableInventory):Unit = inventory.removeFromInventory(this)


}



