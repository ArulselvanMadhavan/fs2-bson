package dev.composter.fs2.bson
import scala.concurrent.ExecutionContext
import cats.effect._
import java.util.concurrent.Executors
import com.monovore.decline._
import cats.implicits._
import java.nio.file.Path
import com.monovore.decline.effect._
import dev.composter.fs2.bson.BsonLoader
import fs2.Stream
import fs2.io.file
import reactivemongo.bson._
import scala.util.control.NoStackTrace

object FileExample
    extends CommandIOApp(
      name = "fs2-bson-examples",
      header = "fs2-bson-examples command line",
      version = "0.0.1"
    ) {

  case class CommandLineConfigs(pathToBson: Path)

  sealed abstract class ParseError extends Throwable with NoStackTrace
  case object InvalidDoc extends ParseError

  val blocker = Blocker.liftExecutionContext(ExecutionContext.fromExecutorService(Executors.newCachedThreadPool()))

  val pathToBsonFileOpts = Opts.argument[Path](metavar = "file").map(CommandLineConfigs(_))

  def extractObjectId(d: BSONDocument): Either[String, BSONObjectID] =
    d.get("_id")
      .collect {
        case x: BSONObjectID => x
      }
      .liftTo[Either[String, ?]]("InvalidDoc")

  def runBsonParser(c: CommandLineConfigs): IO[ExitCode] = {
    val src: Stream[IO, Byte] = file.readAll[IO](c.pathToBson, blocker, 1024)
    src
      .through(BsonLoader.bsonDocumentPipe)
      .map(_.flatMap(extractObjectId))
      .evalMap(x => IO(println(x)))
      .compile
      .drain *> IO(ExitCode.Success)
  }

  override def main: Opts[IO[ExitCode]] =
    (pathToBsonFileOpts).map(runBsonParser(_))

}
