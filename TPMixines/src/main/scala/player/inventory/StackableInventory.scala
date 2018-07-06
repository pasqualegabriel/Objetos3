package player.inventory

import item._


class StackableInventory(inventoryCapacity: Int) extends Inventory[Item](inventoryCapacity) {

  //Given a stackable item, it's added to an existing stack in the inventory or to a new stack.
  def addStackableItem(item:Item): Unit = {

    //Change filter condition.
    val possibleStacks = items.filter(i=>i.itemType==item.itemType && i.freeCapacity>0)

    StackableHandler.addItem(possibleStacks,this,item)
  }

  //Given an item, it is removed from the stack where it is placed or the inventory's items.
  def remove(item: Item): Unit = {



    item.removeFromInventory(this)
  }



}
