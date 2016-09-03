package model

import java.util.UUID

import argonaut.Argonaut._
import argonaut.{CodecJson, DecodeJson, EncodeJson}
import model.Microchip._

case class Cat(id: UUID, name: String)

object Cat {

  val snowball = Cat(microchipId1, "Snowball")
  val felix = Cat(microchipId2, "Felix")
  val jasper = Cat(microchipId3, "Jasper")

  val allCats = List(snowball, felix, jasper)

  implicit def UUIDEncodeJson: EncodeJson[UUID] =
    EncodeJson((id: UUID) => ("id" := id.toString) ->: jEmptyObject)

  implicit def UUIDDecodeJson: DecodeJson[UUID] =
    DecodeJson(c => for {
      id <- (c --\ "id").as[String]
    } yield UUID.fromString(id))

  implicit def CatCodec: CodecJson[Cat] = casecodec2(Cat.apply, Cat.unapply)("id", "name")

}



