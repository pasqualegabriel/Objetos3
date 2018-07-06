package testing

import exceptions._
import item._
import item.itemType.ItemType
import org.junit.Assert._
import org.junit.Test
import player._
import player.attribute.Attribute
import player.equipment._


class MagicTest {

  @Test
  def aPlayerIdentifiesAnMagicRareArmorWithParchmentAndEquipsIt(): Unit = {

    val aPlayer = new Player(20)

    val parchment  = new Item(ItemType.Parchment,10)
    val aMagicArmor = new Item(ItemType.Armor, 10) with DefenseCondition with DefenseEffect with Rare

    aPlayer.grab(aMagicArmor)
    aPlayer.grab(parchment)
    aPlayer.identify(aMagicArmor, parchment)

    assertTrue(aPlayer.getPart(SlotPart.Armor).isEmptySlot)

    aPlayer.equip(SlotPart.Armor, aMagicArmor)

    assertFalse(aPlayer.getPart(SlotPart.Armor).isEmptySlot)
  }


  @Test(expected = classOf[CantBeEquippedExceptionEquipmentNotIdentified])
  def aPlayerCantBeEquippedAnMagicRareArmorBecauseItIsNotIdentified(): Unit = {

    val aPlayer = new Player(20)

    val parchment  = new Item(ItemType.Parchment,10)
    val aMagicArmor = new Item(ItemType.Armor, 10) with DefenseCondition with DefenseEffect with Rare

    aPlayer.grab(aMagicArmor)
    aPlayer.grab(parchment)

    aPlayer.equip(SlotPart.Armor, aMagicArmor)

    fail()
  }

  @Test
  def aMagicRareArmorValueTheDoubleThatTheNormalValueOfSellAndBuy():Unit={

    val aPlayer = new Player(20)

    val parchment   = new Item(ItemType.Parchment,10)
    val aMagicArmor = new Item(ItemType.Armor, 10,valueOfSell = 10,valueOfBuy = 15) with DefenseCondition with DefenseEffect with Rare

    aPlayer.grab(aMagicArmor)
    aPlayer.grab(parchment)

    assertEquals(10,aMagicArmor.getValueOfSell)
    assertEquals(15,aMagicArmor.getValueOfBuy)

    aPlayer.identify(aMagicArmor,parchment)

    assertEquals(20,aMagicArmor.getValueOfSell)
    assertEquals(30,aMagicArmor.getValueOfBuy)
  }


  @Test
  def aMagicRareArmorThatIsNotIdentifiedItsValueOfSellIsTheBaseValueOfSell():Unit={

    val aPlayer = new Player(20)

    val parchment  = new Item(ItemType.Parchment,10)
    val aMagicArmor = new Item(ItemType.Armor, 10 , valueOfSell = 10 , valueOfBuy = 15) with DefenseCondition with DefenseEffect with Rare

    aPlayer.grab(aMagicArmor)
    aPlayer.grab(parchment)

    assertEquals(10,aMagicArmor.getValueOfSell)
    assertEquals(15,aMagicArmor.getValueOfBuy)
  }


  @Test
  def aVeryRareItemsWorthIsTripledWhenItsIdentified():Unit={

    val aPlayer = new Player(20)

    val parchment  = new Item(ItemType.Parchment,10)
    val aMagicArmor = new Item(ItemType.Armor, 10,valueOfSell = 10,valueOfBuy = 15) with DefenseCondition with DefenseEffect with VeryRare

    aPlayer.grab(aMagicArmor)
    aPlayer.grab(parchment)

    assertEquals(10,aMagicArmor.getValueOfSell)
    assertEquals(15,aMagicArmor.getValueOfBuy)

    aPlayer.identify(aMagicArmor,parchment)

    assertEquals(30,aMagicArmor.getValueOfSell)
    assertEquals(45,aMagicArmor.getValueOfBuy)
  }

  @Test
  def aVeryRareMagicItemsWorthDoesNotChangeWhenItsNotIdentified():Unit={
    val aPlayer = new Player(20)

    val parchment  = new Item(ItemType.Parchment,10)
    val aMagicArmor = new Item(ItemType.Armor, 10,valueOfSell = 10, valueOfBuy = 15) with DefenseCondition with DefenseEffect with VeryRare


    aPlayer.grab(aMagicArmor)
    aPlayer.grab(parchment)

    assertEquals(10,aMagicArmor.getValueOfSell)
    assertEquals(15,aMagicArmor.getValueOfBuy)

  }

