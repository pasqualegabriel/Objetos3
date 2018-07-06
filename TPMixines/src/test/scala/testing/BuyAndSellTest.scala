package testing

import exceptions.InsufficientGoldException
import item._
import item.itemType.ItemType
import item.uses.Consumable
import org.junit.Assert._
import org.junit.Test
import player._
import player.buy_sell.{Merchantable, Seller}
import player.equipment._

class BuyAndSellTest {

  @Test
  def aPlayerBuysAnItem(): Unit = {

    val player = new Player(5)
    val seller = new Seller
    val item   = new Item(ItemType.Potion, 2) with Merchantable
    seller.addItem(item)

    assertEquals(10,item.getValueOfBuy)

    assertEquals(20, player.gold)
    assertTrue( seller.items.contains(item))
    assertFalse(player.inventory.items.contains(item))

    player.buy(item, seller)

    assertEquals(10, player.gold)
    assertFalse(seller.items.contains(item))
    assertTrue( player.inventory.items.contains(item))
  }

  @Test(expected = classOf[InsufficientGoldException])
  def aPlayerDoesNotHaveEnoughGoldToBuyAnItem(): Unit = {

    val player = new Player(5)
    val seller = new Seller
    val item   = new Item(ItemType.Potion, 2, valueOfBuy = 21, valueOfSell = 15) with Merchantable
    seller.addItem(item)

    player.buy(item, seller)

    fail()
  }

  @Test
  def aPlayerSellsAnItem(): Unit = {

    val player = new Player(5)
    val seller = new Seller
    val item   = new Item(ItemType.Potion, 2, valueOfBuy = 10, valueOfSell = 15) with Merchantable
    seller.addItem(item)
    player.buy(item, seller)

    assertEquals(10, player.gold)
    assertFalse(seller.items.contains(item))
    assertTrue( player.inventory.items.contains(item))

    player.sell(item, seller)

    assertEquals(25, player.gold)
    assertTrue( seller.items.contains(item))
    assertFalse(player.inventory.items.contains(item))
  }

  @Test
  def aPlayerBuysAnLinkableItem(): Unit = {

    val aPlayer = new Player(50, gold = 30)
    val seller  = new Seller
    val rune    = new Item(ItemType.RuneCa, 1 , valueOfBuy = 15)   with DefenseEffect with SkillEffect with Linkable
    val jewel   = new Item(ItemType.Jewel,  1 , valueOfBuy = 15)   with ActualLifeEffect with Linkable

    val item    = new Item(ItemType.Armor, 10 , valueOfBuy = 15) with Merchantable with DefenseCondition with DefenseEffect with StrengthEffect with Linkable
    item.setCapacityRuneAndJewel(2)

    aPlayer.addJewelOrRune(item, rune)
    aPlayer.addJewelOrRune(item, jewel)

    seller.addItem(item)

    assertEquals(30,  aPlayer.gold)

    aPlayer.buy(item, seller)

    assertEquals(0, aPlayer.gold)
  }

  @Test
  def aPlayerSellsAnLinkableItem(): Unit = {

    val aPlayer = new Player(50, gold = 0)
    val seller  = new Seller
    val rune    = new Item(ItemType.RuneCa, 1 , valueOfSell = 10)   with DefenseEffect with SkillEffect with Linkable
    val jewel   = new Item(ItemType.Jewel,  1 , valueOfSell = 10)   with ActualLifeEffect with Linkable

    val item    = new Item(ItemType.Armor, 10, valueOfSell = 10) with Merchantable with DefenseCondition with DefenseEffect with StrengthEffect with Linkable
    item.setCapacityRuneAndJewel(2)
    aPlayer.grab(item)
    aPlayer.grab(rune)
    aPlayer.grab(jewel)
    aPlayer.addJewelOrRune(item, rune)
    aPlayer.addJewelOrRune(item, jewel)

    assertEquals(0,  aPlayer.gold)

    aPlayer.sell(item, seller)

    assertEquals(20, aPlayer.gold)
  }

  @Test
  def aPlayerBuysAnMagicUniqueItem(): Unit = {

    val aPlayer = new Player(50, gold = 100)
    val seller  = new Seller

    val parchment  = new Item(ItemType.Parchment,10)
    val rune    = new Item(ItemType.RuneCa, 1 , 10,valueOfBuy = 10)   with DefenseEffect with SkillEffect with Linkable
    val jewel   = new Item(ItemType.Jewel,  1 , 10,valueOfBuy = 10)   with ActualLifeEffect with Linkable

    val item    = new Item(ItemType.Armor, 10 , 10,valueOfBuy = 10) with Merchantable  with Linkable with Unique
    item.setCapacityRuneAndJewel(2)

    item.insert(rune)
    item.insert(jewel)

    seller.addItem(item)

    assertEquals(100,  aPlayer.gold)
    aPlayer.identify(item,parchment)

    aPlayer.buy(item, seller)

    assertEquals(0, aPlayer.gold)
  }

