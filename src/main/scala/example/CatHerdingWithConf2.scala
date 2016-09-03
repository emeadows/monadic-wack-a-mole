package example

import java.util.UUID

import argonaut._
import exceptions.{ArgonautDecodeException, NoValueException, UnknownDbException}
import model.Microchip._
import model.{Cat, CrazyCatLadyDb, DBConf, Microchip}

import scalaz._

object CatHerdingWithConf2 {

  import Syntax2._

  // Ties together all of the other methods
  def getCatWithConf(dBConf: DBConf, microchipJson: Json) = {
    for {
      chip <- parseMicrochip(microchipJson).liftConfiguredResult
      id <- stringToUUID(chip.id).liftConfiguredResult
      cat <- getCatById(id)
    } yield cat
  }.run(dBConf)

  // Requires failure handling
  def stringToUUID(s: String): \/[Throwable, UUID] =
    \/.fromTryCatchNonFatal(UUID.fromString(s))

  // Requires failure handling, supplied by Argonaut as DecodeResult
  def parseMicrochip(json: Json): DecodeResult[Microchip] =
    json.as[Microchip]

  // Requires failure handling AND configuration
  def getCatById(id: UUID): ConfiguredResult[DBConf, Cat] =
    ConfiguredResult[DBConf, Cat] {
      case CrazyCatLadyDb => Cat.allCats.find(_.id == id).liftResult
      case _ => -\/(UnknownDbException)
    }

}

object Syntax2 extends KleisliFunctions {
  type Result[A] = \/[Throwable, A]
  // the first type needs to be a type constructor
  type ConfiguredResult[DBConf, A] = Kleisli[Result, DBConf, A]

  object ConfiguredResult {
    def apply[DBConf, A](f: DBConf => Result[A]): ConfiguredResult[DBConf, A] = kleisli[Result, DBConf, A](f)
  }

  implicit class OptionOps[A](option: Option[A]) {
    def liftResult: Result[A] = option.fold[Result[A]](-\/(NoValueException))(value => \/-(value))
  }

  implicit class ResultWithDBConfOps[A](result: Result[A]) {
    def liftConfiguredResult: ConfiguredResult[DBConf, A] = kleisli[Result, DBConf, A] { (u: DBConf) => result }
  }

  implicit class ResultOps[A](dr: DecodeResult[A]) {
    // convert to a pure disjunction then map across success and failure values
    def liftResult: Result[A] =
      dr.toDisjunction.bimap(_ => ArgonautDecodeException, success => success)

    def liftConfiguredResult: ConfiguredResult[DBConf, A] = kleisli[Result, DBConf, A] { (u: DBConf) => dr.liftResult }
  }

}