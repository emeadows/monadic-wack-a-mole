package model

import java.util.UUID
import io.circe._
import io.circe.generic.semiauto._

case class Microchip(id: String)

object Microchip {

  val microchipId1 = UUID.randomUUID()
  val microchipId2 = UUID.randomUUID()
  val microchipId3 = UUID.randomUUID()

  implicit def microchipEncodeJson: Encoder[Microchip] = deriveEncoder[Microchip]
  implicit def microchipDecodeJson: Decoder[Microchip] = deriveDecoder[Microchip]
}