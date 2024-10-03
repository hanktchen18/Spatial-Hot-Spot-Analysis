package cse512

object HotzoneUtils {

  def ST_Contains(queryRectangle: String, pointString: String ): Boolean = {
    // YOU NEED TO CHANGE THIS PART
    var pt = pointString.split(",")
    var pt_x = pt(0).trim.toDouble
    var pt_y = pt(1).trim.toDouble

    var rect = queryRectangle.split(",")
    var rect_x1 = rect(0).trim.toDouble
    var rect_y1 = rect(1).trim.toDouble
    var rect_x2 = rect(2).trim.toDouble
    var rect_y2 = rect(3).trim.toDouble
    
    var low_x = 0.0
    var high_x = 0.0
    if (rect_x1 > rect_x2) {
      high_x = rect_x1
      low_x = rect_x2
    } else {
      high_x = rect_x2
      low_x = rect_x1
    }

    var low_y = 0.0
    var high_y = 0.0
    if (rect_y1 > rect_y2) {
      high_y = rect_y1
      low_y = rect_y2
    } else {
      high_y = rect_y2
      low_y = rect_y1
    }

    if (pt_x > high_x || pt_x < low_x || pt_y > high_y || pt_y < low_y){
      return false
    } else {
      return true
    }
  }
}
