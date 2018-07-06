package item


import player.inventory.StackableInventory

trait Stackable extends Item {

  var stackedItem:Stackable = _
  var top: Stackable = this

  //Set the stackableItem as the top of this item.
  def setTop(stackableItem: Stackable): Unit = top = stackableItem

  //Return the next item of the stack.
  def next : Stackable = stackedItem


  //Return the free capacity in the stack.
  override def freeCapacity: Int = {
    this.itemType.stackCapacity - this.getSize
  }

  //Add an stackable item to the player's inventory.
  override def addToInventory(inventory: StackableInventory): Unit = {
    inventory.addStackableItem(this)
  }

  //Removes an item from the stack on the player's inventory.
  override def removeFromInventory(inventory: StackableInventory): Unit = {

    if(this.getSize ==1) inventory.removeFromInventory(this)
    else {
      val newStack = top.pop
      val itemToRemove = newStack._1
      val stackToAdd = newStack._2

      inventory.removeFromInventory(itemToRemove)
      inventory.addStackableItem(stackToAdd)
    }
  }

  //Add an stackable item to the stack.
  def stack(item: Stackable, top: Stackable):Unit= {

    if (stackedItem == null){

      item.setTop(top)
      stackedItem = item

    } else {
      stackedItem.stack(item,top)
    }
  }

  //Returns a tuple with the item to be removed and the rest of the stack.
  private def pop: (Stackable, Stackable) = {
    val stacked = this.stackedItem
    this.stackedItem = null

    if(stacked != null) stacked.updateStack(stacked)

    (this, stacked)
  }

  def updateStack(stackable: Stackable): Unit ={

    if(stackedItem == null) {
      top = stackable
    }
    else {
      top = stackable
      stackedItem.updateStack(stackable)
    }
  }

  //Returns the total volume of the stack.
  override def totalVolume: Int = {
    this.getVolume / 2
  }

  override def getValueOfSell: Int ={
    if(stackedItem == null) valueOfSell
    else valueOfSell + stackedItem.getValueOfSell
  }

  override def getValueOfBuy: Int ={
    if(stackedItem == null) valueOfBuy
    else valueOfBuy + stackedItem.getValueOfBuy
  }

  def getVolume: Int ={
    if(stackedItem == null) volume
    else  volume + stackedItem.getVolume
  }

  def getSize: Int = {
    if(stackedItem == null) 1
    else  1 + stackedItem.getSize
  }

}

