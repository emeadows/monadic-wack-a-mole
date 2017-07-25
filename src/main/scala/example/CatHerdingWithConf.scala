package example

import java.util.UUID

import exceptions._
import io.circe.{Decoder, Json}
import model.Microchip._
import model._

import scala.util.Try

object CatHerdingWithConf {

  type Result[A] = Either[Throwable, A]

  //   This is the same as the other examples but now we have db configuration
  def getCatWithConf(dBConf: DBConf, microchipJson: Json): Result[Cat] = {
    val chip: Decoder.Result[Microchip] = parseMicrochip(microchipJson)
    val cat: Result[Option[Cat]] = for {
      microchipId <- chip.fold[Result[String]](fail => Left(CirceDecodeException), microchip => Right(microchip.id))
      idAsUUID <- stringToUUID(microchipId)
      optionCat <- getCatById(dBConf)(idAsUUID)
    } yield optionCat


    cat.fold[Result[Cat]](
      f=> Left(f) ,
      _.fold[Result[Cat]](Left(NoValueException))(Right(_))
    )
  }

  // Requires failure handling, supplied by Argonaut as DecodeResult
  def parseMicrochip(json: Json): Decoder.Result[Microchip] =
    json.as[Microchip]

  // Requires failure handling
  def stringToUUID(s: String): Either[Throwable, UUID] = {
    val triedUuid = Try(UUID.fromString(s))
    triedUuid.fold[Either[Throwable, UUID]](t => Left(t), id => Right(id))
  }

  // Requires failure handling if configuration fails
  def getCatById(dBConf: DBConf)(id: UUID): Result[Option[Cat]] =
    dBConf match {
      case CrazyCatLadyDb => Right(Cat.allCats.find(_.id == id))
      case _ => Left(UnknownDbException)
    }
}









