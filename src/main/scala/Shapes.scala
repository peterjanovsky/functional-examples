case class User(name: String)

sealed abstract class ActionType
object ActionType {
  final case object Login extends ActionType
  final case object Logout extends ActionType
}

case class Action(actionType: ActionType, user: User)
