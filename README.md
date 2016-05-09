# functional-examples

## running

Use `FunctionRunner#execWithRecover` when are not certain if `Future#failure` from an underlying library has been converted
to a `Left`

Use `FunctionRunner#execWithoutRecover` when you are certain failure recovery has been handled

```scala
scala> import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext.Implicits.global

scala> FunctionRunner.execWithRecover("peter", Service.mergeActionsWithUserS)
These are peter's actions Action(Login,User(peter)),Action(Logout,User(peter))
res0: scala.concurrent.Future[Unit] = Success(())

scala> FunctionRunner.execWithRecover("peter", Service.mergeActionsWithUserN)
Error Received: MySQL(UserNotFound)
res1: scala.concurrent.Future[Unit] = List()

scala> FunctionRunner.execWithRecover("peter", Service.mergeActionsWithUserFF)
Future Error Received: java.lang.Exception: db connection pool error
res2: scala.concurrent.Future[Unit] = List()

scala> FunctionRunner.execWithRecover("peter", Service.mergeActionsWithUserSF)
Error Received: Cassandra(ConnectivityError(java.lang.Exception: db connection pool error))
res3: scala.concurrent.Future[Unit] = List()
```
