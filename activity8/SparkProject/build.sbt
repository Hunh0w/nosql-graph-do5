name := "Simple Project"

version := "1.0"

scalaVersion := "2.12.18"

Compile/mainClass := Some("TestUserMinio")

libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.5"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}