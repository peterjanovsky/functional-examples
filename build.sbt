import com.typesafe.sbt.packager.SettingsHelper._

name := "functional-examples"

lazy val root = (project in file(".")).enablePlugins(SbtNativePackager, JavaAppPackaging, UniversalDeployPlugin)

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Maven Releases" at "http://repo.typesafe.com/typesafe/maven-releases"
  , "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
  , "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository" )

libraryDependencies ++= {
  val akkaV       = "2.4.2"
  val scalaTestV  = "2.2.4"

  Seq(
      "ch.qos.logback"              %   "logback-classic"                   % "1.1.3"
    , "com.typesafe.scala-logging"  %%  "scala-logging"                     % "3.1.0"
    , "com.typesafe.akka"           %%  "akka-actor"                        % akkaV
    , "com.typesafe.akka"           %%  "akka-slf4j"                        % akkaV
    , "com.typesafe.akka"           %%  "akka-testkit"                      % akkaV % "test"
    , "com.typesafe.akka"           %%  "akka-stream"                       % akkaV
    , "com.typesafe.akka"           %%  "akka-http-core"                    % akkaV
    , "com.typesafe.akka"           %%  "akka-http-experimental"            % akkaV
    , "com.typesafe.akka"           %%  "akka-http-spray-json-experimental" % akkaV
    , "com.typesafe.akka"           %%  "akka-http-testkit"                 % akkaV
    , "io.circe"                    %%  "circe-core"                        % "0.3.0"
    , "io.circe"                    %%  "circe-generic"                     % "0.3.0"
    , "io.circe"                    %%  "circe-parser"                      % "0.3.0"
    , "org.scalaz"                  %%  "scalaz-core"                       % "7.1.2"
    , "org.scalaz"                  %%  "scalaz-concurrent"                 % "7.1.2"
    , "org.typelevel"               %%  "cats"                              % "0.4.1"
    , "com.pjanof"                  %%  "smicro"                            % "0.1-SNAPSHOT"
    , "org.scalatest"               %%  "scalatest"                         % "2.2.4" % "test" ) }

// continuous build
Revolver.settings

// run options
javaOptions in run ++= Seq(
  "-Dconfig.file=src/main/resources/application.conf",
  "-Dlogback.configurationFile=src/main/resources/logback.xml"
)

scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

// test options
javaOptions in Test += "-Dconfig.file=src/test/resources/application.conf"

scalacOptions in Test ++= Seq("-Yrangepos")

fork := true

// deploy
deploymentSettings

publishMavenStyle := true

// sbt-native-packager - universal:publish
makeDeploymentSettings(Universal, packageZipTarball in Universal, "tgz")

// sbt-release - publish
val packageTgz = taskKey[File]("package-zip-tarball")

packageTgz := (baseDirectory in Compile).value / "target" / "universal" / (name.value + "-" + version.value + ".tgz")

artifact in (Universal, packageTgz) ~= { (art:Artifact) => art.copy(`type` = "tgz", extension = "tgz") }

addArtifact(artifact in (Universal, packageTgz), packageTgz in Universal)

publish <<= (publish) dependsOn (packageZipTarball in Universal)
