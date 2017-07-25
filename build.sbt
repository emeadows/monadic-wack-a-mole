organization := "com.example"

name := "monadic-wack-a-mole"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.8.0",
  "io.circe" %% "circe-generic" % "0.8.0",
  "io.circe" %% "circe-parser" % "0.8.0",
  "org.typelevel" %% "cats" % "0.9.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases")
)

