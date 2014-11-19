#!/bin/bash

DIRS=(naointendo naointendo-left naointendo-right smile smile-left smile-right grayscale rotated2 rotated5 rotated10 rotated15)

for DIR in ${DIRS[*]}
do
  rm -rf $DIR
  mkdir -p $DIR
done


for fname in $(ls -1 orig/* | xargs -n1 basename)
do
  echo "Transforming: $fname"
  composite -gravity center naointendo.png orig/$fname naointendo/$fname
  composite -gravity west naointendo.png orig/$fname naointendo-left/$fname
  composite -gravity east naointendo.png orig/$fname naointendo-right/$fname

  composite -gravity center smile.gif orig/$fname smile/$fname
  composite -gravity west smile.gif orig/$fname smile-left/$fname
  composite -gravity east smile.gif orig/$fname smile-right/$fname

  convert -type Grayscale orig/$fname grayscale/$fname
  convert -rotate 2 orig/$fname rotated2/$fname
  convert -rotate 5 orig/$fname rotated5/$fname
  convert -rotate 10 orig/$fname rotated10/$fname
  convert -rotate 15 orig/$fname rotated15/$fname
done
