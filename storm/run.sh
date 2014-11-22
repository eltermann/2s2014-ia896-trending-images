#!/bin/bash

source twitter_credentials.sh
storm jar target/ia896-capstone-*-jar-with-dependencies.jar ia896.capstone.TrendingImages $CONSUMER_KEY $CONSUMER_SECRET $ACCESS_TOKEN_KEY $ACCESS_TOKEN_SECRET
