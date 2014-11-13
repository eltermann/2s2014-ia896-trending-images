import time

import logging
import pymongo
from twitter import OAuth, TwitterStream

from timgs.settings import mongodb_credentials
from timgs.settings import twitter_credentials

logging.basicConfig(format='[logging] %(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)

def collect_tweets():
    while True:
        # we must be able to restart after any fail

        try:
            # connect to database
            dbconn = pymongo.Connection(mongodb_credentials.HOST, mongodb_credentials.PORT)
            dbcollection = dbconn['ia896']['raw_tweets']
            logging.info('Database connection [OK]')

            # connect to twitter api
            auth = OAuth(
                consumer_key=twitter_credentials.CONSUMER_KEY,
                consumer_secret=twitter_credentials.CONSUMER_SECRET,
                token=twitter_credentials.ACCESS_TOKEN_KEY,
                token_secret=twitter_credentials.ACCESS_TOKEN_SECRET
            )
            publicstream = TwitterStream(auth=auth)
            iterator = publicstream.statuses.sample()
            logging.info('Twitter API connection [OK]')

            # let it flow in
            for tweet in iterator:
                try:
                    if tweet['entities']['media'][0]['type'] == 'photo':
                        # tweet contains an image; insert it
                        dbcollection.insert(tweet)
                except:
                    # if tweet doesn't contain a image, it will break the references and
                    # an exception will be thrown; just ignore this tweet
                    continue

        except Exception as e:
            logging.info('Something failed: %s', repr(e))
            logging.info('Retrying in a minute...')
            time.sleep(60)
