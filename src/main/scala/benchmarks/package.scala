import cats.effect.{ContextShift, IO}
import monix.execution.ExecutionModel.SynchronousExecution
import monix.execution.Scheduler
import monix.execution.Scheduler.Implicits.global

package object benchmarks {
  implicit val scheduler: Scheduler =
    global.withExecutionModel(SynchronousExecution)

  implicit val ctx: ContextShift[IO] =
    IO.contextShift(scheduler)
}