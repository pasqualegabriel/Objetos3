package player.equipment

import item.Item

import player.attribute.StatisticsPj

trait Magic extends Item with  Equipment{

  var extraEffect: Equipment = new Object with Equipment

  override def effect(aStatisticsPj: StatisticsPj): Unit= {
    extraEffect.effect(aStatisticsPj)
    super.effect(aStatisticsPj)
  }

  identifiable = IdentifiableItem.IdentifiableNegative

  def magicMultiplier: Int

  def identify(): Unit ={
    identifiable = IdentifiableItem.IdentifiablePositive
  }

  override def getValueOfSell: Int =calculate(super.getValueOfSell)

  override def getValueOfBuy: Int = calculate(super.getValueOfBuy)

  def calculate(aValue :Int) : Int=
    if(identifiable.isIdentified)  this.magicMultiplier * aValue
    else aValue

}

trait Rare extends Magic{

  def magicMultiplier: Int =  2
}

trait VeryRare extends Magic{

  def magicMultiplier: Int =  3
}

trait Unique extends Magic {

  def magicMultiplier: Int = 5
}