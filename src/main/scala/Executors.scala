import java.util.concurrent.{ExecutorService, Executors, TimeUnit}
import scala.concurrent.ExecutionContext


object ExecutorsForTesting {

  class OurExecutorService(delegate: ExecutorService) extends java.util.concurrent.AbstractExecutorService {

    override def shutdown: Unit = {
      println("shutdown invoked")
      delegate.shutdown()
    }

    override def execute(r: Runnable): Unit = {
      println("--"*20 +   "execute invoked")
      delegate.execute(r)
    }

    override def awaitTermination(timeout: Long, timeUnit: TimeUnit): Boolean = {
      println("await termination invoked")
      delegate.awaitTermination(timeout, timeUnit)
    }

    override def isShutdown: Boolean = {
      println("isShutdown invoked")
      delegate.isShutdown
    }

    override def isTerminated: Boolean = {
      println("isTerminated invoked")
      delegate.isTerminated
    }

    override def shutdownNow: java.util.List[Runnable] = {
      println("shutdownNow invoked")
      delegate.shutdownNow()
    }
  }

  val processors: Int = Runtime.getRuntime.availableProcessors

  implicit val es = new OurExecutorService(Executors.newFixedThreadPool(processors))

  implicit val ec = new ExecutionContext {

    override def reportFailure(cause: Throwable): Unit = {
      println(cause)
    }

    override def execute(runnable: Runnable): Unit = {
      es.execute(runnable)
    }
  }
}
