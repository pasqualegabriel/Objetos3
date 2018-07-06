package player.attribute

import player.attribute.Attribute.Attribute

class AttributePj(val anAttribute: Attribute, var baseValue: Integer, var effectsValue: Integer = 0) {

  def totalValue(): Integer = baseValue + effectsValue

  def setBaseValue(value: Integer): Unit = baseValue = value

  def setEffectsValue(value: Integer): Unit = effectsValue = value

}
