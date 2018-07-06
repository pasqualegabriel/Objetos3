package item.itemType

object ItemType {

  sealed trait ItemType {

    def stackCapacity = 0

    def slotCapacity(): Int = 1

    def isRune:Boolean = false
  }

  case object Empty extends ItemType

  case object Potion extends ItemType

  case object Parchment extends ItemType {
    override def stackCapacity: Int = 4
  }

  case object Arrow extends ItemType{
    override def stackCapacity: Int = 4
  }

  case object Armor extends ItemType {
    override def slotCapacity(): Int = 4
  }

  case object Shield extends ItemType

  case object EyeOfAnok extends ItemType

  case object Key extends ItemType{
    override def stackCapacity: Int = 4
  }

  case object Saeta extends ItemType{
    override def stackCapacity: Int = 4
  }

  case object Jewel extends ItemType

  trait Rune  extends ItemType{
    override def isRune:Boolean = true
  }

  case object RuneRo extends Rune

  case object RuneCa extends Rune

  case object RuneMo extends Rune

  case object RuneCo extends Rune

  case object RunePo extends Rune

  case object RuneLo extends Rune

}
