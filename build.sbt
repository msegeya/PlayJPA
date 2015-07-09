name := "PlayJPA"

organization := "com.fliptoo"

version := "1.0.1-SNAPSHOT"

scalaVersion := "2.11.6"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-core" % "4.3.10.Final"
)