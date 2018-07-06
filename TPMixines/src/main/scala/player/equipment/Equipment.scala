package player.equipment

import IdentifiableItem.IdentifiableItem
import SlotPart.SlotPart

import player._
import player.attribute.{Attribute, StatisticsPj}

trait Equipment {

  var conditionValue = 1

  var identifiable : IdentifiableItem = IdentifiableItem.IdentifiablePositive

  var effectValue = 1

  def isEmptySlot = false

  def comply(aPlayer: Player, aSlot: SlotPart): Boolean = true

  def effect(statisticsPj: StatisticsPj): Unit = {}

  def isIdentified: Boolean = identifiable.isIdentified

}

/// Conditions ///////////////////////////////////////


trait SkillCondition extends Equipment {

  override def comply(aPlayer: Player, aSlot: SlotPart): Boolean =
    aPlayer.get(Attribute.Skill) >= conditionValue && super.comply(aPlayer, aSlot)
}

trait StrengthCondition extends Equipment {

  override def comply(aPlayer: Player, aSlot: SlotPart): Boolean =
    aPlayer.get(Attribute.Strength) >= conditionValue && super.comply(aPlayer, aSlot)
}

trait ActualLifeCondition extends Equipment {

  override def comply(aPlayer: Player, aSlot: SlotPart): Boolean =
    aPlayer.get(Attribute.ActualLife) >= conditionValue && super.comply(aPlayer, aSlot)
}

trait DefenseCondition extends Equipment {

  override def comply(aPlayer: Player, aSlot: SlotPart): Boolean =
    aPlayer.get(Attribute.Defense) >= conditionValue && super.comply(aPlayer, aSlot)
}

/// Equipments ///////////////////////////////////////

trait Effect extends Equipment

trait SkillEffect extends Effect {

  override def effect(statisticsPj: StatisticsPj): Unit = {
    statisticsPj.applyEffect(Attribute.Skill, effectValue)
    super.effect(statisticsPj)
  }
}

trait StrengthEffect extends Effect {

  override def effect(statisticsPj: StatisticsPj): Unit = {
    statisticsPj.applyEffect(Attribute.Strength, effectValue)
    super.effect(statisticsPj)
  }

}

trait ActualLifeEffect extends Effect {

  override def effect(statisticsPj: StatisticsPj): Unit = {
    statisticsPj.applyEffect(Attribute.ActualLife, effectValue)
    super.effect(statisticsPj)
  }

}

trait DefenseEffect extends Effect {
  override def effect(statisticsPj: StatisticsPj): Unit = {
    statisticsPj.applyEffect(Attribute.Defense, effectValue)
    super.effect(statisticsPj)
  }


}




















