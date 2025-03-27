import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

case class WikiPage(title: String, text: String, isRedirect: Boolean)

object WikipediaAnalysis {

  def main(args: Array[String]): Unit = {
    val programStartTime = System.nanoTime()
    //Logger.getLogger("org").setLevel(Level.ERROR)

    val file = "data/wikipedia.dat"
    val spark = SparkSession.builder
      .appName("Wikipedia Analysis Application")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val rawData = spark.read
      .textFile(file)

    val extractedDf = rawData.map { line =>
      val titlePattern = "<title>(.*?)</title>".r
      val textPattern = "<text>(.*?)</text>".r

      val title = titlePattern.findFirstMatchIn(line).map(_.group(1)).getOrElse("")
      val text = textPattern.findFirstMatchIn(line).map(_.group(1)).getOrElse("")
      val isRedirect = text.contains("#REDIRECT")

      WikiPage(title, text, isRedirect)
    }.toDF()

    extractedDf.show(5, truncate = false)

    extractedDf.groupBy("isRedirect")
      .count().show()

    import org.apache.spark.sql.functions._

    val categoryPattern = "\\[\\[Category:(.*?)]]".r

    val extractCategories = udf { text: String =>
      categoryPattern.findAllMatchIn(text).map(_.group(1)).toSeq
    }

    val wikiWithCategories = extractedDf
      .withColumn("categories", extractCategories(col("text")))

    wikiWithCategories.select("title", "categories").show(false)

    val exploded = wikiWithCategories
      .withColumn("category", explode(col("categories")))

    exploded.groupBy("category")
      .count()
      .orderBy(desc("count"))
      .show(50, truncate = false)


    //spark.sparkContext.setLogLevel("ERROR")
    println(s"\nThis is Spark Version: $version")

    val programElapsedTime = (System.nanoTime() - programStartTime) / 1e9
    println(s"\nProgram execution time: $programElapsedTime seconds")
    println(".......Program *****Completed***** Successfully.....!\n")
    spark.stop()
  }

}