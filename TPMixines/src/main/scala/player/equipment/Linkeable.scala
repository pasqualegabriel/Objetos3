package player.equipment

import item.itemType.ItemType.ItemType
import item._
import player.attribute.StatisticsPj
import player.inventory.Inventory

trait Linkable extends Item with Equipment {

  var runsAndJewel = new Inventory[Linkable](1)

  def setCapacityRuneAndJewel(aCount:Int):Unit= runsAndJewel.capacity = aCount

  def insert(aRun: Linkable): Unit = runsAndJewel.addItem(aRun)

  override def effect(statisticsPj: StatisticsPj): Unit = {
    runsAndJewel.items.foreach(_.effect(statisticsPj))
    super.effect(statisticsPj)
  }

  def discard(aRun: Linkable): Unit = runsAndJewel.removeFromInventory(aRun)

  def calculate( value: Int, sum: Int): Int =
    if(runsAndJewel.isNonEmpty) (sum / 2) + value
    else value

  override def getValueOfSell:  Int =  calculate(super.getValueOfSell, runsAndJewel.items.map(_.getValueOfSell).sum)

  override def getValueOfBuy:   Int =  calculate(super.getValueOfBuy, runsAndJewel.items.map(_.getValueOfBuy).sum)
}

trait CombinationRune extends Linkable {

  var combination:List[Combination] = Nil

  override def effect(statisticsPj: StatisticsPj): Unit = {
    val runes = runsAndJewel.items.filter(_.itemType.isRune).map(_.itemType)

    combination.foreach(_.executeCombination(runes,statisticsPj))
    super.effect(statisticsPj)
  }
}

class Combination(var aCombinations:List[ItemType],var aEffect:Equipment){

  def executeCombination(runes:List[ItemType],aStatisticsPj:StatisticsPj):Unit =
    if(aCombinations.forall(runes.contains(_))) aEffect.effect(aStatisticsPj)
}