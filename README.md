# Geospatial-Hot-Spot-Analysis

## About
This project performs numerous spatial queries on a large database containing geographic information and the real-time locations of customers from a well-known taxi company.

### Hot zone analysis
A range join operation is performed on rectangle datasets and a point dataset. For each rectangle, the number of points located within it is determined. A "hotter" rectangle includes more points. This task calculates the hotness of all rectangles based on the number of points they contain.

### Hot cell analysis

#### Description
This analysis focuses on applying spatial statistics to spatio-temporal big data to identify statistically significant spatial hot spots using Apache Spark. The concept is derived from the ACM SIGSPATIAL GISCUP 2016.

Problem Definition: [http://sigspatial2016.sigspatial.org/giscup2016/problem](http://sigspatial2016.sigspatial.org/giscup2016/problem) 

Submit Format: [http://sigspatial2016.sigspatial.org/giscup2016/submit](http://sigspatial2016.sigspatial.org/giscup2016/submit)

#### Special requirement (different from GIS CUP)
This analysis implements a Spark program to calculate the Getis-Ord statistic on NYC Taxi Trip datasets, termed "**Hot cell analysis**". To reduce computational power needs, the following modifications have been made: 

To reduce the computation power needï¼Œwe made the following changes:

1. **Input Data**: Monthly taxi trip datasets from 2009 to 2012 (e.g., yellow_tripdata_2009-01_point.csv, yellow_tripdata_2010-02_point.csv).
2. **Cell Unit Size**: Each cell is 0.01° latitude by 0.01° longitude.
3. **Time Step Size**: 1 day. The first day of a month is step 1, with every month having 31 days.
4. **Consideration**: Only Pick-up Locations are considered.
5. **Similarity Check**: Jaccard similarity is not used to verify your answers.
Note: The code template generates cell coordinates. You only need to implement the remaining parts of the task.

### Input parameters example

```
test/output hotzoneanalysis src/resources/point-hotzone.csv src/resources/zone-hotzone.csv hotcellanalysis src/resources/yellow_tripdata_2009-01_point.csv
```

### Input data format

#### Hot zone analysis
The input point data can be any small subset of NYC taxi dataset.

#### Hot cell analysis
The input point data is a monthly NYC taxi trip dataset (2009-2012) like "yellow\_tripdata\_2009-01\_point.csv"

### Output data format

#### Hot zone analysis
All zones with their count, sorted by "rectangle" string in an ascending order. 

```
"-73.795658,40.743334,-73.753772,40.779114",1
"-73.797297,40.738291,-73.775740,40.770411",1
"-73.832707,40.620010,-73.746541,40.665414",20
```


#### Hot cell analysis
The coordinates of top 50 hotest cells sorted by their G score in a descending order. Note, DO NOT OUTPUT G score.

```
-7399,4075,15
-7399,4075,29
-7399,4075,22
```
