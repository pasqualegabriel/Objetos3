package testing

import exceptions._
import item._
import item.itemType.ItemType
import org.junit.Assert._
import org.junit.Test
import player._
import player.attribute.Attribute
import player.equipment._

class SlotTest {

  @Test
  def aPlayerAddsAnEquipmentThatSatisfiesTheRequirements(): Unit = {

    val aPlayer = new Player(20)
    val armor = new Item(ItemType.Armor,20) with SkillCondition
    aPlayer.grab(armor)

    assertTrue(aPlayer.getPart(SlotPart.Armor).isEmptySlot)

    aPlayer.equip(SlotPart.Armor, armor)

    assertFalse(aPlayer.getPart(SlotPart.Armor).isEmptySlot)
  }

  @Test(expected = classOf[CantBeEquippedExceptionPlayerDoesNotHaveTheRequirmentsForTheEquipment])
  def aPlayerAddsAnEquipmentThatDoesNotSatisfyTheRequirements(): Unit = {

    val aPlayer = new Player(20)

    val armor = new Item(ItemType.Armor,20) with DefenseCondition {
      conditionValue = 2
    }
    aPlayer.grab(armor)

    aPlayer.equip(SlotPart.Armor, armor)

    fail()
  }

  @Test
  def aPlayerEquipsAnItemThatSatisfiesTheRequirementsAndIncreasesOneTheDefense(): Unit = {

    val aPlayer = new Player(20)

    val armor = new Item(ItemType.Armor,20) with DefenseCondition with DefenseEffect
    aPlayer.grab(armor)

    assertTrue(aPlayer.getPart(SlotPart.Armor).isEmptySlot)
    assertEquals(1, aPlayer.get(Attribute.Defense))

    aPlayer.equip(SlotPart.Armor, armor)

    assertFalse(aPlayer.getPart(SlotPart.Armor).isEmptySlot)
    assertEquals(2, aPlayer.get(Attribute.Defense))
  }

  @Test
  def aPlayerEquipsAnItemThatSatisfiesTheRequirementsAndIncreasesOneTheSkillTheDefenseAndTheActualLife(): Unit = {

    val aPlayer = new Player(20)

    val armor = new Item(ItemType.Armor,20) with DefenseCondition with SkillEffect with DefenseEffect with ActualLifeEffect
    aPlayer.grab(armor)

    assertEquals(1, aPlayer.get(Attribute.Defense))
    assertEquals(1, aPlayer.get(Attribute.Skill))
    assertEquals(1, aPlayer.get(Attribute.ActualLife))

    aPlayer.equip(SlotPart.Armor, armor)

    assertEquals(2, aPlayer.get(Attribute.Defense))
    assertEquals(2, aPlayer.get(Attribute.Skill))
    assertEquals(2, aPlayer.get(Attribute.ActualLife))
  }

  @Test
  def aPlayerEquipsAnItemThatIncreasesFifteenTheSkillButTheMaxValueIsTen(): Unit = {

    val aPlayer = new Player(20)

    val armor = new Item(ItemType.Armor,20) with DefenseCondition with SkillEffect
    armor.effectValue = 15

    aPlayer.grab(armor)

    assertEquals(1, aPlayer.get(Attribute.Skill))

    aPlayer.equip(SlotPart.Armor, armor)

    assertEquals(10, aPlayer.get(Attribute.Skill))
  }

  @Test
  def aPlayerEquipsAnItemThatIncreasesFourteenTheActualLifeButTheMaxLifeIsTen(): Unit = {

    val aPlayer = new Player(20)

    val armor = new Item(ItemType.Armor,20) with ActualLifeCondition with ActualLifeEffect
    armor.effectValue = 14

    aPlayer.grab(armor)

    assertEquals(1, aPlayer.get(Attribute.ActualLife))

    aPlayer.equip(SlotPart.Armor, armor)

    assertEquals(10, aPlayer.get(Attribute.ActualLife))
  }

  @Test
  def aPlayerDiscardsAnEquipmentAndHisStatsAreRecalculated(): Unit = {

    val aPlayer = new Player(20)

    val armor = new Item(ItemType.Armor,20) with ActualLifeCondition with DefenseEffect
    aPlayer.grab(armor)

    aPlayer.equip(SlotPart.Armor, armor)

    assertFalse(aPlayer.getPart(SlotPart.Armor).isEmptySlot)
    assertFalse(aPlayer.inventory.items.contains(armor))
    assertEquals(2, aPlayer.get(Attribute.Defense))

    aPlayer.discard(SlotPart.Armor)

    assertTrue(aPlayer.getPart(SlotPart.Armor).isEmptySlot)
    assertTrue(aPlayer.inventory.items.contains(armor))
    assertEquals(1, aPlayer.get(Attribute.Defense))
  }

  @Test(expected = classOf[InsufficientCapacityException])
  def aPlayerDiscardsAnEquipmentButCantBeEnoughCapacityInTheInventory(): Unit = {

    val aPlayer = new Player(20)

    val armor = new Item(ItemType.Armor,20)  with ActualLifeCondition with DefenseEffect
    val shield = new Item(ItemType.Shield,1) with ActualLifeCondition with DefenseEffect
    aPlayer.grab(armor)

    aPlayer.equip(SlotPart.Armor, armor)

    aPlayer.grab(shield)
    aPlayer.discard(SlotPart.Armor)

    fail()
  }

  @Test
  def aPlayerThrowsOutAnEquipmentAndHisStatsAreRecalculated(): Unit = {

    val aPlayer = new Player(20)

    val armor = new Item(ItemType.Armor,20) with DefenseCondition with DefenseEffect
    aPlayer.grab(armor)
    aPlayer.equip(SlotPart.Armor, armor)

    assertFalse(aPlayer.getPart(SlotPart.Armor).isEmptySlot)
    assertFalse(aPlayer.inventory.items.contains(armor))
    assertEquals(2, aPlayer.get(Attribute.Defense))

    aPlayer.throwOutEquipment(SlotPart.Armor)

    assertTrue(aPlayer.getPart(SlotPart.Armor).isEmptySlot)
    assertFalse(aPlayer.inventory.items.contains(armor))
    assertEquals(1, aPlayer.get(Attribute.Defense))
  }

}
