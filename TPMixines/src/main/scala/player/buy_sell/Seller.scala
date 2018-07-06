package player.buy_sell

import item._

class Seller {

  var items: List[Item] = Nil

  def addItem(item: Item with Merchantable) :Unit = items ::= item

  def removeItem(item: Item) :Unit = items = items.filter(!_.equals(item))
}
