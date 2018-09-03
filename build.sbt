name := "benchmarks"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "io.monix" %% "monix" % "3.0.0-RC2-d0feeba",
  "org.typelevel" %% "cats-effect" % "1.0.0-RC3"
)

enablePlugins(JmhPlugin)