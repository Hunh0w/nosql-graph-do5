import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.types.{LongType, StringType, StructType}

object FraudDetection {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .appName("Neo4j Spark Application")
      .config("spark.driver.memory", "8G")
      .config("neo4j.url", "bolt://34.230.44.227:7687")
      .config("neo4j.authentication.basic.username", "neo4j")
      .config("neo4j.authentication.basic.password", "password")
      .config("neo4j.authentication.basic.database", "neo4j")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val neo4jDF = spark.read
      .format("org.neo4j.spark.DataSource")
      .option("query", "MATCH (:Client:FirstPartyFraudster)-[]-(txn:Transaction)-[]-(c:Client)\nWHERE NOT c:FirstPartyFraudster\nUNWIND labels(txn) AS transactionType\nRETURN transactionType, count(*) AS freq")
      .load()

    neo4jDF.show()

    spark.stop()
  }
}
