import cats.data.{Xor, XorT}
import cats.std.future._
import scala.concurrent.{ExecutionContext, Future}

sealed abstract class ServiceError
object ServiceError {
  final case class MySQL(error: MySQLError) extends ServiceError
  final case class Cassandra(error: CassandraError) extends ServiceError
}

object Service {

  def mergeActionsWithUserXorT(
    name: String,
    userFailure: Boolean = false,
    actionsFailure: Boolean = false
  )(implicit ec: ExecutionContext): XorT[Future, Throwable, List[Action]] = 
      for {
        user <- MySQL.getUserByNameXorT(name, userFailure)
        actions <- Cassandra.getActionsByUserXorT(user, actionsFailure)
      } yield actions

  def mergeActionsWithUserXor(
    name: String,
    userFailure: Boolean = false,
    actionsFailure: Boolean = false
  )(implicit ec: ExecutionContext): Future[Xor[Throwable, List[Action]]] = 
      MySQL.getUserByNameXor(name, userFailure).flatMap(xor =>
        xor.fold(
          error => Future.successful(Xor.left(error)),
          user => Cassandra.getActionsByUserXor(user, actionsFailure)
        )
      )

  def mergeActionsWithUserF(
    name: String,
    userFailure: Boolean = false,
    actionsFailure: Boolean = false
  )(implicit ec: ExecutionContext): Future[List[Action]] =
      MySQL.getUserByNameF(name, userFailure).flatMap(user =>
        Cassandra.getActionsByUserF(user, actionsFailure)
      )
}
