package example

import java.util.UUID

import example.CatHerdingForBeginners2.getCat
import exceptions.{CirceDecodeException, NoValueException, UnknownDbException}
import io.circe.Json
import io.circe.syntax._
import model.{Cat, CatteryDb, CrazyCatLadyDb, Microchip}
import org.scalatest.{FlatSpec, Matchers}


class CatHerdingWithConfSpec extends FlatSpec with Matchers {

//  import CatHerdingWithConf2.getCatWithConf
  import CatHerdingWithConf3.getCatWithConf

  "getCat" should "return an CirceDecodeException if json cannot be parsed into a Microchip" in {
    val jsonIn = Json.fromString("{}")
    val result = getCat(jsonIn)
    result shouldBe Left(CirceDecodeException)
  }

  it should "return an NoValueException if id in Microchip is not valid UUID" in {
    val jsonIn = Microchip("Meow").asJson
    val result = getCat(jsonIn)
    result match {
      case Left(e: IllegalArgumentException) => e.getMessage shouldBe "Invalid UUID string: Meow"
      case _ => fail
    }
  }

  it should "return an UnknownDbException if the DB is not correct" in {
    val jsonIn = Microchip(UUID.randomUUID().toString).asJson
    val result = getCatWithConf(CatteryDb, jsonIn)
    result shouldBe Left(UnknownDbException)
  }

  it should "return an NoValueException if the UUID is not in the store" in {
    val jsonIn = Microchip(UUID.randomUUID().toString).asJson
    val result = getCatWithConf(CrazyCatLadyDb, jsonIn)
    result shouldBe Left(NoValueException)
  }

  it should "return a cat if one found" in {
    val jasper = Cat.jasper
    val jsonIn = Microchip(jasper.id.toString).asJson
    val result = getCatWithConf(CrazyCatLadyDb, jsonIn)
    result shouldBe Right(jasper)
  }

  it should "return a cat name if one found" in {
    import CatHerdingWithConf3.getCatNameWithConf
    val jasper = Cat.jasper
    val jsonIn = Microchip(jasper.id.toString).asJson
    val result = getCatNameWithConf(CrazyCatLadyDb, jsonIn)
    result shouldBe Right(jasper.name)
  }
}