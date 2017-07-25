package model

import java.util.UUID
import io.circe._
import io.circe.generic.semiauto._

import model.Microchip._


case class Cat(id: UUID, name: String)

object Cat {

  val snowball = Cat(microchipId1, "Snowball")
  val felix = Cat(microchipId2, "Felix")
  val jasper = Cat(microchipId3, "Jasper")

  val allCats = List(snowball, felix, jasper)

  implicit val UUIDEncodeJson: Encoder[UUID] = new Encoder[UUID] {
    final def apply(a: UUID): Json = Json.obj(
      ("id", Json.fromString(a.toString))
    )
  }

  implicit def UUIDDecodeJson: Decoder[UUID] = new Decoder[UUID] {
    final def apply(c: HCursor): Decoder.Result[UUID] =
      for {
        id <- c.downField("id").as[String]
      } yield UUID.fromString(id)
  }

  implicit def CatEncodeJson: Encoder[Cat] = deriveEncoder[Cat]

  implicit def CatDecodeJson: Decoder[Cat] = deriveDecoder[Cat]
}



