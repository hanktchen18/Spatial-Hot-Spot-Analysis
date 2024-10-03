
package cse512
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions._
object HotcellAnalysis {
  Logger.getLogger("org.spark_project").setLevel(Level.WARN)
  Logger.getLogger("org.apache").setLevel(Level.WARN)
  Logger.getLogger("akka").setLevel(Level.WARN)
  Logger.getLogger("com").setLevel(Level.WARN)
def runHotcellAnalysis(spark: SparkSession, pointPath: String): DataFrame =
{
  // Load the original data from a data source
  var pickupInfo = spark.read.format("com.databricks.spark.csv").option("delimiter",";").option("header","false").load(pointPath);
  pickupInfo.createOrReplaceTempView("nyctaxitrips")
  pickupInfo.show()
  // Assign cell coordinates based on pickup points
  spark.udf.register("CalculateX",(pickupPoint: String)=>((
    HotcellUtils.CalculateCoordinate(pickupPoint, 0)
    )))
  spark.udf.register("CalculateY",(pickupPoint: String)=>((
    HotcellUtils.CalculateCoordinate(pickupPoint, 1)
    )))
  spark.udf.register("CalculateZ",(pickupTime: String)=>((
    HotcellUtils.CalculateCoordinate(pickupTime, 2)
    )))
  pickupInfo = spark.sql("select CalculateX(nyctaxitrips._c5),CalculateY(nyctaxitrips._c5), CalculateZ(nyctaxitrips._c1) from nyctaxitrips")
  var newCoordinateName = Seq("x", "y", "z")
  pickupInfo = pickupInfo.toDF(newCoordinateName:_*)
  pickupInfo.show()
  // Define the min and max of x, y, z
  val minX = -74.50/HotcellUtils.coordinateStep
  val maxX = -73.70/HotcellUtils.coordinateStep
  val minY = 40.50/HotcellUtils.coordinateStep
  val maxY = 40.90/HotcellUtils.coordinateStep
  val minZ = 1
  val maxZ = 31
  val numCells = (maxX - minX + 1)*(maxY - minY + 1)*(maxZ - minZ + 1)
  // YOU NEED TO CHANGE THIS PART
  val pickupInfoTempView = pickupInfo.createOrReplaceTempView("pickupInfoTempView")
  val cellsWithinRange = spark.sql("select x, y, z from pickupInfoTempView where x >= " + minX + " and x <= " + maxX + " and y >= " + minY + " and y <= " + maxY + " and z >= " + minZ +" and z <= " + maxZ)
  cellsWithinRange.createOrReplaceTempView("cellsWithinRange")
  // cellsWithinRange.show()

  val hotCells = spark.sql("select x, y, z, count(*) as hotCells from cellsWithinRange group by x, y, z order by z, y, x")
  hotCells.createOrReplaceTempView("hotCells")
  // hotCells.show()

  val countPickupPoints = spark.sql("select sum(hotCells) as countPickupPoints, sum(hotCells * hotCells) as squareSumHotCells from hotCells")
  countPickupPoints.createOrReplaceTempView("countPickupPoints")
  // countPickupPoints.show()

  val mean = (countPickupPoints.first().getLong(0)).toDouble / numCells.toDouble
  val sd = math.sqrt((countPickupPoints.first().getLong(1)).toDouble / numCells.toDouble - math.pow(mean, 2))
  
  spark.udf.register("neighbours", (inputX: Int, inputY: Int, inputZ: Int, minX: Int, maxX: Int, minY: Int, maxY: Int, minZ: Int, maxZ: Int) => HotcellUtils.calculateNeighbours(inputX, inputY, inputZ, minX, maxX, minY, maxY, minZ, maxZ))

  val neighbours = spark.sql(
  "select neighbours(h1.x, h1.y, h1.z, " + minX + ", " + maxX + ", " + minY + ", " + maxY + ", " + minZ + ", " + maxZ + ") as countNeighbours, " +
  "h1.x as x, h1.y as y, h1.z as z, sum(h2.hotCells) as countPickupPoints " +
  "from hotCells h1, hotCells h2 " +
  "where (h2.x = h1.x+1 or h2.x = h1.x or h2.x = h1.x-1) and " +
  "      (h2.y = h1.y+1 or h2.y = h1.y or h2.y = h1.y-1) and " +
  "      (h2.z = h1.z+1 or h2.z = h1.z or h2.z = h1.z-1) " +
  "group by h1.x, h1.y, h1.z " +
  "order by h1.z, h1.y, h1.x"
  )
  neighbours.createOrReplaceTempView("neighbours")
  // neighbours.show()

  spark.udf.register("gScore", (countNeighbours: Int, countPickupPoints: Int, numCells: Int, x: Int, y: Int, z: Int, mean: Int, sd: Int) => HotcellUtils.calculateGScore(countNeighbours, countPickupPoints, numCells, x, y, z, mean, sd))
 
  val gScores = spark.sql(
  "select gScore(countNeighbours, countPickupPoints, " + numCells + ", x, y, z, " + mean + ", " + sd + ") as gOCalculation, x, y, z " +
  "from neighbours " +
  "order by gOCalculation desc"
)
  gScores.createOrReplaceTempView("gScores")
  // gScores.show()

  val resultInfo = spark.sql("select x, y, z from gScores")
  resultInfo.createOrReplaceTempView("finalResults")
  // resultInfo.show()

  // YOU NEED TO CHANGE THIS PART
  return resultInfo  
  }
}
