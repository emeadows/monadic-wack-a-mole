package example

import java.util.UUID

import argonaut.Parse
import argonaut.Argonaut._
import exceptions.{UnknownDbException, NoValueException, ArgonautDecodeException}
import model.{CatteryDb, CrazyCatLadyDb, Cat, Microchip}
import org.scalatest.{Matchers, FlatSpec}

import scalaz.{\/-, -\/}

class CatHerdingWithConfSpec extends FlatSpec with Matchers {

  import CatHerdingWithConf.getCatWithConf
//  import CatHerdingWithConf2.getCatWithConf

  "getCat" should "return an ArgonautDecodeException if json cannot be parsed into a Microchip" in {
    val jsonIn = Parse.parse("{}").toOption.get
    val result = getCatWithConf(CatteryDb, jsonIn)
    result shouldBe -\/(ArgonautDecodeException)
  }

  it should "return an NoValueException if id in Microchip is not valid UUID" in {
    val jsonIn = Microchip("Meow").asJson
    val result = getCatWithConf(CatteryDb, jsonIn)
    result match {
      case -\/(e: IllegalArgumentException) => e.getMessage shouldBe "Invalid UUID string: Meow"
      case _ => fail
    }
  }

  it should "return an UnknownDbException if the DB is not correct" in {
    val jsonIn = Microchip(UUID.randomUUID().toString).asJson
    val result = getCatWithConf(CatteryDb, jsonIn)
    result shouldBe -\/(UnknownDbException)
  }

  it should "return an NoValueException if the UUID is not in the store" in {
    val jsonIn = Microchip(UUID.randomUUID().toString).asJson
    val result = getCatWithConf(CrazyCatLadyDb, jsonIn)
    result shouldBe -\/(NoValueException)
  }

  it should "return a cat if one found" in {
    val jasper = Cat.jasper
    val jsonIn = Microchip(jasper.id.toString).asJson
    val result = getCatWithConf(CrazyCatLadyDb, jsonIn)
    result shouldBe \/-(jasper)
  }

}