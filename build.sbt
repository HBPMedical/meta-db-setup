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
      val doobieVersion = "0.4.2"
      val diffsonVersion = "2.2.1"
      val flywayDbVersion = "4.2.0"
    }
    val scalaCheck: ModuleID = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
    val scalaTest: ModuleID = "org.scalatest"  %% "scalatest"  % Version.scalaTest
    val doobieCore: ModuleID = "org.tpolecat" %% "doobie-core-cats" % Version.doobieVersion
    val doobiePostgres: ModuleID =  "org.tpolecat" %% "doobie-postgres-cats" % Version.doobieVersion
    val doobieSpecs2: ModuleID = "org.tpolecat" %% "doobie-specs2-cats" % Version.doobieVersion
    val diffson: ModuleID = "org.gnieh" %% "diffson-circe" % Version.diffsonVersion
    val flywayDb: ModuleID = "org.flywaydb" % "flyway-core" % Version.flywayDbVersion
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
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value),
    wartremoverWarnings in (Compile, compile) ++= Warts.unsafe,
    test in assembly := {}
)

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtOnCompile.in(Sbt) := false,
    scalafmtVersion := "1.1.0"
  )
