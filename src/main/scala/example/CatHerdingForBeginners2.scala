package example

import java.util.UUID

import argonaut._
import exceptions.{ArgonautDecodeException, NoValueException}
import model.{Cat, Microchip}

import scalaz.{-\/, \/, \/-}

object CatHerdingForBeginners2 {
  import Syntax1._

  def getCat(microchipJson: Json): Result[Cat] =
    for {
      chip <- parseMicrochip(microchipJson).liftResult
      id <- stringToUUID(chip.id)
      cat <- getCatById(id).liftResult
    } yield cat

  // Requires failure handling
  def stringToUUID(s: String): \/[Throwable, UUID] =
    \/.fromTryCatchNonFatal(UUID.fromString(s))

  // Requires failure handling, supplied by Argonaut as DecodeResult
  def parseMicrochip(json: Json): DecodeResult[Microchip] =
    json.as[Microchip]


  // A cache miss (i.e. a None) could be a valid case or a failure
  def getCatById(id: UUID): Option[Cat] = Cat.allCats.find(_.id == id)

}

object Syntax1 {

  type Result[A] = \/[Throwable, A]

  implicit class ResultOps[A](dr: DecodeResult[A]) {
    // converts a DecodeResult to a pure disjunction, then maps across the either
    // for both success and failure values
    def liftResult: Result[A] =
      dr.toDisjunction.bimap(_ => ArgonautDecodeException, identity)
  }

  implicit class OptionOps[A](option: Option[A]) {
    // folds over the option, converting this in to a Result[A] with a failure if none
    def liftResult: Result[A] = option.fold[Result[A]](-\/(NoValueException))(value => \/-(value))
  }
}



