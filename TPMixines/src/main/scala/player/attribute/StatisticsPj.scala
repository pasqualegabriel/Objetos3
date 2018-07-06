package player.attribute

import item.Item
import player.attribute.Attribute.Attribute
import player.equipment.Equipment

class StatisticsPj(aMaxValue: Integer, aMaxLife: Integer, aLife: Integer, aSkill: Integer, aDefense: Integer, aStrength: Integer) {

  var stats: List[AttributePj] = List(
    new AttributePj(Attribute.MaxValue, aMaxValue), new AttributePj(Attribute.MaxLife, aMaxLife),
    new AttributePj(Attribute.ActualLife, aLife),   new AttributePj(Attribute.Skill, aSkill),
    new AttributePj(Attribute.Defense, aDefense),   new AttributePj(Attribute.Strength, aStrength)
  )

  def increase(anAttribute: Attribute, amount: Int): Unit = {
    val attribute = getAttribute(anAttribute)
    anAttribute match {
      case Attribute.ActualLife => attribute.setBaseValue((attribute.baseValue + amount).min(getAttribute(Attribute.MaxLife).baseValue))
      case _ => attribute.setBaseValue((attribute.baseValue + amount).min(getAttribute(Attribute.MaxValue).baseValue))
    }
  }

  // Return the value of the attribute with the effects of the slots
  def get(anAttribute: Attribute): Int = {
    getAttribute(anAttribute).totalValue()
  }

  def getAttribute(anAttribute: Attribute): AttributePj = stats.find(_.anAttribute.equals(anAttribute)).get

  def applyEffect(anAttribute: Attribute, effectValue: Integer): Unit = {
    val attribute = getAttribute(anAttribute)
    val newEffectValue = (attribute.effectsValue + effectValue).min(getAttribute(Attribute.MaxValue).baseValue - attribute.baseValue)
    attribute.setEffectsValue(newEffectValue)
  }

  def calculateEffects(aSlots: List[Item with Equipment]): Unit = {
    stats.foreach(_.setEffectsValue(0))
    aSlots.foreach(_.effect(this))
  }

}
