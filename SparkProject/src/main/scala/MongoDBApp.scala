import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.types.{LongType, StringType, StructType}

object MongoDBApp {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .appName("MongoDB Spark Application")
      .config("spark.driver.memory", "8G")
      .config("spark.mongodb.read.connection.uri", "mongodb+srv://admin:bdstz0VpDfjvNkuf@test.mingkc3.mongodb.net/Test.Users")
      .config("spark.mongodb.write.connection.uri", "mongodb+srv://admin:bdstz0VpDfjvNkuf@test.mingkc3.mongodb.net/Test.Users")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val schema = new StructType()
      .add("id", LongType, nullable = true)
      .add("name", StringType, nullable = true)
      .add("age", LongType, nullable = true)
      .add("city", StringType, nullable = true)

    val df = spark.read
      .option("header", "true")
      .schema(schema)
      .csv("data/users.csv")


    df.write.format("mongodb")
      .mode("overwrite")
      .option("database", "Test")
      .option("collection", "Users")
      .save()

    println("users.csv written to MongoDB !")

    val readedDF = spark.read
      .format("mongodb")
      .option("database", "Test")
      .option("collection", "Users")
      .load()

    readedDF.show()

    spark.stop()
  }
}
