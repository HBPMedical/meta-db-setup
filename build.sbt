// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `meta-db-setup` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, GitVersioning, GitBranchPrompt)
    .settings(settings)
    .settings(
      Seq(
        assemblyJarName in assembly := "meta-db-setup.jar",
        libraryDependencies ++= Seq(
          library.doobieCore,
          library.doobiePostgres,
          library.diffson,
          library.flywayDb,
          library.scalaCheck % Test,
          library.scalaTest  % Test
        )
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val scalaCheck = "1.13.5"
      val scalaTest  = "3.0.3"
      val doobie = "0.4.4"
      val diffson = "2.2.1"
      val flywayDb = "4.2.0"
    }
    val scalaCheck: ModuleID = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
    val scalaTest: ModuleID = "org.scalatest"  %% "scalatest"  % Version.scalaTest
    val doobieCore: ModuleID = "org.tpolecat" %% "doobie-core-cats" % Version.doobie
    val doobiePostgres: ModuleID =  "org.tpolecat" %% "doobie-postgres-cats" % Version.doobie
    val doobieSpecs2: ModuleID = "org.tpolecat" %% "doobie-specs2-cats" % Version.doobie
    val diffson: ModuleID = "org.gnieh" %% "diffson-circe" % Version.diffson
    val flywayDb: ModuleID = "org.flywaydb" % "flyway-core" % Version.flywayDb
    // "org.specs2" %% "specs2-core" % "3.8.9" % "test"
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  gitSettings ++
  scalafmtSettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.12.3",
    organization := "eu.humanbrainproject.mip",
    organizationName := "LREN CHUV",
    startYear := Some(2017),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-value-discard",
      "-Ypartial-unification",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value),
    wartremoverWarnings in (Compile, compile) ++= Warts.unsafe,
    fork in run := true,
    test in assembly := {},
    fork in Test := false,
    parallelExecution in Test := false
)

lazy val gitSettings =
  Seq(
    git.gitTagToVersionNumber := { tag: String =>
      if (tag matches "[0-9]+\\..*") Some(tag)
      else None
    },
    git.useGitDescribe := true
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtOnCompile.in(Sbt) := false,
    scalafmtVersion := "1.1.0"
  )
