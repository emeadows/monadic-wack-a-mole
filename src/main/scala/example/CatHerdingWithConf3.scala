package example

import java.util.UUID

import exceptions._
import io.circe.{Decoder, Json}
import model.Microchip._
import model._

import scala.util.Try

object CatHerdingWithConf3 {

  import Syntax3._
  // Ties together all of the other methods

  def getCatWithConf(dBConf: DBConf, microchipJson: Json): Result[Cat] =
    for {
      microchip <-  parseMicrochip(microchipJson).liftResult
      uuid <- stringToUUID(microchip.id)
      cat <- catReader(uuid).run(dBConf)
    } yield cat

  def getCatNameWithConf(dBConf: DBConf, microchipJson: Json): Result[String] =
    for {
      microchip <-  parseMicrochip(microchipJson).liftResult
      uuid <- stringToUUID(microchip.id)
      cat <- nameReader(uuid).run(dBConf)
    } yield cat


  def parseMicrochip(json: Json): Decoder.Result[Microchip] =
    json.as[Microchip]

  // Requires failure handling
  def stringToUUID(s: String): Either[Throwable, UUID] = {
    val triedUuid = Try(UUID.fromString(s))
    triedUuid.fold[Either[Throwable, UUID]](t => Left(t), id => Right(id))
  }

  def catReader(id: UUID) =
    ResultReader[DBConf, Cat] {
      case CrazyCatLadyDb => Cat.allCats.find(_.id == id).liftResult
      case _ => Left(UnknownDbException)
    }

  def nameReader(catId: UUID) =
    catReader(catId).map {
      case catResult => catResult.fold[Result[String]](
        fail => Left(fail),
        cat => Right(cat.name)
      )
    }
}

object Syntax3 {

  import cats.data.{Kleisli, Reader, ReaderT}

  type Result[A] = Either[Throwable, A]
  type ResultReader[DBConf, A] = ReaderT[Result, DBConf, A]

  object ResultReader {
    def apply[Config, A](run: Config => Result[A]): Reader[Config, Result[A]] =
      Reader[Config, Result[A]](run)
  }

  implicit class ResultOps[A](dr: Decoder.Result[A]) {
    // converts a DecodeResult to a pure disjunction, then maps across the either
    // for both success and failure values
    def liftResult: Result[A] = dr.fold(fail => Left(CirceDecodeException), success => Right(success))

    def liftConfiguredResult: ResultReader[DBConf, A] =
      Kleisli[Result, DBConf, A] { (u: DBConf) => dr.liftResult }
  }

  implicit class OptionOps[A](option: Option[A]) {
    // folds over the option, converting this in to a Result[A] with a failure if none
    def liftResult: Result[A] = option.fold[Result[A]](Left(NoValueException))(value => Right(value))
  }

  implicit class ResultWithDBConfOps[A](result: Result[A]) {
    def liftConfiguredResult: ResultReader[DBConf, A] =
      Kleisli[Result, DBConf, A] { (conf: DBConf) => result }
  }
}