package example

import java.util.UUID

import argonaut._
import exceptions.{ArgonautDecodeException, NoValueException}
import model.{Cat, Microchip}

import scalaz.{-\/, \/, \/-}

object CatHerdingForBeginners {

  type Result[A] = \/[Throwable, A]

  // Requires failure handling
  def stringToUUID(s: String): \/[Throwable, UUID] =
    \/.fromTryCatchNonFatal(UUID.fromString(s))

  // Requires failure handling, supplied by Argonaut as DecodeResult
  def parseMicrochip(json: Json): DecodeResult[Microchip] =
    json.as[Microchip]

  // Takes some json, returns a cat if successful.
  def getCat(microchipJson: Json): Result[Cat] = {
    val chip: DecodeResult[Microchip] = parseMicrochip(microchipJson)
    val cat: \/[Throwable, Option[Cat]] = for {
      microchipId <- chip.toDisjunction.bimap(_ => ArgonautDecodeException, microchip => microchip.id)
      idAsUUID <- stringToUUID(microchipId)
    } yield getCatById(idAsUUID)
    cat.flatMap(_.fold[\/[Throwable, Cat]](-\/(NoValueException))(c => \/-(c)))
  }

  // A cache miss (i.e. a None) could be a valid case or a failure
  def getCatById(id: UUID): Option[Cat] = cats.find(_.id == id)

  private val cats: List[Cat] =
    List(
      Cat.snowball,
      Cat.felix,
      Cat.jasper
    )

}

