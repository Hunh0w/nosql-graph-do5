import org.apache.spark.sql.SparkSession

object KafkaStreaming {

  def main(args: Array[String]): Unit = {
    var server = "localhost:9092"
    if (args.length >= 1) {
      server = args(0)
    }

    val spark = SparkSession.builder()
      .appName("UserStreamingKafka")
      .master("local[*]")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")

    import spark.implicits._

    val stream = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", server) // Change this to your Kafka broker
      .option("subscribe", "streaming.test") // Change this to your Kafka topic
      .option("startingOffsets", "earliest")
      .load()

    val words = stream.selectExpr("CAST(value AS STRING)").as[String]

    words.writeStream
      .outputMode("append")
      .format("console")
      .option("truncate", "false")
      .start()
      .awaitTermination()

  }
}