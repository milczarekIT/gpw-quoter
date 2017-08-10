name := "gpw-quoter"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.5.3",
  "org.apache.httpcomponents" % "fluent-hc" % "4.5.3",
  "com.hazelcast" % "hazelcast" % "3.8.3"
)

    