  @Test
  def anUniqueMagicItemsWorthIsFivefoldWhenItsIdentified():Unit={
    val aPlayer = new Player(20)

    val parchment  = new Item(ItemType.Parchment,10)
    val aMagicArmor = new Item(ItemType.Armor, 10,valueOfSell = 10, valueOfBuy = 15) with DefenseCondition with DefenseEffect with Unique

    aPlayer.grab(aMagicArmor)
    aPlayer.grab(parchment)

    assertEquals(10,aMagicArmor.getValueOfSell)
    assertEquals(15,aMagicArmor.getValueOfBuy)

    aPlayer.identify(aMagicArmor,parchment)

    assertEquals(50,aMagicArmor.getValueOfSell)
    assertEquals(75,aMagicArmor.getValueOfBuy)
  }


  @Test
  def aPlayerEquipsAMagicArmorWithAnExtraEffectThatIncrementsThePlayersStats():Unit={
    val aPlayer     = new Player(50)

    val parchment  = new Item(ItemType.Parchment,10)
    val runeRO     = new Item(ItemType.RuneRo,1) with DefenseEffect with SkillEffect with Linkable
    val runeCA     = new Item(ItemType.RuneCa,1) with ActualLifeEffect with Linkable


    val aMagicArmor = new Item(ItemType.Armor, 10,valueOfSell = 10) with DefenseCondition with DefenseEffect with Linkable with Unique
    aMagicArmor.setCapacityRuneAndJewel(2)
    aPlayer.grab(runeRO)
    aPlayer.grab(runeCA)
    aPlayer.grab(aMagicArmor)
    aPlayer.grab(parchment)


    aMagicArmor.extraEffect = new Object with DefenseEffect with ActualLifeEffect  with SkillEffect
    aPlayer.identify(aMagicArmor,parchment)

    aPlayer.addJewelOrRune(aMagicArmor, runeRO)
    aPlayer.addJewelOrRune(aMagicArmor, runeCA)

    aPlayer.equip(SlotPart.Armor, aMagicArmor)

    assertEquals(4,aPlayer.get(Attribute.Defense))
    assertEquals(3,aPlayer.get(Attribute.Skill))
    assertEquals(3,aPlayer.get(Attribute.ActualLife))
    assertEquals(1,aPlayer.get(Attribute.Strength))

  }


  @Test
  def whenThePlayerDiscardsAMagicArmorThePlayersStatsReturnsToTheBaseStats():Unit={
    val aPlayer     = new Player(50)

    val parchment  = new Item(ItemType.Parchment,10)
    val runeRO     = new Item(ItemType.RuneRo,1) with DefenseEffect with SkillEffect with Linkable
    val runeCA     = new Item(ItemType.RuneCa,1) with ActualLifeEffect with Linkable


    val aMagicArmor = new Item(ItemType.Armor, 10,valueOfSell = 10) with DefenseCondition with DefenseEffect with Linkable with Unique
    aMagicArmor.setCapacityRuneAndJewel(2)
    aPlayer.grab(runeRO)
    aPlayer.grab(runeCA)
    aPlayer.grab(aMagicArmor)
    aPlayer.grab(parchment)


    aMagicArmor.extraEffect = new Object with DefenseEffect with ActualLifeEffect  with SkillEffect
    aPlayer.identify(aMagicArmor,parchment)

    aPlayer.addJewelOrRune(aMagicArmor, runeRO)
    aPlayer.addJewelOrRune(aMagicArmor, runeCA)

    assertEquals(1,aPlayer.get(Attribute.Defense))
    assertEquals(1,aPlayer.get(Attribute.Skill))
    assertEquals(1,aPlayer.get(Attribute.ActualLife))
    assertEquals(1,aPlayer.get(Attribute.Strength))

    aPlayer.equip(SlotPart.Armor, aMagicArmor)

    assertEquals(4,aPlayer.get(Attribute.Defense))
    assertEquals(3,aPlayer.get(Attribute.Skill))
    assertEquals(3,aPlayer.get(Attribute.ActualLife))
    assertEquals(1,aPlayer.get(Attribute.Strength))

    aPlayer.discard(SlotPart.Armor)

    assertEquals(1,aPlayer.get(Attribute.Defense))
    assertEquals(1,aPlayer.get(Attribute.Skill))
    assertEquals(1,aPlayer.get(Attribute.ActualLife))
    assertEquals(1,aPlayer.get(Attribute.Strength))

  }


}
