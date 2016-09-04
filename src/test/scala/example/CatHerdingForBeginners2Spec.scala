package example

import java.util.UUID

import argonaut.Argonaut._
import argonaut.Parse
import exceptions.{ArgonautDecodeException, NoValueException}
import model.{Cat, Microchip}
import org.scalatest.{FlatSpec, Matchers}

import scalaz.{-\/, \/-}

class CatHerdingForBeginners2Spec extends FlatSpec with Matchers {

//  import CatHerdingForBeginners.getCat
    import CatHerdingForBeginners2.getCat

  "getCat" should "return an ArgonautDecodeException if json cannot be parsed into a Microchip" in {
    val jsonIn = Parse.parse("{}").toOption.get
    val result = getCat(jsonIn)
    result shouldBe -\/(ArgonautDecodeException)
  }

  it should "return an NoValueException if id in Microchip is not valid UUID" in {
    val jsonIn = Microchip("Meow").asJson
    val result = getCat(jsonIn)
    result match {
      case -\/(e: IllegalArgumentException) => e.getMessage shouldBe "Invalid UUID string: Meow"
      case _ => fail
    }
  }

  it should "return an NoValueException if the UUID is not in the store" in {
    val jsonIn = Microchip(UUID.randomUUID().toString).asJson
    val result = getCat(jsonIn)
    result shouldBe -\/(NoValueException)
  }

  it should "return a cat if one found" in {
    val jasper = Cat.jasper
    val jsonIn = Microchip(jasper.id.toString).asJson
    val result = getCat(jsonIn)
    result shouldBe \/-(jasper)
  }

}
