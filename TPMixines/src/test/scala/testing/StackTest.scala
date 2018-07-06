package testing

import item.itemType.ItemType
import item.uses.LimitedUses
import item.{Stackable, _}
import org.junit.Assert._
import org.junit.{Before, Test}
import player._
import player.buy_sell.{Merchantable, Seller}

class StackTest {

  val player = new Player(20)
  val i1 = new Item(ItemType.Arrow,1) with Merchantable with LimitedUses with Stackable
  val i2 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
  val i3 = new Item(ItemType.Arrow,3) with Merchantable with LimitedUses with Stackable
  val i4 = new Item(ItemType.Arrow,4) with Merchantable with LimitedUses with Stackable

  @Before
  def init(): Unit ={

    player.grab(i1)
    player.grab(i2)
    player.grab(i3)
    player.grab(i4)
  }

  @Test
  def aStackIsCreated(): Unit = {

    assertEquals(player.inventory.items.length, 1)
    
    assertEquals(i2,i1.next)
    assertEquals(i3,i2.next)
    assertEquals(i4,i3.next)
    assertNull(i4.next)

    assertEquals(i1,i1.top)
    assertEquals(i1,i2.top)
    assertEquals(i1,i3.top)
  }

  @Test
  def theStackVolumeIsFour(): Unit = {

    assertEquals(5,i1.totalVolume)
  }

  @Test
  def aPlayerThrowsOutStack(): Unit = {

    assertEquals(i1,i1.top)
    assertEquals(i1,i2.top)
    assertEquals(i1,i3.top)

    player.throwOut(i1)

    assertEquals(i2,i2.top)
    assertEquals(i2,i3.top)
    assertEquals(i2,i4.top)

    assertEquals(3,i2.getSize)
    assertFalse(player.inventory.items.contains(i1))
    assertEquals(1,player.inventory.items.size)
  }

  @Test
  def givenAStackOfUsableItemsThePlayerUsesOneOfThem(): Unit = {

    player.use(i1)

    assertEquals(3,i2.getSize)
  }

  @Test
  def aStackWithMerchandisableItemsIsSold(): Unit = {

    assertEquals(60,i1.getValueOfSell)
    assertEquals(20,player.gold)

    val seller = new Seller

    player.sell(i1,seller)

    assertEquals(0,player.inventory.items.size)
    assertEquals(80,player.gold)
  }

  @Test
  def aStackWithMerchandisableItemsIsBoughtByAPlayer(): Unit = {

    val player1 = new Player(20,gold=200)
    val i5 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i6 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i7 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i8 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable

    i5.stack(i6,i5)
    i6.stack(i7,i5)
    i7.stack(i8,i5)

    assertEquals(40,i5.getValueOfBuy)

    val seller = new Seller
    seller.addItem(i6)

    player1.buy(i5,seller)

    assertEquals(1,player1.inventory.items.size)
    assertEquals(160,player1.gold)
  }

  @Test
  def capacidadDeStack(): Unit = {

    val i5 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i6 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i7 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i8 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable

    i5.stack(i6)
    i6.stack(i7)
    i7.stack(i8)

    assertEquals(4,i5.itemType.stackCapacity)
  }

  @Test
  def unaPilaConTamanioCuatroNoPuedeAgregarCinco(): Unit = {

    val i5 = new Item(ItemType.Arrow, 2) with Merchantable with LimitedUses with Stackable

    assertEquals(1, player.inventory.items.size)

    player.grab(i5)

    assertEquals(2, player.inventory.items.size)
    assertTrue(player.inventory.items.contains(i5))
    assertNull(i4.next)

  }
  @Test
  def givenAnInventoryWithAFullStackWhenHeGrabsAnOtherItIsInHisInventory(): Unit = {

    val i5 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i6 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i7 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i8 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable

    i5.stack(i6,i5)
    i6.stack(i7,i5)
    i7.stack(i8,i5)

    player.grab(i5)

    assertEquals(2,player.inventory.items.size)
    assertTrue(player.inventory.items.contains(i5))
    assertTrue(player.inventory.items.contains(i1))
    assertEquals(4,i5.getSize)
  }

  @Test
  def givenAPlayerWithTwoStacksInHisInventoryWhenHeThrowsAnItemItIsNoLongerOnTheStack(): Unit = {

    val i5 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i6 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i7 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i8 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable

    i5.stack(i6,i5)
    i6.stack(i7,i5)
    i7.stack(i8,i5)

    player.grab(i5)

    player.throwOut(i1)

    assertEquals(2,player.inventory.items.size)
    assertTrue(player.inventory.items.contains(i5))
    assertFalse(player.inventory.items.contains(i1))
    assertTrue(player.inventory.items.contains(i2))
  }

  @Test
  def givenAPlayerWithTwoStacksInHisInventoryWhenHeUsesAnItemItIsNoLongerOnTheStack(): Unit = {

    val i5 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i6 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i7 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable
    val i8 = new Item(ItemType.Arrow,2) with Merchantable with LimitedUses with Stackable

    i5.stack(i6,i5)
    i6.stack(i7,i5)
    i7.stack(i8,i5)

    assertEquals(i5,i5.top)
    assertEquals(i5,i6.top)
    assertEquals(i5,i7.top)
    assertEquals(i5,i8.top)

    player.grab(i5)

    assertEquals(2,player.inventory.items.size)
    assertTrue(player.inventory.items.contains(i1))
    assertTrue(player.inventory.items.contains(i5))
    assertEquals(4,i1.getSize)
    assertEquals(4,i5.getSize)

    player.use(i7)

    assertEquals(2,player.inventory.items.size)
    assertTrue(player.inventory.items.contains(i1))
    assertTrue(player.inventory.items.contains(i6))
    assertFalse(player.inventory.items.contains(i5))

    assertEquals(4,i1.getSize)
    assertEquals(3,i6.getSize)
  }

  @Test
  def givenAPlayerUsesAllItemsInStackHisInventoryIsEmpty(): Unit = {

    player.use(i1)
    player.use(i2)
    player.use(i3)
    player.use(i4)

    assertEquals(0,player.inventory.items.size)
  }

}