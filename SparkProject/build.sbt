name := "SimpleProject"

version := "1.0"

scalaVersion := "2.12.18"

Compile/mainClass := Some("KafkaStreaming")

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "3.5.3",
  "org.apache.spark" %% "spark-core" % "3.5.3",
  "org.neo4j" % "neo4j-connector-apache-spark_2.12" % "5.3.5_for_spark_3",
  "org.mongodb.spark" %% "mongo-spark-connector" % "10.4.0",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.3"
)

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}