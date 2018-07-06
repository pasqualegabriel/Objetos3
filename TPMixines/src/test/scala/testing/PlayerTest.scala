package testing

import exceptions._
import item._
import item.itemType.{ItemType, Potion}
import item.uses.LimitedUses
import itemEffects._
import org.junit.Assert._
import org.junit.Test
import player._
import player.attribute.Attribute

class PlayerTest {

  @Test
  def aPlayerGrabsAnItem(): Unit = {

    val player = new Player(5)
    val item = new Item(ItemType.Armor, 2)

    assertTrue(player.inventory.items.isEmpty)

    player.grab(item)

    assertEquals(1, player.inventory.items.size)
    assertTrue(player.inventory.items.contains(item))
  }

  @Test(expected = classOf[InsufficientCapacityException])
  def aPlayerCantGrabAnItem(): Unit = {

    val player = new Player(5)
    val anItem    = new Item(ItemType.Armor, 18)
    val otherItem = new Item(ItemType.Key, 3)

    player.grab(anItem)
    player.grab(otherItem)

    fail()
  }

  @Test
  def aPlayerGrabsAPotion(): Unit = {

    val player = new Player(5)
    val potion = new Potion(2)

    assertTrue(player.inventory.items.isEmpty)

    player.grab(potion)

    assertEquals(1, player.inventory.items.size)
    assertTrue(player.inventory.items.contains(potion))
  }

  @Test
  def aPlayerThrowsOutAnItem(): Unit = {

    val player = new Player(20)
    val anItem    = new Item(ItemType.Armor, 10)
    val otherItem = new Item(ItemType.Key, 6)

    player.grab(anItem)
    player.grab(otherItem)

    assertEquals(4, player.inventory.freeCapacity())
    assertEquals(2, player.inventory.items.size)
    assertTrue(player.inventory.items.contains(otherItem))

    player.throwOut(otherItem)

    assertEquals(10, player.inventory.freeCapacity())
    assertEquals(1 , player.inventory.items.size)
    assertFalse(player.inventory.items.contains(otherItem))
  }

  @Test
  def aPlayerUsesAnArrowThatIncreasesOneActualLifeAndOnlyItHasOneUse(): Unit = {

    val player = new Player(20)
    val item   = new Item(ItemType.Arrow, 10) with LimitedUses with ActualLifeIncrease
    player.grab(item)

    assertEquals(1, player.get(Attribute.ActualLife))
    assertTrue(player.inventory.items.contains(item))

    player.use(item)

    assertEquals(2, player.get(Attribute.ActualLife))
    assertFalse(player.inventory.items.contains(item))
  }

  @Test
  def aPlayerUsesAShieldWithTwoUsesAndThatIncreasesOneTheDefenceTwice(): Unit = {

    val player = new Player(200)
    val shield = new Item(ItemType.Shield, 10,2) with LimitedUses with DefenseIncrease

    player.grab(shield)
    player.use(shield)

    assertEquals(2, player.get(Attribute.Defense))
    assertTrue(player.inventory.items.contains(shield))

    player.use(shield)

    assertEquals(3, player.get(Attribute.Defense))
    assertFalse(player.inventory.items.contains(shield))
  }

  @Test
  def aPlayerUsesAnArrowThatIncreasesTwoTheActualLifeTwoTheDefenseAndOnlyItHasOneUse(): Unit = {

    val player = new Player(20)
    val item   = new Item(ItemType.Arrow, 10) with LimitedUses with ActualLifeIncrease with DefenseIncrease
    item.value = 2

    player.grab(item)

    assertEquals(1, player.get(Attribute.ActualLife))
    assertEquals(1, player.get(Attribute.Defense))
    assertTrue(player.inventory.items.contains(item))

    player.use(item)

    assertEquals(3, player.get(Attribute.ActualLife))
    assertEquals(3, player.get(Attribute.Defense))
    assertFalse(player.inventory.items.contains(item))
  }

  @Test
  def aPotionInInventoryIsMovedToTheBelt(): Unit = {

    val player = new Player(20, beltCapacity = 10)
    val potion = new Potion(10)
    player.grab(potion)

    assertTrue( player.inventory.items.contains(potion))
    assertFalse(player.belt.items.contains(potion))
    assertEquals(10, player.inventory.freeCapacity())
    assertEquals(10, player.belt.freeCapacity())

    player.moveToBelt(potion)

    assertTrue( player.belt.items.contains(potion))
    assertFalse(player.inventory.items.contains(potion))
    assertEquals(20, player.inventory.freeCapacity())
    assertEquals(0, player.belt.freeCapacity())
  }

  @Test(expected = classOf[InsufficientCapacityException])
  def aPotionCantBeMovedToTheBeltBecauseItDoesNotHaveEnoughCapacity(): Unit = {

    val player = new Player(20, beltCapacity = 9)
    val potion = new Potion(10)
    player.grab(potion)

    player.moveToBelt(potion)

    fail()
  }

  @Test
  def aPlayerThrowsOutAPotionFromTheBelt(): Unit = {

    val player = new Player(20, beltCapacity = 10)
    val potion = new Potion(10)
    player.grab(potion)
    player.moveToBelt(potion)
    player.throwOutThePotionFromTheBelt(potion)

    assertFalse(player.belt.items.contains(potion))
  }

  @Test
  def aPlayerMovesAPotionFromTheInventoryToTheBelt(): Unit = {

    val player = new Player(20, beltCapacity = 20)
    val potion = new Potion(10)
    player.grab(potion)
    player.moveToBelt(potion)

    assertTrue( player.belt.items.contains(potion))
    assertFalse(player.inventory.items.contains(potion))
    assertEquals(20, player.inventory.freeCapacity())
    assertEquals(10, player.belt.freeCapacity())

    player.moveToInventory(potion)

    assertTrue( player.inventory.items.contains(potion))
    assertFalse(player.belt.items.contains(potion))
    assertEquals(10, player.inventory.freeCapacity())
    assertEquals(20, player.belt.freeCapacity())
  }

  @Test
  def aPlayerUsesAPotionThatIsInTheBeltIncreasesActualLifeToTwo(): Unit = {

    val player = new Player(20)
    val potion = new Potion(10)
    player.grab(potion)
    player.moveToBelt(potion)

    assertEquals(1, player.get(Attribute.Defense))
    assertTrue(player.belt.items.contains(potion))

    player.use(potion)

    assertEquals(2, player.get(Attribute.Defense))
    assertFalse(player.belt.items.contains(potion))
  }

}
