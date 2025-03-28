import org.apache.spark.sql.SparkSession

object TestUserMinioStreaming {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("SparkMinIOStreaming")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._

    val lines = spark.readStream
      .option("startingPosition","earliest")
      .option("host", "minio")
      .option("port", "9000")
      .text("s3a://data/")

    val words = lines.as[String].flatMap(_.split(" "))

    val query = words.writeStream
      .outputMode("append")
      .format("console")
      .start()

    query.awaitTermination()
    spark.stop()
  }
}