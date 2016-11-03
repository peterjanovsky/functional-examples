package com.pjanof.cats

import cats.data.{EitherT, OptionT, Xor, XorT}
import cats.instances.future._
import scala.concurrent.{ExecutionContext, Future}

sealed abstract class TransformerErrorType
object TransformerErrorTypes {
  case object FooTransformerError extends TransformerErrorType
  case object BarTransformerError extends TransformerErrorType
  case object BazTransformerError extends TransformerErrorType
}

/** general patterns for constructing monad stacks
 *
 *  build from the inside out
 *    first type parameter to monad transformer is the outer monad
 *    transformer provides the inner monad
 *  define type aliases with single type parameters for each intermediate layer
 *
 *  in general monads do not compose as they model effects, however some monads
 *  can be made to compose using monad-specific glue (transformers)
 *
 *  expose untransformed stacks at module boundaries
 *    apply transformations locally to operate on monads
 *    untransformed results before passing them on
 */
object TransformerTypes {
  type TransformerErrorXor[A] = TransformerErrorType Xor A
  type TransformerErrorXorOption[A] = OptionT[TransformerErrorXor, A]

  type FutureXor[A] = Future[Xor[TransformerErrorType, A]]
  type FutureXorT[A] = XorT[Future, TransformerErrorType, A]

  type FutureXorOption[A] = OptionT[FutureXor, A]
  type FutureXorTOption[A] = OptionT[FutureXorT, A]
}
