import sbt.Resolver

lazy val commonSettings = Seq(
  organization := "br.com.semantix",
  version := "1.0.0",
  scalaVersion := "2.10.5"
)

lazy val root = (project in file(".")).
  settings(
    commonSettings,
    name := "test-kafka-consumer",

    libraryDependencies ++= Seq(
      // kafka
      "org.apache.spark" %% "spark-core" % "1.6.1" % "provided",
      "org.apache.spark" %% "spark-sql" % "1.6.1" % "provided",
      "org.apache.spark" %% "spark-streaming" % "1.6.1" % "provided",
      "org.apache.spark" %% "spark-streaming-kafka" % "1.6.1.2.4.2.0-258",

      // ibm mq
      "com.ibm.mq" % "com.ibm.mq.allclient" % "9.0.4.0",

      // json
      "org.json4s" %% "json4s-native" % "3.6.0",

      // config
      "com.typesafe" % "config" % "1.2.1",

      //solr
      "com.lucidworks.spark" % "spark-solr" % "2.0.4"
    ),

    // append several options to the list of options passed to the Java compiler
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    // append -deprecation to the options passed to the Scala compiler
    scalacOptions ++= Seq("-deprecation", "-unchecked"),

    resolvers ++= Seq(
      "Hortonworks Repository" at "http://repo.hortonworks.com/content/repositories//releases/",
      "Restlet Repository" at "http://maven.restlet.org/",
      Resolver.sonatypeRepo("public")
    ),

    // fork a new JVM for 'run' and 'test:run'
    fork := true,
    // fork a new JVM for 'test:run', but not 'run'
    fork in Test := true,
    // add a JVM option to use when forking a JVM for 'run'
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),

    // only use a single thread for building
    parallelExecution := false,
    // Execute tests in the current project serially
    //   Tests from other projects may still run concurrently.
    parallelExecution in Test := false,

    // uses compile classpath for the run task, including "provided" jar (cf http://stackoverflow.com/a/21803413/3827)
    run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in(Compile, run), runner in(Compile, run)).evaluated,

    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )
