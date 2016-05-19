# functional-examples

## running

Use `FunctionRunner#execWithRecover` when are not certain if `Future#failure` from an underlying library has been converted
to a `Left`

Use `FunctionRunner#execWithoutRecover` when you are certain failure recovery has been handled

```scala
scala> import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext.Implicits.global

scala> FunctionRunner.execWithRecover("paul", false, false, Service.mergeActionsWithUser)
These are paul's actions Action(Login,Tue May 17 00:46:39 PDT 2016),Action(Logout,Tue May 17 00:46:39 PDT 2016)

scala> FunctionRunner.execWithRecover("peter", false, false, Service.mergeActionsWithUser)
Error Received: MySQL(UserNotFound)

scala> FunctionRunner.execWithRecover("paul", true, false, Service.mergeActionsWithUser)
Future Error Received: java.lang.Throwable: db error

scala> FunctionRunner.execWithRecover("paul", false, true, Service.mergeActionsWithUser)
Error Received: Cassandra(ConnectivityError(java.lang.Throwable: db error))
```

## context switches

```scala
scala> import ExecutorsForTesting._
import ExecutorsForTesting._
```

```scala
FunctionRunner.execWithRecover("peter", Service.mergeActionsWithUserS)
```
Results in 11 `OurExecutorService#execute` invocations

```scala
FunctionRunner.execWithRecover("peter", Service.mergeActionsWithUserN)
```
Results in 6 `OurExecutorService#execute` invocations

```scala
FunctionRunner.execWithRecover("peter", Service.mergeActionsWithUserFF)
```
Results in 6 `OurExecutorService#execute` invocations

```scala
FunctionRunner.execWithRecover("peter", Service.mergeActionsWithUserSF)
```
Results in 11 `OurExecutorService#execute` invocations
