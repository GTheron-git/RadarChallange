# Radar Challenge

Provide a markdown file containing ASCII images similar to what you see in `data/` that will be read on startup.
Inside the file there must be two sections, one containing a list of images (invaders) and one containing a list of 
images (radar images) that may contain one or more of the invaders and also some noise.

This application attempts to locate and identify the most likely matches of invaders inside the radar images, 
and prints then in a log. If an invader is identified then it will be overlaid in the position it is found 
with the character representation of its image id (assigned upon loading). 

The  application can be extended to take invader/radar images via a HTTP binding of your choice and respond with the 
scanned radar image.

## Dependencies

 * Maven
 * Git
 * Java 8

## Setup

in src/main/resources/application.properties you will find a few settings. You can keep all these defaulted.

- File name to load images from
`constants.filename=./data/SpaceInvaders_2.0.md`

- Text to scan for in the document to identify a section conatining invaders
`constants.invaderSectionIdentifier=Known space invaders`

- Text to scan for in the document to identify radar images
`constants.radarSectionIdentifier=Example radar image`

- Cutoff correlation (as percentage/100) in radar image where an invader is ignored
`constants.cutoffMatchDensity=0.4`

- Minimum correlation (as percentage/100) in radar image where an invader is considered present
`constants.acceptedMatchRatioForHit=0.75`

## Running project

To run the application locally, start from the main project folder. 

```bash
mvn clean package -DskipTests spring-boot:run
```

To run the tests do:

```bash
mvn clean verify
```

Note on testing: Due to the statistical nature of this problem the main test is composed of 20 runs and is
considered successful if there is a perfect match between the predicted and actual invader ids and positions
70% of the rest runs.

If the test fails, rerun it, it will pass some times and fail some times.

## Info on matching method

Firstly areas of high correlation between invader images and radar images are identified.
Each invader images is swept over a radar image and all points where there is a 'o' on both radar and invader image 
indicate a correlation. Areas with little or no correlation are ignored. the result is a correlation image.

This image is then fed through a mean-shift clustering algorithm which identifies possible sub-areas inside areas of
high correlation, enumerates each sub area and assigns a cluster id to each point based on its nearest cluster centroid.

For each cluster, the scanning process in the first step is repeated and the most likely candidate invader image is 
identified. This invader image is then subtracted from the original image and rescanned to find possible other hits.

The result is a list of points with their respective probability of being a complete hit and the invader image for 
this point. These points are used to overlay the original invader images onto the original radar image with 'o' being 
replaced by the invader image id.

(note that, if there more than ten invader in an image then the picture will not be easily viewed due to skewness.) 