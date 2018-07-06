package player.inventory

import exceptions.InsufficientCapacityException
import item._

class Inventory[T <: Item](var capacity: Int, var items: List[T] = Nil) {

  //Add an item to the inventory if it has enough capacity for the item.
  def addItem(item: T): Unit = {

    if (this.freeCapacity() < item.totalVolume) throw new InsufficientCapacityException
    items ::= item
  }

  //Returns the amount of free space in the inventory.
  def freeCapacity(): Int = capacity - items.map(i => i.totalVolume).sum

  //Removes an item.
  def removeFromInventory(item: T): Unit = {

    items = items.filter(!_.equals(item))
  }

  def isNonEmpty: Boolean = items.nonEmpty
}