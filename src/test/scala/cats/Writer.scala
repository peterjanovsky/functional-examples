package com.pjanof.cats

import cats.data.{WriterT, Xor, XorT}
import cats.instances.future._
import com.pjanof.cats.WriterTypes._
import org.scalatest.AsyncFlatSpec
import scala.concurrent.Future

class WriterSpec extends AsyncFlatSpec {

  "The addition step" should "return FutureXor" in {
    val we = new WriterExamples()
    val writerT: WriterT[FutureXorT, String, Int] = we.addStep(20)

    // extract the log
    val futureXortL: FutureXorT[String] = writerT.written
    val futureXorL: FutureXor[String] = futureXortL.value

    futureXorL.map(xor => xor match {
      case Xor.Left(error) => fail("expected string")
      case Xor.Right(log) =>
        assert(log == "Result of addition step: 22")
    })

    // extract the value
    val futureXortV: FutureXorT[Int] = writerT.value
    val futureXorV: FutureXor[Int] = futureXortV.value

    futureXorV.map(xor => xor match {
      case Xor.Left(error) => fail("expected int")
      case Xor.Right(opResult) =>
        assert(opResult == 22)
    })

    // extract the tuple (log, value)
    val futureXortLV: FutureXorT[(String, Int)] = writerT.run
    val futureXorLV: FutureXor[(String, Int)] = futureXortLV.value

    futureXorLV.map(xor => xor match {
      case Xor.Left(error) => fail("expected tuple")
      case Xor.Right((log, opResult)) =>
        assert(log == "Result of addition step: 22")
        assert(opResult == 22)
    })
  }

  "The multiplication step" should "return FutureXor" in {
    val we = new WriterExamples()
    val writerT: WriterT[FutureXorT, String, Int] = we.multiplyStep(20)
    val futureXort: FutureXorT[(String, Int)] = writerT.run
    val futureXor: Future[Xor[WriterErrorType, (String, Int)]] = futureXort.value
    futureXor.map(xor => xor match {
      case Xor.Left(error) => fail("expected tuple")
      case Xor.Right((log, opResult)) =>
        assert(log == "Result of multiplication step: 40")
        assert(opResult == 40)
    })
  }

  "Combining the addition and multiplication steps" should "return FutureXor" in {
    import cats.instances.string._

    val se = new WriterExamples()
    def combinedSteps(num: Int): WriterT[FutureXorT, String, (Int, Int)] = for {
      a <- se.addStep(num)
      b <- se.multiplyStep(a)
    } yield (a,b)

    val futureXort: FutureXorT[(String, (Int, Int))] = combinedSteps(20).run
    val futureXor: Future[Xor[WriterErrorType, (String, (Int, Int))]] = futureXort.value
    futureXor.map(xor => xor match {
      case Xor.Left(error) => fail("expected tuple")
      case Xor.Right((log, opResult)) =>
        println(s"LOG: $log")
        //assert(log._1 == "Result of addition step: 22")
        //assert(log._2 == "Result of multiplication step: 44")
        assert(opResult._1 == 22) 
        assert(opResult._2 == 44)
    })
  }

/*
  "The modulo step" should "return FutureOption" in {
    val se = new WriterExamples()
    val futureOptionT: FutureOptionT[(String, Int)] = se.moduloStep.run(20)
    val futureOption: Future[Option[(String, Int)]] = futureOptionT.value
    futureOption.map(opt => opt.map { tup =>
      assert(tup._1 == 0)
      assert(tup._2 == "Result of modulus step: 0")
    } getOrElse(fail("expected tuple")))
  }

  "Combining the addition, multiplication and modulus steps" should "return FutureXor" in {
    val se = new WriterExamples()
    val combinedSteps: WriterT[FutureXorT, Int, (String, String, String)] = for {
      a <- se.addStep
      b <- se.multiplyStep
      c <- se.liftOptionT(se.moduloStep)
    } yield (a,b,c)

    val futureXort: FutureXorT[(Int, (String, String, String))] = combinedSteps.run(20)
    val futureXor: Future[Xor[ErrorType, (Int, (String, String, String))]] = futureXort.value
    futureXor.map(xor => xor match {
      case Xor.Left(error) => fail("expected tuple")
      case Xor.Right((log, opResult)) =>
        assert(opResult == 0)
        assert(log._1 == "Result of addition step: 22")
        assert(log._2 == "Result of multiplication step: 44")
        assert(log._3 == "Result of modulus step: 0")
    })
  }
*/
}
