# functional-examples

## running
```scala
scala> import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext.Implicits.global

scala> LogicRunner.exec("peter", Logic.mergeBooksWithAuthorS)
These are peter's books Book(Foo,Author(peter)),Book(Bar,Author(peter))
res0: scala.concurrent.Future[Unit] = Success(())

scala> LogicRunner.exec("peter", Logic.mergeBooksWithAuthorN)
Error Received: RecordNotFound
res1: scala.concurrent.Future[Unit] = List()

scala> LogicRunner.exec("peter", Logic.mergeBooksWithAuthorFF)
Future Error Received: java.lang.Exception: db connection pool error
res2: scala.concurrent.Future[Unit] = List()

scala> LogicRunner.exec("peter", Logic.mergeBooksWithAuthorSF)
Error Received: StoreError(java.lang.Exception: db connection pool error)
res3: scala.concurrent.Future[Unit] = List()
```
