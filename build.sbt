name := "gpw-quoter"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.0",
  "org.slf4j" % "slf4j-api" % "1.7.7",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.1",
  "org.apache.httpcomponents" % "fluent-hc" % "4.5.1"
)

    