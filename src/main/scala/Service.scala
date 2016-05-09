import cats.data.{Xor, XorT}
import cats.std.future._
import scala.concurrent.{ExecutionContext, Future}

sealed abstract class ServiceError
object ServiceError {
  final case class MySQL(error: MySQLError) extends ServiceError
  final case class Cassandra(error: CassandraError) extends ServiceError
}

object Service {

  private def liftFuture(f: Future[Xor[CassandraError, List[Action]]])(
    implicit ec: ExecutionContext
  ): XorT[Future, ServiceError, List[Action]] =
    XorT(
      f.map(xor =>
        xor.fold(
          error => Xor.left(ServiceError.Cassandra(error)),
          actions => Xor.right[ServiceError, List[Action]](actions)
        )
      )
    )

  def mergeActionsWithUserS(name: String)(implicit ec: ExecutionContext): XorT[Future, ServiceError, List[Action]] = 
    for {
      user <- MySQL.getUserByName(name).leftMap(ServiceError.MySQL)
      actions <- liftFuture(Cassandra.getActionsByUser(user))
    } yield actions

  def mergeActionsWithUserN(name: String)(implicit ec: ExecutionContext): XorT[Future, ServiceError, List[Action]] = 
    for {
      user <- MySQL.getUserByNameN(name).leftMap(ServiceError.MySQL)
      actions <- liftFuture(Cassandra.getActionsByUser(user))
    } yield actions

  def mergeActionsWithUserFF(name: String)(implicit ec: ExecutionContext): XorT[Future, ServiceError, List[Action]] = 
    for {
      user <- MySQL.getUserByNameFF(name).leftMap(ServiceError.MySQL)
      actions <- liftFuture(Cassandra.getActionsByUser(user))
    } yield actions

  def mergeActionsWithUserSF(name: String)(implicit ec: ExecutionContext): XorT[Future, ServiceError, List[Action]] = 
    for {
      user <- MySQL.getUserByName(name).leftMap(ServiceError.MySQL)
      actions <- liftFuture(Cassandra.getActionsByUserF(user))
    } yield actions
}
