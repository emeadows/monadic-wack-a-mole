organization := "com.example"

name := "monadic-wack-a-mole"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.11.7"

lazy val doobieVersion = "0.2.0"
lazy val argonautVersion = "6.1" 

libraryDependencies ++= Seq(
  "org.tpolecat"   %% "doobie-core"               % doobieVersion,
  "org.tpolecat"   %% "doobie-contrib-postgresql" % doobieVersion,
  "org.tpolecat"   %% "doobie-contrib-specs2"     % doobieVersion,
  "io.argonaut"    %% "argonaut"                  % argonautVersion,
  "org.scalactic"  %% "scalactic"                 % "3.0.0",
  "org.scalatest"  %% "scalatest"                 % "3.0.0" % "test"
)

resolvers ++= Seq(
  "tpolecat" at "http://dl.bintray.com/tpolecat/maven",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
  Resolver.sonatypeRepo("releases"),
  "Artima Maven Repository" at "http://repo.artima.com/releases"
)

