package model

import java.util.UUID

import argonaut.Argonaut._
import argonaut.CodecJson

case class Microchip(id: String)

object Microchip {
  implicit def MicrochipCodec: CodecJson[Microchip] = casecodec1(Microchip.apply, Microchip.unapply)("id")

  val microchipId1 = UUID.randomUUID()
  val microchipId2 = UUID.randomUUID()
  val microchipId3 = UUID.randomUUID()
}