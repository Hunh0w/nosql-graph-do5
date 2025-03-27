# Build Spark Application

### (SparkProject directory)

### Add Spark dependency
add this into `build.sbt`
```sbt
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.5"
```

Due to some issues when build, I added this to fix duplicate errors of dependencies
```sbt
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}
```

### Build the Application
```sh
$ sbt assembly
```

The jar file should be located at target/scala-2.12/some-name.jar

If you need to change the main class, you can do it in the `build.sbt` file
And do this change into the `../activity9/spark-testuser.yaml` as well

