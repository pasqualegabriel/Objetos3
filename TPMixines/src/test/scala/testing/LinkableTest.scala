package testing

import exceptions.InsufficientCapacityException
import item._
import item.itemType.ItemType
import org.junit.Assert._
import org.junit.Test
import player._
import player.attribute.Attribute
import player.equipment._

class LinkableTest {

  @Test
  def aPlayerAddsARuneAndJewelInTheSlotArmorWithSlotCapacityOfTwo(): Unit = {

    val aPlayer = new Player(20)

    val rune  = new Item(ItemType.RuneCa,1) with DefenseEffect with SkillEffect with Linkable
    val jewel = new Item(ItemType.Jewel,1)  with ActualLifeEffect with Linkable

    val armor = new Item(ItemType.Armor,10) with DefenseCondition with DefenseEffect with StrengthEffect with Linkable
    armor.setCapacityRuneAndJewel(2)
    aPlayer.grab(armor)
    aPlayer.grab(rune)
    aPlayer.grab(jewel)

    assertTrue(armor.runsAndJewel.items.isEmpty)
    assertTrue(aPlayer.getPart(SlotPart.Armor).isEmptySlot)

    aPlayer.equip(SlotPart.Armor, armor)

    aPlayer.addJewelOrRune(armor, rune)
    aPlayer.addJewelOrRune(armor, jewel)

    assertFalse(armor.runsAndJewel.items.isEmpty)
    assertEquals(2,armor.runsAndJewel.items.size)
    assertFalse(aPlayer.getPart(SlotPart.Armor).isEmptySlot)
  }

  @Test(expected = classOf[InsufficientCapacityException])
  def aRuneCaCantBeEquipped(): Unit = {

    val aPlayer = new Player()

    val runeRO = new Item(ItemType.RuneRo,1,  valueOfSell=5,  valueOfBuy = 10) with StrengthEffect   with Linkable
    val runeCA = new Item(ItemType.RuneCa,1,  valueOfSell=5,  valueOfBuy = 10) with ActualLifeEffect with Linkable
    val armor  = new Item(ItemType.RuneCa,10, valueOfSell=10, valueOfBuy = 20) with SkillCondition   with StrengthEffect with Linkable

    aPlayer.grab(armor)
    aPlayer.grab(runeRO)
    aPlayer.grab(runeCA)

    aPlayer.addJewelOrRune(armor, runeRO)
    aPlayer.addJewelOrRune(armor, runeCA)

    fail()
  }

  @Test
  def aPlayerAddsARuneInTheSlotArmorThatIncreaseHisDefense(): Unit = {

    val aPlayer = new Player(20)

    val runeRO = new Item(ItemType.RuneCa,1) with DefenseEffect with SkillEffect with Linkable
    val runeCA = new Item(ItemType.RuneCa,1) with ActualLifeEffect with Linkable

    val armor = new Item(ItemType.Armor,10) with DefenseCondition with DefenseEffect with StrengthEffect with Linkable
    armor.setCapacityRuneAndJewel(2)

    aPlayer.grab(armor)
    aPlayer.grab(runeRO)
    aPlayer.grab(runeCA)
    aPlayer.addJewelOrRune(armor, runeCA)
    aPlayer.addJewelOrRune(armor, runeRO)

    assertEquals(2 , armor.runsAndJewel.items.size)

    assertTrue(aPlayer.getPart(SlotPart.Armor).isEmptySlot)
    assertEquals(1, aPlayer.get(Attribute.Defense))

    aPlayer.equip(SlotPart.Armor, armor)

    assertFalse(aPlayer.getPart(SlotPart.Armor).isEmptySlot)
    assertEquals(3, aPlayer.get(Attribute.Defense))
    assertEquals(2, aPlayer.get(Attribute.Strength))
    assertEquals(2, aPlayer.get(Attribute.Skill))
    assertEquals(2, aPlayer.get(Attribute.ActualLife))
  }

