#!/bin/bash

source twitter_credentials.sh

VERSION=ia896*dependencies.jar

storm jar target/$VERSION ia896.capstone.TrendingImages $CONSUMER_KEY $CONSUMER_SECRET $ACCESS_TOKEN_KEY $ACCESS_TOKEN_SECRET

