import cats.data.{OptionT, Xor, XorT}
import cats.std.future._
import scala.concurrent.{ExecutionContext, Future}

sealed abstract class MySQLError
object MySQLError {
  final case object UserNotFound extends MySQLError
  final case class ConnectivityError(throwable: Throwable) extends MySQLError
}

object MySQL {

  // assumes users have unique names and underlying lib returns Future[Option[User]]
  def getUserByName(name: String)(implicit ec: ExecutionContext): XorT[Future, MySQLError, User] = {

    val resultFO: Future[Option[User]] = Future(Some(User(name)))
    val optT: OptionT[Future, User] = OptionT(resultFO)
    optT.toRight[MySQLError](MySQLError.UserNotFound)
  }

  def getUserByNameN(name: String)(implicit ec: ExecutionContext): XorT[Future, MySQLError, User] = {

    val resultFO: Future[Option[User]] = Future(None)
    val optT: OptionT[Future, User] = OptionT(resultFO)
    optT.toRight[MySQLError](MySQLError.UserNotFound)
  }

  def getUserByNameFF(name: String)(implicit ec: ExecutionContext): XorT[Future, MySQLError, User] = {

    val resultFO: Future[Option[User]] = Future(throw new Exception("db connection pool error"))
    val optT: OptionT[Future, User] = OptionT(resultFO)
    optT.toRight[MySQLError](MySQLError.UserNotFound)
  }
}

sealed abstract class CassandraError
object CassandraError {
  final case object UserActionStreamNotFound extends CassandraError
  final case class ConnectivityError(throwable: Throwable) extends CassandraError
}

object Cassandra {

  // assumes underlying lib returns Future[List[Action]]
  def getActionsByUser(user: User)(implicit ec: ExecutionContext): Future[Xor[CassandraError, List[Action]]] = {

    val resultF: Future[List[Action]] = Future(List(Action(ActionType.Login, user), Action(ActionType.Logout, user)))
    resultF.map(Xor.right[CassandraError, List[Action]]) recover {
      case ex => Xor.left[CassandraError, List[Action]](CassandraError.ConnectivityError(ex))
    }
  }

  def getActionsByUserF(user: User)(implicit ec: ExecutionContext): Future[Xor[CassandraError, List[Action]]] = {

    val resultF: Future[List[Action]] = Future(throw new Exception("db connection pool error"))
    resultF.map(Xor.right[CassandraError, List[Action]]) recover {
      case ex => Xor.left[CassandraError, List[Action]](CassandraError.ConnectivityError(ex))
    }
  }
}