  @Test
  def aPlayerSellsAnMagicUniqueItem(): Unit = {

    val aPlayer = new Player(50, gold = 0)
    val seller  = new Seller

    val parchment  = new Item(ItemType.Parchment,10)
    val rune    = new Item(ItemType.RuneCa, 1 , 10,valueOfSell = 15)   with DefenseEffect with SkillEffect with Linkable
    val jewel   = new Item(ItemType.Jewel,  1 , 10,valueOfSell = 15)   with ActualLifeEffect with Linkable

    val item    = new Item(ItemType.Armor, 10 , 10,valueOfSell = 15) with Merchantable  with Linkable with Unique
    item.setCapacityRuneAndJewel(2)


    aPlayer.grab(item)
    aPlayer.addJewelOrRune(item,rune)
    aPlayer.addJewelOrRune(item,jewel)
    aPlayer.identify(item,parchment)

    assertEquals(0,  aPlayer.gold)

    aPlayer.sell(item, seller)

    assertEquals(150, aPlayer.gold)
  }

  @Test
  def aPlayerBuysAnMagicVeryRareItem(): Unit = {

    val aPlayer = new Player(50, gold = 60)
    val seller  = new Seller

    val parchment  = new Item(ItemType.Parchment,10)
    val rune    = new Item(ItemType.RuneCa, 1 , 10,valueOfBuy = 10)   with DefenseEffect with SkillEffect with Linkable
    val jewel   = new Item(ItemType.Jewel,  1 , 10,valueOfBuy = 10)   with ActualLifeEffect with Linkable

    val item    = new Item(ItemType.Armor, 10 , 10,valueOfBuy = 10) with Merchantable  with Linkable with VeryRare
    item.setCapacityRuneAndJewel(2)

    item.insert(rune)
    item.insert(jewel)

    seller.addItem(item)

    assertEquals(60,  aPlayer.gold)
    aPlayer.identify(item,parchment)

    aPlayer.buy(item, seller)

    assertEquals(0, aPlayer.gold)
  }

  @Test
  def aPlayerSellsAnMagicVeryRareItem(): Unit = {

    val aPlayer = new Player(50, gold = 0)
    val seller  = new Seller

    val parchment  = new Item(ItemType.Parchment,10)
    val rune    = new Item(ItemType.RuneCa, 1 , 10,valueOfSell = 10)   with DefenseEffect with SkillEffect with Linkable
    val jewel   = new Item(ItemType.Jewel,  1 , 10,valueOfSell = 10)   with ActualLifeEffect with Linkable

    val item    = new Item(ItemType.Armor, 10 , 10,valueOfSell = 10) with Merchantable  with Linkable with VeryRare
    item.setCapacityRuneAndJewel(2)


    aPlayer.grab(item)
    aPlayer.addJewelOrRune(item,rune)
    aPlayer.addJewelOrRune(item,jewel)
    aPlayer.identify(item,parchment)

    assertEquals(0,  aPlayer.gold)

    aPlayer.sell(item, seller)

    assertEquals(60, aPlayer.gold)
  }

  @Test
  def aPlayerBuysAnMagicRareItem(): Unit = {

    val aPlayer = new Player(50, gold = 40)
    val seller  = new Seller

    val parchment  = new Item(ItemType.Parchment,10)
    val rune    = new Item(ItemType.RuneCa, 1 , 10,valueOfBuy = 10)   with DefenseEffect with SkillEffect with Linkable
    val jewel   = new Item(ItemType.Jewel,  1 , 10,valueOfBuy = 10)   with ActualLifeEffect with Linkable

    val item    = new Item(ItemType.Armor, 10 , 10,valueOfBuy = 10) with Merchantable  with Linkable with Rare
    item.setCapacityRuneAndJewel(2)

    item.insert(rune)
    item.insert(jewel)

    seller.addItem(item)

    assertEquals(40,  aPlayer.gold)
    aPlayer.identify(item,parchment)

    aPlayer.buy(item, seller)

    assertEquals(0, aPlayer.gold)
  }

  @Test
  def aPlayerSellsAnMagicRareItem(): Unit = {

    val aPlayer = new Player(50, gold = 0)
    val seller  = new Seller

    val parchment  = new Item(ItemType.Parchment,10)
    val rune    = new Item(ItemType.RuneCa, 1 , 10,valueOfSell = 15)   with DefenseEffect with SkillEffect with Linkable
    val jewel   = new Item(ItemType.Jewel,  1 , 10,valueOfSell = 15)   with ActualLifeEffect with Linkable

    val item    = new Item(ItemType.Armor, 10 , 10,valueOfSell = 15) with Merchantable  with Linkable with Rare
    item.setCapacityRuneAndJewel(2)


    aPlayer.grab(item)
    aPlayer.addJewelOrRune(item,rune)
    aPlayer.addJewelOrRune(item,jewel)
    aPlayer.identify(item,parchment)

    assertEquals(0,  aPlayer.gold)

    aPlayer.sell(item, seller)

    assertEquals(60, aPlayer.gold)
  }


}
