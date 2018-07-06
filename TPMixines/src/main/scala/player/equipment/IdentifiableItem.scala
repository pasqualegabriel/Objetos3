package player.equipment

object IdentifiableItem {

  sealed trait IdentifiableItem{
    def isIdentified: Boolean
  }

  case object IdentifiablePositive extends IdentifiableItem {
      def isIdentified: Boolean ={
        true
      }
  }

  case object IdentifiableNegative extends IdentifiableItem {
    def isIdentified: Boolean ={
      false
    }
  }

}
