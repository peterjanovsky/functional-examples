package com.pjanof.cats

import cats.data.{EitherT, OptionT, Xor, XorT}
import cats.instances.future._
import cats.Monad
import cats.syntax.applicative._  // provides the pure syntax
import com.pjanof.cats.TransformerTypes._
import org.scalatest.AsyncFlatSpec
import scala.concurrent.Future

class TransformerSpec extends AsyncFlatSpec {

  "Lifting an integer into the type TransformerErrorOptionXor" should "ultimately unpack to Xor[TransformerErrorType, Option[Int]]" in {
    val transformerErrorXorOption: TransformerErrorXorOption[Int] = 11.pure[TransformerErrorXorOption]
    val transformerErrorXor: TransformerErrorXor[Option[Int]] = transformerErrorXorOption.value
    val xorOption: Xor[TransformerErrorType, Option[Int]] = transformerErrorXor.value
    xorOption match {
      case Xor.Left(error) => fail("expected option, received Xor.left[TransformerErrorType, Option[Int]](ErrorType)")
      case Xor.Right(None) => fail("expection option, received Xor.right[TransformerErrorType, Option[Int]](None)")
      case Xor.Right(Some(num)) => assert(num == 11)
    }
  }

  "Building a simple monad stack using FutureXorOption" should "unpack to Future[Xor[TransformerErrorType, Option[Int]]]" in {
    val combinedMonad: FutureXorTOption[Int] = for {
      a <- 11.pure[FutureXorTOption]
      b <- 19.pure[FutureXorTOption]
    } yield a + b

    val futureXorT: FutureXorT[Option[Int]] = combinedMonad.value
    val futureXor: FutureXor[Option[Int]] = futureXorT.value
    futureXor.map(xor => xor match {
      case Xor.Left(error) => fail("expected option, received Xor.left[TransformerErrorType, Option[Int]](ErrorType)")
      case Xor.Right(None) => fail("expection option, received Xor.right[TransformerErrorType, Option[Int]](None)")
      case Xor.Right(Some(num)) => assert(num == 30)
    })
  }
}