  @Test
  def aPlayerDiscardsARuneOfSlotArmorAndHisDefenseAndSkillLoseTheEffects(): Unit = {

    val aPlayer = new Player(20)

    val runeRO = new Item(ItemType.RuneRo,1) with DefenseEffect with SkillEffect with Linkable
    val runeCA = new Item(ItemType.RuneCa,1) with ActualLifeEffect with Linkable

    val armor = new Item(ItemType.Armor,10) with DefenseCondition with StrengthEffect   with Linkable
    armor.setCapacityRuneAndJewel(2)

    aPlayer.grab(armor)
    aPlayer.grab(runeRO)
    aPlayer.grab(runeCA)
    aPlayer.equip(SlotPart.Armor, armor)
    aPlayer.addJewelOrRune(armor, runeRO)
    aPlayer.addJewelOrRune(armor, runeCA)

    assertEquals(2, aPlayer.get(Attribute.Defense))
    assertEquals(2, aPlayer.get(Attribute.Strength))
    assertEquals(2, aPlayer.get(Attribute.Skill))
    assertEquals(2, aPlayer.get(Attribute.ActualLife))

    aPlayer.discardJewelOrRune(armor,runeRO)

    assertEquals(1, aPlayer.get(Attribute.Defense))
    assertEquals(1, aPlayer.get(Attribute.Skill))
    assertEquals(2, aPlayer.get(Attribute.Strength))
    assertEquals(2, aPlayer.get(Attribute.ActualLife))
  }

  @Test
  def aPlayerEquipsAThreeRunesCombinationInTheSlotArmorWithOneExtraEffectPositivesEach(): Unit = {

    val aPlayer = new Player()

    val oneEffect: Effect   = new Object    with DefenseEffect
    val twoEffect: Effect   = new Object    with SkillEffect
    val threeEffect:Effect  = new Object    with StrengthEffect

    val aCombination:List[Combination]= List(new Combination(List(ItemType.RuneCa,ItemType.RuneRo),oneEffect)
      ,new Combination(List(ItemType.RuneMo,ItemType.RuneCo),twoEffect)
      ,new Combination(List(ItemType.RunePo,ItemType.RuneLo),threeEffect))


    val runeRO = new Item(ItemType.RuneRo,1) with StrengthEffect   with Linkable
    val runeCA = new Item(ItemType.RuneCa,1) with ActualLifeEffect with Linkable

    val runeMo = new Item(ItemType.RuneMo,1) with SkillEffect      with Linkable
    val runeCo = new Item(ItemType.RuneCo,1) with ActualLifeEffect with Linkable

    val runePo = new Item(ItemType.RunePo,1) with DefenseEffect    with Linkable
    val runeLo = new Item(ItemType.RuneLo,1) with ActualLifeEffect with Linkable

    val armor = new Item(ItemType.RuneCa,10) with SkillCondition with StrengthEffect with Linkable with CombinationRune
    armor.combination = aCombination
    armor.setCapacityRuneAndJewel(6)

    aPlayer.grab(armor)
    aPlayer.grab(runeRO)
    aPlayer.grab(runeCA)
    aPlayer.grab(runeMo)
    aPlayer.grab(runeCo)
    aPlayer.grab(runePo)
    aPlayer.grab(runeLo)

    aPlayer.equip(SlotPart.Armor, armor)
    aPlayer.addJewelOrRune(armor, runeRO)
    aPlayer.addJewelOrRune(armor, runeCA)

    aPlayer.addJewelOrRune(armor, runeMo)
    aPlayer.addJewelOrRune(armor, runeCo)

    aPlayer.addJewelOrRune(armor, runePo)
    aPlayer.addJewelOrRune(armor, runeLo)


    assertEquals(3, aPlayer.get(Attribute.Defense))
    assertEquals(4, aPlayer.get(Attribute.Strength))
    assertEquals(3, aPlayer.get(Attribute.Skill))
    assertEquals(4, aPlayer.get(Attribute.ActualLife))
  }

