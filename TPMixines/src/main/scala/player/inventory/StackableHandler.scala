package player.inventory

import item._

object StackableHandler {

  val inventoryStates: List[StackableCreation] = List(NewStack, AddToExistingStack)

  def addItem(possibleStacks: List[Item], inventory: StackableInventory, item: Item): Unit = {
    this.inventoryStates.find(s => s.canTakeCharge(possibleStacks, inventory, item)).get.addItem(possibleStacks, inventory, item)
  }

  trait StackableCreation {

    def canTakeCharge(possibleStacks: List[Item], inventory: StackableInventory, item: Item): Boolean

    def addItem(possibleStacks: List[Item], inventory: StackableInventory, item: Item)
  }

  object NewStack extends StackableCreation {

    override def canTakeCharge(possibleStacks: List[Item], inventory: StackableInventory, item: Item): Boolean = {

      possibleStacks.isEmpty
    }

    override def addItem(possibleStacks: List[Item], inventory: StackableInventory, item: Item): Unit = {

      val stackableItem = item.asInstanceOf[Stackable]
      stackableItem.setTop(stackableItem)
      inventory.addItem(item)
    }
  }

  object AddToExistingStack extends StackableCreation {

    override def canTakeCharge(possibleStacks: List[Item], inventory: StackableInventory, item: Item): Boolean = possibleStacks.nonEmpty

    override def addItem(possibleStacks: List[Item], inventory: StackableInventory, item: Item): Unit = {

      val stackTop = possibleStacks.head.asInstanceOf[Stackable]
      stackTop.stack(item.asInstanceOf[Stackable],stackTop)
    }
  }

}