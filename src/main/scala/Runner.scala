import cats.data.{Xor, XorT}
import cats.std.future._
import scala.concurrent.{ExecutionContext, Future}

object FunctionRunner {

  def execWithRecover(name: String, f: String => XorT[Future, ServiceError, List[Action]])(implicit ec: ExecutionContext): Future[Unit] =
    f(name).fold(
      { err => println(s"Error Received: $err") },
      { actions => println(s"These are $name's actions ${actions.mkString(",")}") }
    ) recover {
        case ex => println(s"Future Error Received: $ex")
    }

  def execWithoutRecover(name: String, f: String => XorT[Future, ServiceError, List[Action]])(implicit ec: ExecutionContext): Future[Unit] =
    f(name).fold(
      { err => println(s"Error Received: $err") },
      { actions => println(s"These are $name's actions ${actions.mkString(",")}") })
}
