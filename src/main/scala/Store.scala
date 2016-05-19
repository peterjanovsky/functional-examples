import cats.data.{OptionT, Xor, XorT}
import cats.std.future._
import scala.concurrent.{ExecutionContext, Future}

sealed abstract class MySQLError
object MySQLError {
  final case object UserNotFound extends MySQLError
  final case class ConnectivityError(throwable: Throwable) extends MySQLError
}

object MySQL {

  def getUserByNameXorT(name: String, forceFailure: Boolean = false)(implicit ec: ExecutionContext): XorT[Future, Throwable, User] =
    if (forceFailure) XorT(Future.successful(Xor.left(new Throwable("db error"))))
    else {
      val userFO: Future[Option[User]] = Future.successful(UserStubs.users.find(_.name == name))
      val optT: OptionT[Future, User] = OptionT(userFO)
      optT.toRight[Throwable](new Throwable("user not found"))
    }

  def getUserByNameXor(name: String, forceFailure: Boolean = false)(implicit ec: ExecutionContext): Future[Xor[Throwable, User]] =
    if (forceFailure) Future.successful(Xor.left(new Throwable("db error")))
    else {
      val userO: Option[User] = UserStubs.users.find(_.name == name)
      Future.successful(userO.map(Xor.right) getOrElse(Xor.left(new Throwable("user not found"))))
    }

  def getUserByNameF(name: String, forceFailure: Boolean = false)(implicit ec: ExecutionContext): Future[User] =
    if (forceFailure) Future.failed(new Throwable("db error"))
    else {
      val userO: Option[User] = UserStubs.users.find(_.name == name)
      userO.map(Future.successful) getOrElse(Future.failed(new Throwable("user not found")))
    }
}

sealed abstract class CassandraError
object CassandraError {
  final case object ActionsNotFound extends CassandraError
  final case class ConnectivityError(throwable: Throwable) extends CassandraError
}

object Cassandra {

  def getActionsByUserXorT(user: User, forceFailure: Boolean = false)(implicit ec: ExecutionContext): XorT[Future, Throwable, List[Action]] =
    if (forceFailure) XorT(Future.successful(Xor.left(new Throwable("db error"))))
    else {
      val userActionsFO: Future[Option[(User, List[Action])]] = Future.successful(ActionStubs.actions.find { case (k,v) => k == user })
      val optT: OptionT[Future, (User, List[Action])] = OptionT(userActionsFO)
      optT.map(_._2).toRight[Throwable](new Throwable("user not found"))
    }

  def getActionsByUserXor(user: User, forceFailure: Boolean = false)(implicit ec: ExecutionContext): Future[Xor[Throwable, List[Action]]] =
    if (forceFailure) Future.successful(Xor.left(new Throwable("db error")))
    else {
      val userActionsO: Option[(User, List[Action])] = ActionStubs.actions.find { case (k,v) => k == user }
      Future.successful {
        userActionsO.map { case (k,v) => Xor.right(v) } getOrElse(Xor.left(new Throwable("actions not found")))
      }
    }

  def getActionsByUserF(user: User, forceFailure: Boolean = false)(implicit ec: ExecutionContext): Future[List[Action]] =
    if (forceFailure) Future.failed(new Throwable("db error"))
    else {
      val userActionsO: Option[(User, List[Action])] = ActionStubs.actions.find { case (k,v) => k == user }
      userActionsO.map { case (k,v) => Future.successful(v) } getOrElse { Future.failed(new Throwable("actions not found")) }
    }
}
