name := "ammonite-scripts"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= {
  Seq(
    "com.lihaoyi" % "ammonite" % "1.4.4" % Compile cross CrossVersion.full
  )
}