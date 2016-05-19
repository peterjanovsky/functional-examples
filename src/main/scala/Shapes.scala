import java.util.Date

case class User(name: String)

object UserStubs {

  val users: List[User] = List(User("john"), User("paul"), User("george"), User("ringo"))
}

sealed abstract class ActionType
object ActionType {
  final case object Login extends ActionType
  final case object Logout extends ActionType
}

case class Action(actionType: ActionType, dt: Date)

object ActionStubs {

  val actions: Map[User, List[Action]] = UserStubs.users.foldLeft( Map.empty[User, List[Action]] ) { (acc, elem) =>
    acc + (elem -> List(Action(ActionType.Login, new Date), Action(ActionType.Logout, new Date)))
  }
}
