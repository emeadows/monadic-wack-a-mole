package example

import java.util.UUID

import exceptions.{CirceDecodeException, NoValueException}
import io.circe.{Decoder, Json}
import model.{Cat, Microchip}

import scala.util.Try

object CatHerdingForBeginners2 {
  import Syntax1._

  type Result[A] = Either[Throwable, A]

  // Requires failure handling, supplied by Argonaut as DecodeResult
  def parseMicrochip(json: Json): Decoder.Result[Microchip] =
    json.as[Microchip]

  // Requires failure handling
  def stringToUUID(s: String): Either[Throwable, UUID] = {
    val triedUuid = Try(UUID.fromString(s))
    triedUuid.fold[Either[Throwable, UUID]](t => Left(t), id => Right(id))
  }

  // A cache miss (i.e. a None) could be a valid case or a failure
  def getCatById(id: UUID): Option[Cat] =
    Cat.allCats.find(_.id == id)

  // Takes some json, returns a cat if successful.
  def getCat(microchipJson: Json): Result[Cat] = {
    for {
      microchip <- parseMicrochip(microchipJson).liftResult
      id <- stringToUUID(microchip.id)
      cat <- getCatById(id).liftResult
    } yield cat
  }

}

object Syntax1 {

  type Result[A] = Either[Throwable, A]

  implicit class ResultOps[A](dr: Decoder.Result[A]) {
    // converts a DecodeResult to a pure disjunction, then maps across the either
    // for both success and failure values
    def liftResult: Result[A] = dr.fold(fail => Left(CirceDecodeException), success => Right(success))
  }

  implicit class OptionOps[A](option: Option[A]) {
    // folds over the option, converting this in to a Result[A] with a failure if none
    def liftResult: Result[A] = option.fold[Result[A]](Left(NoValueException))(value => Right(value))
  }

}



