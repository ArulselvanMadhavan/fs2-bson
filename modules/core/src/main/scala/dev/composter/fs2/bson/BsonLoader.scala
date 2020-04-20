package dev.composter.fs2.bson
import fs2.{ Chunk, Pipe, Pull, Pure, Stream }
import reactivemongo.bson.buffer.DefaultBufferHandler
import reactivemongo.bson.buffer.ArrayReadableBuffer
import reactivemongo.bson.BSONDocument
import java.nio.ByteBuffer

object BsonLoader {

  def littleEndianToBigEndian(value: Int): Int = {
    val b1 = (value >> 0) & 0xff
    val b2 = (value >> 8) & 0xff
    val b3 = (value >> 16) & 0xff
    val b4 = (value >> 24) & 0xff
    b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0
  }

  def bsonDocumentPipe[F[_]]: Pipe[F, Byte, BSONDocument] = {

    def outputBsonDoc(
        lenBytes: Chunk[Byte],
        tail: Stream[Pure, Byte]
    ): Pull[Pure, BSONDocument, Option[Stream[Pure, Chunk[Byte]]]] = {
      val lenArray  = lenBytes.toArray
      val lenLittle = ByteBuffer.wrap(lenArray).getInt
      val lenBig    = littleEndianToBigEndian(lenLittle)
      tail.pull.unconsN(lenBig - lenArray.length).flatMap {
        case Some((docBytes, nextDocs)) => {
          val docArray         = docBytes.toArray
          val bsonDocByteArray = lenArray ++ docArray
          DefaultBufferHandler.readDocument(ArrayReadableBuffer(bsonDocByteArray)).toEither match {
            case Right(bsonDoc) => Pull.output1(bsonDoc) >> doPull(nextDocs)
            case Left(ex) => {
              println(s"Error occured while reading bsonDocAsBytes ${ex}")
              println(s"LenLittle-${lenLittle}\tLenBig-${lenBig}\tdocArrayLen-${docArray.length}")
              doPull(nextDocs)
            }
          }
        }
        case None => Pull.pure(None)
      }
    }

    def doPull(
        s: Stream[Pure, Byte]
    ): Pull[Pure, BSONDocument, Option[Stream[Pure, Chunk[Byte]]]] =
      s.pull.unconsN(4, allowFewer = false).flatMap {
        case Some((lenBytes, tail)) => outputBsonDoc(lenBytes, tail)
        case None                   => Pull.pure(None)
      }

    (in: Stream[Pure, Byte]) => doPull(in).void.stream
  }

}