  @Test
  def aPlayerEquipsAThreeRunesCombinationDiscardsOneLosingTheEffects(): Unit = {

    val aPlayer = new Player

    val oneEffect:Effect    = new Object with DefenseEffect
    val twoEffect:Effect    = new Object with SkillEffect
    val threeEffect:Effect  = new Object with StrengthEffect

    val aCombination:List[Combination]= List(new Combination(List(ItemType.RuneCa,ItemType.RuneRo),oneEffect)
      ,new Combination(List(ItemType.RuneMo,ItemType.RuneCo),twoEffect)
      ,new Combination(List(ItemType.RunePo,ItemType.RuneLo),threeEffect))

    val runeRO = new Item(ItemType.RuneRo,1) with StrengthEffect   with Linkable
    val runeCA = new Item(ItemType.RuneCa,1) with ActualLifeEffect with Linkable

    val runeMo = new Item(ItemType.RuneMo,1) with SkillEffect      with Linkable
    val runeCo = new Item(ItemType.RuneCo,1) with ActualLifeEffect with Linkable

    val runePo = new Item(ItemType.RunePo,1) with DefenseEffect    with Linkable
    val runeLo = new Item(ItemType.RuneLo,1) with ActualLifeEffect with Linkable

    val armor = new Item(ItemType.RuneCa,10) with SkillCondition with StrengthEffect with Linkable with CombinationRune
    armor.combination = aCombination
    armor.setCapacityRuneAndJewel(6)

    aPlayer.grab(armor)
    aPlayer.grab(runeRO)
    aPlayer.grab(runeCA)
    aPlayer.grab(runeMo)
    aPlayer.grab(runeCo)
    aPlayer.grab(runePo)
    aPlayer.grab(runeLo)

    aPlayer.equip(SlotPart.Armor, armor)
    aPlayer.addJewelOrRune(armor, runeRO)
    aPlayer.addJewelOrRune(armor, runeCA)

    aPlayer.addJewelOrRune(armor, runeMo)
    aPlayer.addJewelOrRune(armor, runeCo)

    aPlayer.addJewelOrRune(armor, runePo)
    aPlayer.addJewelOrRune(armor, runeLo)

    assertEquals(3, aPlayer.get(Attribute.Defense))
    assertEquals(4, aPlayer.get(Attribute.Strength))
    assertEquals(3, aPlayer.get(Attribute.Skill))
    assertEquals(4, aPlayer.get(Attribute.ActualLife))

    aPlayer.discardJewelOrRune(armor, runeMo)
    aPlayer.discardJewelOrRune(armor, runeCo)

    aPlayer.discardJewelOrRune(armor, runePo)


    assertEquals(2, aPlayer.get(Attribute.Defense))
    assertEquals(3, aPlayer.get(Attribute.Strength))
    assertEquals(1, aPlayer.get(Attribute.Skill))
    assertEquals(3, aPlayer.get(Attribute.ActualLife))
  }

  @Test
  def valueOfBuyAndValueOfSellOfAnArmorWithTwoRunes(): Unit = {

    val aPlayer = new Player()

    val runeRO = new Item(ItemType.RuneRo,1,valueOfSell=5, valueOfBuy = 10) with StrengthEffect   with Linkable
    val runeCA = new Item(ItemType.RuneCa,1,valueOfSell=5, valueOfBuy = 10) with ActualLifeEffect with Linkable
    val armor  = new Item(ItemType.RuneCa,10, valueOfSell=10, valueOfBuy = 20) with SkillCondition with StrengthEffect with Linkable
    armor.setCapacityRuneAndJewel(2)

    aPlayer.grab(armor)
    aPlayer.grab(runeRO)
    aPlayer.grab(runeCA)

    aPlayer.addJewelOrRune(armor, runeRO)
    aPlayer.addJewelOrRune(armor, runeCA)
    aPlayer.equip(SlotPart.Armor, armor)

    assertEquals(15, armor.getValueOfSell)
    assertEquals(30, armor.getValueOfBuy)
  }

}
