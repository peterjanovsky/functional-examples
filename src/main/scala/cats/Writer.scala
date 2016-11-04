package com.pjanof.cats

import cats.data.{OptionT, WriterT, Xor, XorT}
import cats.instances.future._
import scala.concurrent.{ExecutionContext, Future}

sealed abstract class WriterErrorType
object WriterErrorTypes {
  case object FooWriterError extends WriterErrorType
  case object BarWriterError extends WriterErrorType
  case object BazWriterError extends WriterErrorType
}

object WriterTypes {
  type FutureXor[A] = Future[Xor[WriterErrorType, A]]
  type FutureXorT[A] = XorT[Future, WriterErrorType, A]

  type FutureOption[A] = Future[Option[A]]
  type FutureOptionT[A] = OptionT[Future, A]
}

final class WriterExamples()(implicit ec: ExecutionContext) {
  import WriterTypes._

  // add 2
  def addStep(num: Int) = {
    val res = num + 2
    WriterT[FutureXorT, String, Int](
      XorT(Future.successful(Xor.right((s"Result of addition step: $res", res))))
    )
  }

  // multiply by 2
  def multiplyStep(num: Int) = {
    val res = num * 2
    WriterT[FutureXorT, String, Int](
      XorT(Future.successful(Xor.right((s"Result of multiplication step: $res", res))))
    )
  }

  // modulo 2
  def moduloStep(num: Int) = {
    val res = num % 2
    WriterT[FutureOptionT, String, Int](
      OptionT(Future.successful(Option((s"Result of modulus step: $res", res))))
    )
  }

/*
  def liftOptionT(s: WriterT[FutureOptionT, Int, String]): WriterT[FutureXorT, Int, String] =
    s.transformF[FutureXorT, String] { (optT: OptionT[Future, (Int, String)]) => XorT(
      optT.map { tup => Xor.right[WriterErrorType, (Int, String)](tup._1, tup._2) } getOrElse(Xor.left[WriterErrorType, (Int, String)](WriterErrorTypes.FooWriterError))
    ) }
*/
}
