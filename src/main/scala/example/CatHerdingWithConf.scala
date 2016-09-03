package example

import java.util.UUID

import argonaut._
import exceptions._
import model.Microchip._
import model._

import scalaz._

object CatHerdingWithConf {

  type Result[A] = \/[Throwable, A]

//   This is the same as the other examples but now we have db configuration
  def getCatWithConf(dBConf: DBConf, microchipJson: Json): Result[Cat] = {
    val chip: DecodeResult[Microchip] = parseMicrochip(microchipJson)
    val cat: \/[Throwable, Option[Cat]] = for {
      microchipId <- chip.toDisjunction.bimap(_ => ArgonautDecodeException, microchip => microchip.id)
      idAsUUID <- stringToUUID(microchipId)
      optionCat <- getCatById(dBConf)(idAsUUID)
    } yield optionCat
    cat.flatMap(_.fold[\/[Throwable, Cat]](-\/(NoValueException))(c => \/-(c)))
  }

  // Requires failure handling
  def stringToUUID(s: String): \/[Throwable, UUID] =
    \/.fromTryCatchNonFatal(UUID.fromString(s))

  // Requires failure handling, supplied by Argonaut as DecodeResult
  def parseMicrochip(json: Json): DecodeResult[Microchip] =
    json.as[Microchip]

  // Requires failure handling if configuration fails
  def getCatById(dBConf: DBConf)(id: UUID): Result[Option[Cat]] =
    dBConf match {
      case CrazyCatLadyDb => \/-(Cat.allCats.find(_.id == id))
      case _ => -\/(UnknownDbException)
    }
}