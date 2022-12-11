package demo

import language.experimental.fewerBraces

import scala.io.Source
import zio.*
import zio.direct.*
import zio.direct.Dsl.Params
import zio.direct.core.metaprog.{Collect, TypeUnion}

object Main extends ZIOAppDefault:
  val forLoop =
    def getNums: UIO[List[Int]] = ZIO.succeed((0 to 5).toList)
    def printNum(i: Int): UIO[Unit] = ZIO.succeed(println(s"number: $i"))

    defer(Params(Collect.Parallel)):
      for
        n <- getNums.run
        // if n % 2 == 0 // currently doesn't work
      do printNum(n).run

      // Desugared for loop works if we use `filter` instead of `withFilter`
      // getNums.run.withFilter(n => n % 2 == 0).foreach(n => printNum(n).run)

  val whileLoopWithRef =
    defer:
      val i = Ref.make(10).run
      while i.get.run > 0 do
        println(s"hello ${i.get.run}")
        i.update(i => i - 1).run

  val tryCatchFinally =
    defer:
      try
        unsafe:
          ZIO.log("Running try block").run
          throw new IllegalArgumentException
      catch
        case ex: IllegalArgumentException => ZIO.log("Caught error").run
      finally
        ZIO.log("Running finalizer").run

  def run =
    defer:
      // whileLoopWithRef.run
      // tryCatchFinally.run
      forLoop.run
      // ServiceExample.task.run
      CompositionExample.task.run

object ServiceExample:
  // Service declaration
  trait FileService:
    def read(s: String): Task[Iterator[String]]
    def write(s: String): Task[Unit]

  object FileService:
    // Service implementation
    def live = ZLayer.succeed:
      new FileService:
        override def read(path: String) = ZIO.attempt(Source.fromFile(path).getLines)
        override def write(s: String): Task[Unit] = ZIO.succeed(println(s))

  val task = defer:
    val fs = ZIO.service[FileService].run
    val words = fs.read("/usr/share/dict/words").run.toIndexedSeq
    if words.nonEmpty then
      words.take(8).foreach(w => fs.write(w).run)
  .provide(FileService.live)

object CompositionExample:
  case class EnvA()
  case class EnvB()

  case class ErrorA() extends Exception
  case class ErrorB() extends Exception

  val composeEnv: RIO[EnvA & EnvB, Unit] =
    def fA(i: Int): ZIO[EnvA, Throwable, Int] = ZIO.succeed(i + 1)
    def fB(i: Int): ZIO[EnvB, Throwable, Int] = ZIO.succeed(-i)

    defer:
      val tuple = (fA(9).run, fB(5).run)
      ZIO.log(s"add $tuple = ${tuple._1 + tuple._2}").run

  object ErrorComposition:
    def fA(i: Int): ZIO[Any, ErrorA, Int] = ZIO.succeed(i + 1)
    def fB(i: Int): ZIO[Any, ErrorB, Int] = ZIO.succeed(-i)

    val asUnion: IO[ErrorA | ErrorB, Unit] =
      defer:
        val tuple = (fA(9).run, fB(5).run)
        ZIO.log(s"add $tuple = ${tuple._1 + tuple._2}").run

    def asUpperBound: IO[Exception, Unit] = ???
      // defer(Params(TypeUnion.LeastUpper)): // doesn't work
      //   val a = fA(9).run
      //   val b = fB(5).run
      //   ZIO.log(s"multiply $a $b = ${a * b}").run

  val task =
    val env = ZLayer.succeed(EnvA()) ++ ZLayer.succeed(EnvB())
    for
      _ <- composeEnv.provide(env)
      _ <- ErrorComposition.asUnion
    // _ <- ErrorComposition.asUpperBound
    yield ()


    // composeTask.provide(ZLayer.succeed(EnvA()) ++ ZLayer.succeed(EnvB()))
