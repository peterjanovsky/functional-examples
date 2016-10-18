package com.pjanof.cats

import cats.data.{StateT, Xor, XorT}
import cats.instances.future._
import com.pjanof.cats.StateTypes._
import org.scalatest.AsyncFlatSpec
import scala.concurrent.Future

class StateSpec extends AsyncFlatSpec {

  "The addition step" should "return FutureXor" in {
    val se = new StateExamples()
    val futureXort: FutureXorT[(Int, String)] = se.addStep.run(20)
    val futureXor: FutureXor[(Int, String)] = futureXort.value
    futureXor.map(xor => xor match {
      case Xor.Left(error) => fail("expected tuple")
      case Xor.Right((opResult, log)) =>
        assert(opResult == 22)
        assert(log == "Result of addition step: 22")
    })
  }

  "The multiplication step" should "return FutureXor" in {
    val se = new StateExamples()
    val futureXort: FutureXorT[(Int, String)] = se.multiplyStep.run(20)
    val futureXor: Future[Xor[ErrorType, (Int, String)]] = futureXort.value
    futureXor.map(xor => xor match {
      case Xor.Left(error) => fail("expected tuple")
      case Xor.Right((opResult, log)) =>
        assert(opResult == 40)
        assert(log == "Result of multiplication step: 40")
    })
  }

  "Combining the addition and multiplication steps" should "return FutureXor" in {
    val se = new StateExamples()
    val combinedSteps: StateT[FutureXorT, Int, (String, String)] = for {
      a <- se.addStep
      b <- se.multiplyStep
    } yield (a,b)

    val futureXort: FutureXorT[(Int, (String, String))] = combinedSteps.run(20)
    val futureXor: Future[Xor[ErrorType, (Int, (String, String))]] = futureXort.value
    futureXor.map(xor => xor match {
      case Xor.Left(error) => fail("expected tuple")
      case Xor.Right((opResult, log)) =>
        assert(opResult == 44)
        assert(log._1 == "Result of addition step: 22")
        assert(log._2 == "Result of multiplication step: 44")
    })
  }

  "The modulo step" should "return FutureOption" in {
    val se = new StateExamples()
    val futureOptionT: FutureOptionT[(Int, String)] = se.moduloStep.run(20)
    val futureOption: Future[Option[(Int, String)]] = futureOptionT.value
    futureOption.map(opt => opt.map { tup =>
      assert(tup._1 == 0)
      assert(tup._2 == "Result of modulus step: 0")
    } getOrElse(fail("expected tuple")))
  }

  "Combining the addition, multiplication and modulus steps" should "return FutureXor" in {
    val se = new StateExamples()
    val combinedSteps: StateT[FutureXorT, Int, (String, String, String)] = for {
      a <- se.addStep
      b <- se.multiplyStep
      c <- se.liftOptionT(se.moduloStep)
    } yield (a,b,c)

    val futureXort: FutureXorT[(Int, (String, String, String))] = combinedSteps.run(20)
    val futureXor: Future[Xor[ErrorType, (Int, (String, String, String))]] = futureXort.value
    futureXor.map(xor => xor match {
      case Xor.Left(error) => fail("expected tuple")
      case Xor.Right((opResult, log)) =>
        assert(opResult == 0)
        assert(log._1 == "Result of addition step: 22")
        assert(log._2 == "Result of multiplication step: 44")
        assert(log._3 == "Result of modulus step: 0")
    })
  }
}
