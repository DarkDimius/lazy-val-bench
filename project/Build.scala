

import sbt._
import Keys._
import Process._
import java.io.File



object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq (
    name := "sync-test",
    version := "0.1",
    scalaVersion := "2.11.0-M7",
    scalacOptions ++= Seq("-deprecation", "-optimise"),
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies ++= Seq(
//      "org.scala-lang" % "scala-reflect" % "2.11.0-SNAPSHOT"
      "com.github.axel22" %% "scalameter" % "0.5-M1"
      , "org.ow2.asm" % "asm-commons" % "4.2"
//      , "com.github.scala-blitz" %% "scala-blitz" % "1.0-M1"
            
    ),
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    logBuffered := false,
    parallelExecution in Test := false
  )
}


object WorkstealingBuild extends Build {
  
  /* projects */

  lazy val root = Project(
    "root",
    file("."),
    settings = BuildSettings.buildSettings
  ) 
//dependsOn (scalameter)

//  lazy val scalameter = RootProject(uri("git://github.com/axel22/scalameter.git"))

}










