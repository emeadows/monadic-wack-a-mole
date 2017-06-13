package example

import java.util.UUID

import exceptions.{CirceDecodeException, NoValueException}
import io.circe.{Decoder, Json}
import model.{Cat, Microchip}

import scala.util.Try

object CatHerdingForBeginners {

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
    val microchip: Decoder.Result[Microchip] = parseMicrochip(microchipJson)
    microchip.fold[Result[Cat]](_ => Left(CirceDecodeException), success => {
      stringToUUID(success.id).fold[Result[Cat]](fail => Left(fail), catId => {
        getCatById(catId).fold[Result[Cat]](Left(NoValueException))(cat => Right(cat))
      })
    })
  }

}

