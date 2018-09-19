package benchmarks

import java.util.concurrent.TimeUnit

import cats.effect.IO
import monix.eval.Task
import org.openjdk.jmh.annotations._
import cats.implicits._

import scala.concurrent.Await
import scala.concurrent.duration._

/** To run the benchmark from within SBT:
  *
  * jmh:run -i 20 -wi 10 -f 1 -t 1 benchmarks.SequenceBenchmark
  *
  * Which means "20 iterations", "10 warm-up iterations", "1 forks", "1 thread".
  */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class SequenceBenchmark {
  @Benchmark
  def monixSequenceTask(): Long = {
    val tasks = (0 until 1000).map(_ => Task.eval(1)).toList
    val f = Task.sequence(tasks).map(_.sum.toLong).runAsync
    Await.result(f, Duration.Inf)
  }

  @Benchmark
  def catsSequenceTask(): Long = {
    val tasks = (0 until 1000).map(_ => Task.eval(1)).toList
    val f = tasks.sequence.map(_.sum.toLong).runAsync
    Await.result(f, Duration.Inf)
  }

  @Benchmark
  def catsSequenceIO(): Long = {
    val tasks = (0 until 1000).map(_ => IO(1)).toList
    val f: IO[Long] = tasks.sequence.map(_.sum.toLong)
    f.unsafeRunSync
  }

  @Benchmark
  def catsParSequenceIO(): Long = {
    val tasks = (0 until 1000).map(_ => IO(1)).toList
    val f: IO[Long] = tasks.parSequence.map(_.sum.toLong)
    f.unsafeRunSync
  }

  @Benchmark
  def monixGatherTask(): Long = {
    val tasks = (0 until 1000).map(_ => Task.eval(1)).toList
    val f = Task.gather(tasks).map(_.sum.toLong).runAsync
    Await.result(f, Duration.Inf)
  }

  @Benchmark
  def monixGatherUnordered(): Long = {
    val tasks = (0 until 1000).map(_ => Task.eval(1)).toList
    val f = Task.gatherUnordered(tasks).map(_.sum.toLong).runAsync
    Await.result(f, Duration.Inf)
  }
}