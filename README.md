# functional-examples

## running

Use `LogicRunner#execWithRecover` when are not certain if `Future#failure` from an underlying library has been converted
to a `Left`

Use `LogicRunner#execWithoutRecover` when you are certain failure recovery has been handled

```scala
scala> import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext.Implicits.global

scala> LogicRunner.execWithRecover("peter", Logic.mergeBooksWithAuthorS)
These are peter's books Book(Foo,Author(peter)),Book(Bar,Author(peter))
res0: scala.concurrent.Future[Unit] = Success(())

scala> LogicRunner.execWithRecover("peter", Logic.mergeBooksWithAuthorN)
Error Received: RecordNotFound
res1: scala.concurrent.Future[Unit] = List()

scala> LogicRunner.execWithRecover("peter", Logic.mergeBooksWithAuthorFF)
Future Error Received: java.lang.Exception: db connection pool error
res2: scala.concurrent.Future[Unit] = List()

scala> LogicRunner.execWithRecover("peter", Logic.mergeBooksWithAuthorSF)
Error Received: StoreError(java.lang.Exception: db connection pool error)
res3: scala.concurrent.Future[Unit] = List()
```
