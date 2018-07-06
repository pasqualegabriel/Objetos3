package player.equipment

object SlotPart {

  sealed trait SlotPart

  case object LeftHand extends SlotPart {
    def opposite(): SlotPart = SlotPart.RightHand
  }

  case object RightHand extends SlotPart {
    def opposite(): SlotPart = SlotPart.LeftHand
  }

  case object Glove extends SlotPart

  case object Armor extends SlotPart

  case object Helmet extends SlotPart{

  }
}
