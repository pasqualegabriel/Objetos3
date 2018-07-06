package item.uses

trait UnlimitedUses extends Consumable {

  uses = 1

  def consume(): Unit = {}
}
