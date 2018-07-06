package testing

import item._
import item.itemType.{ItemType, Potion}
import item.uses.{LimitedUses, UnlimitedUses}
import itemEffects.DefenseIncrease
import org.junit.Assert._
import org.junit.Test

class ItemTest {

  @Test
  def aConsumableItemHasThreeUses(): Unit = {
    val item = new Item(ItemType.Shield, 20, 3) with LimitedUses with DefenseIncrease

    assertEquals(3, item.uses)
  }

  @Test
  def aPotionWhenItIsUsedItHasNoUsesLeft(): Unit = {
    val potion = new Potion(20)

    assertEquals(1, potion.uses)
    potion.consume()

    assertEquals(0, potion.uses)
    assertFalse(potion.canBeUsed)
  }

  @Test
  def aKeyHasInfiniteUsesAlwaysIsOne(): Unit = {
    val key = new Item(ItemType.Key, 20, 1) with UnlimitedUses with DefenseIncrease

    assertEquals(1, key.uses)

    key.consume()

    assertEquals(1, key.uses)
  }

  @Test
  def aShieldHasTwoUses(): Unit = {
    val shield = new Item(ItemType.Shield, 10, 2) with LimitedUses with DefenseIncrease
    shield.consume()

    assertEquals(1, shield.uses)
    assertTrue(shield.canBeUsed)

    shield.consume()

    assertEquals(0, shield.uses)
    assertFalse(shield.canBeUsed)
  }

}
