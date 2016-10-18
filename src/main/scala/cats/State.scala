package com.pjanof.cats

import cats.data.{OptionT, StateT, Xor, XorT}
import cats.instances.future._
import scala.concurrent.{ExecutionContext, Future}

sealed abstract class ErrorType
object ErrorTypes {
  case object FooError extends ErrorType
  case object BarError extends ErrorType
  case object BazError extends ErrorType
}

object StateTypes {
  type FutureXor[A] = Future[Xor[ErrorType, A]]
  type FutureXorT[A] = XorT[Future, ErrorType, A]

  type FutureOption[A] = Future[Option[A]]
  type FutureOptionT[A] = OptionT[Future, A]
}

final class StateExamples()(implicit ec: ExecutionContext) {
  import StateTypes._

  // add 2
  val addStep = StateT[FutureXorT, Int, String] { num =>
    val res = num + 2
    XorT(Future.successful(Xor.right((res, s"Result of addition step: $res"))))
  }

  // multiply by 2
  val multiplyStep = StateT[FutureXorT, Int, String] { num =>
    val res = num * 2
    XorT(Future.successful(Xor.right((res, s"Result of multiplication step: $res"))))
  }

  // modulo 2
  val moduloStep = StateT[FutureOptionT, Int, String] { num =>
    val res = num % 2
    OptionT(Future.successful(Option((res, s"Result of modulus step: $res"))))
  }

  def liftOptionT(s: StateT[FutureOptionT, Int, String]): StateT[FutureXorT, Int, String] =
    s.transformF[FutureXorT, String] { (optT: OptionT[Future, (Int, String)]) => XorT(
      optT.map { tup => Xor.right[ErrorType, (Int, String)](tup._1, tup._2) } getOrElse(Xor.left[ErrorType, (Int, String)](ErrorTypes.FooError))
    ) }
}
