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
  * jmh:run -i 10 -wi 10 -f 2 -t 1 benchmarks.SequenceBenchmark
  *
  * Which means "10 iterations", "10 warm-up iterations", "2 forks", "1 thread".
  */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class SequenceBenchmark {
  @Benchmark
  def monixSequenceTaskA(): Long = {
    val tasks = (0 until 1000).map(_ => Task(1) <* Task.shift).toList
    val f = Task.sequence(tasks).map(_.sum.toLong).runAsync
    Await.result(f, Duration.Inf)
  }

  @Benchmark
  def monixSequenceTaskS(): Long = {
    val tasks = (0 until 1000).map(_ => Task.eval(1)).toList
    val f = Task.sequence(tasks).map(_.sum.toLong).runAsync
    Await.result(f, Duration.Inf)
  }

  @Benchmark
  def catsSequenceTaskA(): Long = {
    val tasks = (0 until 1000).map(_ => Task(1) <* Task.shift).toList
    val f = tasks.sequence.map(_.sum.toLong).runAsync
    Await.result(f, Duration.Inf)
  }

  @Benchmark
  def catsSequenceTaskS(): Long = {
    val tasks = (0 until 1000).map(_ => Task.eval(1)).toList
    val f = tasks.sequence.map(_.sum.toLong).runAsync
    Await.result(f, Duration.Inf)
  }

  @Benchmark
  def catsSequenceIOA(): Long = {
    val tasks = (0 until 1000).map(_ => IO(1) <* IO.shift).toList
    val f: IO[Long] = tasks.sequence.map(_.sum.toLong)
    f.unsafeRunSync
  }

  @Benchmark
  def catsSequenceIOS(): Long = {
    val tasks = (0 until 1000).map(_ => IO(1)).toList
    val f: IO[Long] = tasks.sequence.map(_.sum.toLong)
    f.unsafeRunSync
  }

  @Benchmark
  def catsParSequenceIOA(): Long = {
    val tasks = (0 until 1000).map(_ => IO(1) <* IO.shift).toList
    val f: IO[Long] = tasks.parSequence.map(_.sum.toLong)
    f.unsafeRunSync
  }

  @Benchmark
  def catsParSequenceIOS(): Long = {
    val tasks = (0 until 1000).map(_ => IO(1)).toList
    val f: IO[Long] = tasks.parSequence.map(_.sum.toLong)
    f.unsafeRunSync
  }

  @Benchmark
  def monixGatherTaskA(): Long = {
    val tasks = (0 until 1000).map(_ => Task(1) <* Task.shift).toList
    val f = Task.gather(tasks).map(_.sum.toLong).runAsync
    Await.result(f, Duration.Inf)
  }

  @Benchmark
  def monixGatherTaskS(): Long = {
    val tasks = (0 until 1000).map(_ => Task.eval(1)).toList
    val f = Task.gather(tasks).map(_.sum.toLong).runAsync
    Await.result(f, Duration.Inf)
  }

  @Benchmark
  def monixGatherUnorderedA(): Long = {
    val tasks = (0 until 1000).map(_ => Task(1) <* Task.shift).toList
    val f = Task.gatherUnordered(tasks).map(_.sum.toLong).runAsync
    Await.result(f, Duration.Inf)
  }

  @Benchmark
  def monixGatherUnorderedS(): Long = {
    val tasks = (0 until 1000).map(_ => Task.eval(1)).toList
    val f = Task.gatherUnordered(tasks).map(_.sum.toLong).runAsync
    Await.result(f, Duration.Inf)
  }
}