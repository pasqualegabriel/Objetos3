package item.uses

trait LimitedUses extends Consumable {

  def consume(): Unit = uses -= 1
}
