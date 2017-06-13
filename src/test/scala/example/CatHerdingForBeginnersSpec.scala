package example

import java.util.UUID

import io.circe._
import exceptions.{CirceDecodeException, NoValueException}
import model.{Cat, Microchip}
import org.scalatest.{FlatSpec, Matchers}
import io.circe.syntax._


class CatHerdingForBeginnersSpec extends FlatSpec with Matchers {

  import CatHerdingForBeginners2.getCat

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

  it should "return an NoValueException if the UUID is not in the store" in {
    val jsonIn = Microchip(UUID.randomUUID().toString).asJson
    val result = getCat(jsonIn)
    result shouldBe Left(NoValueException)
  }

  it should "return a cat if one found" in {
    val jasper = Cat.jasper
    val jsonIn = Microchip(jasper.id.toString).asJson
    val result = getCat(jsonIn)
    result shouldBe Right(jasper)
  }
}
