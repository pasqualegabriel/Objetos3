package item.itemType

import item.Item
import item.uses.LimitedUses
import itemEffects.DefenseIncrease

class Potion(volumePotion: Int, valueOfBuyPotion: Int = 10, valueOfSellPotion: Int = 15)
  extends Item(ItemType.Potion, volumePotion, 1, valueOfBuyPotion, valueOfSellPotion) with LimitedUses with DefenseIncrease

