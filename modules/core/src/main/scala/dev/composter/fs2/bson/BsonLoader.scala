package dev.composter.fs2.bson
import fs2.{ Chunk, Pipe, Pull, Stream }
import reactivemongo.bson.buffer.DefaultBufferHandler
import reactivemongo.bson.buffer.ArrayReadableBuffer
import reactivemongo.bson.BSONDocument
import java.nio.ByteBuffer
import io.chrisdavenport.log4cats.Logger
import cats._

object BsonLoader {

  def littleEndianToBigEndian(value: Int): Int = {
    val b1 = (value >> 0) & 0xff
    val b2 = (value >> 8) & 0xff
    val b3 = (value >> 16) & 0xff
    val b4 = (value >> 24) & 0xff
    b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0
  }

  def bsonDocumentPipe[F[_]: Logger: Monad]: Pipe[F, Byte, Either[Throwable, BSONDocument]] = {

    def outputBsonDoc(
        lenBytes: Chunk[Byte],
        tail: Stream[F, Byte]
    ): Pull[F, Either[Throwable, BSONDocument], Option[Stream[F, Chunk[Byte]]]] = {
      val lenArray  = lenBytes.toArray
      val lenLittle = ByteBuffer.wrap(lenArray).getInt
      val lenBig    = littleEndianToBigEndian(lenLittle)
      tail.pull.unconsN(lenBig - lenArray.length).flatMap {
        case Some((docBytes, nextDocs)) => {
          val docArray         = docBytes.toArray
          val bsonDocByteArray = lenArray ++ docArray
          Pull.output1(DefaultBufferHandler.readDocument(ArrayReadableBuffer(bsonDocByteArray)).toEither) >> doPull(
            nextDocs
          )
        }
        case None => Pull.pure(None)
      }
    }

    def doPull(
        s: Stream[F, Byte]
    ): Pull[F, Either[Throwable, BSONDocument], Option[Stream[F, Chunk[Byte]]]] =
      s.pull.unconsN(4, allowFewer = false).flatMap {
        case Some((lenBytes, tail)) => outputBsonDoc(lenBytes, tail)
        case None                   => Pull.pure[F, Option[Stream[F, Chunk[Byte]]]](None)
      }

    (in: Stream[F, Byte]) => doPull(in).void.stream
  }

}
