import cats.data.{Xor, XorT}
import cats.std.future._
import com.pjanof.benchmarking.{Bucket, Benchmarker}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

object FunctionRunner {

  def execXorT(
    name: String,
    userFailure: Boolean,
    actionsFailure: Boolean,
    f: (String, Boolean, Boolean) => XorT[Future, ServiceError, List[Action]]
  )(implicit ec: ExecutionContext): Future[Unit] =
      f(name, userFailure, actionsFailure).fold(
        { err => println(s"Error Received: $err") },
        { actions => println(s"These are $name's actions ${actions.mkString(",")}") }
      ) recover {
          case ex => println(s"Future Error Received: $ex")
      }

  def execXor(
    name: String,
    userFailure: Boolean,
    actionsFailure: Boolean,
    f: (String, Boolean, Boolean) => Future[Xor[ServiceError, List[Action]]]
  )(implicit ec: ExecutionContext): Future[Unit] =
      f(name, userFailure, actionsFailure).map(xor =>
        xor.fold(
          { err => println(s"Error Received: $err") },
          { actions => println(s"These are $name's actions ${actions.mkString(",")}") }
        )
      ) recover {
          case ex => println(s"Future Error Received: $ex")
      }

  def execF(
    name: String,
    userFailure: Boolean,
    actionsFailure: Boolean,
    f: (String, Boolean, Boolean) => Future[List[Action]]
  )(implicit ec: ExecutionContext): Future[Unit] =
      f(name, userFailure, actionsFailure).map(actions =>
        println(s"These are $name's actions ${actions.mkString(",")}")
      ) recover {
          case ex => println(s"Future Error Received: $ex")
      }

  def asBenchmarkXorT(
    name: String,
    userFailure: Boolean,
    actionsFailure: Boolean,
    f: (String, Boolean, Boolean) => XorT[Future, ServiceError, List[Action]]
  )(implicit ec: ExecutionContext): Bucket[Xor[ServiceError, List[Action]]] =
      Benchmarker.benchmark(100) {
        f(name, userFailure, actionsFailure).value
      }

  def asBenchmarkXor(
    name: String,
    userFailure: Boolean,
    actionsFailure: Boolean,
    f: (String, Boolean, Boolean) => Future[Xor[ServiceError, List[Action]]]
  )(implicit ec: ExecutionContext): Bucket[Xor[ServiceError, List[Action]]] =
      Benchmarker.benchmark(100) {
        f(name, userFailure, actionsFailure)
      }

  def asBenchmarkF(
    name: String,
    userFailure: Boolean,
    actionsFailure: Boolean,
    f: (String, Boolean, Boolean) => Future[List[Action]]
  )(implicit ec: ExecutionContext): Bucket[List[Action]] =
      Benchmarker.benchmark(100) {
        f(name, userFailure, actionsFailure)
      }
}
