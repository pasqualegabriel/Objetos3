package player.buy_sell

import exceptions.InsufficientGoldException
import item.Item
import player.Player

object BuyItemHandle {

  val buyStates: List[BuyItem] = List(EnoughGold, InsufficientGold)

  def buy(player: Player, item: Item  with Merchantable, seller: Seller): Unit = {
    buyStates.find(s => s.canTakeCharge(player, item)).get.execute(player, item, seller)
  }

  trait BuyItem {

    def canTakeCharge(player: Player, item: Item): Boolean

    def execute(player: Player, item: Item  with Merchantable, seller: Seller): Unit
  }

  object EnoughGold extends BuyItem {

    def canTakeCharge(player: Player, item: Item): Boolean = !(player.gold < item.getValueOfBuy)

    def execute(player: Player, item: Item  with Merchantable, seller: Seller): Unit = {
      player.grab(item)
      seller.removeItem(item)
      player.subtractGold(item.getValueOfBuy)
    }


  }

  object InsufficientGold extends BuyItem {

    def canTakeCharge(player: Player, item: Item): Boolean = player.gold < item.getValueOfBuy

    def execute(player: Player, item: Item  with Merchantable,  seller: Seller): Unit = throw new InsufficientGoldException
  }

}
