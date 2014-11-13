import datetime
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
            publicstream = TwitterStream(auth=auth, timeout=60)
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


def update_summary():
    with open('../data/summary_ten2ten_min.csv', 'w') as ten2ten_f, open('../data/summary_hits.csv', 'w') as hits_f:
        dbconn = pymongo.Connection(mongodb_credentials.HOST, mongodb_credentials.PORT)
        dbcollection = dbconn['ia896']['raw_tweets']

        logging.info('Starting summary')
        total = dbcollection.count()
        count = 0
        freq = {}
        hits = {}
        for tweet in dbcollection.find():
            tenmin_id = int(tweet['timestamp_ms']) / 600000
            if not tenmin_id in freq:
                freq[tenmin_id] = 0
            freq[tenmin_id] += 1
            count += 1

            if not tweet['entities']['media'][0]['media_url'] in hits:
                hits[tweet['entities']['media'][0]['media_url']] = 0
            hits[tweet['entities']['media'][0]['media_url']] += 1

            if count % 10000 == 0:
                logging.info('%s%%', float(100*count) / total)

        ret = []
        for key, value in freq.iteritems():
            ret.append((
                key,
                value,
                datetime.datetime.fromtimestamp(600*key).strftime('%Y-%m-%d %H:%M:%S'),
            ))
        ret = sorted(ret, key=lambda tup:tup[0])

        hits_summ = {}
        for key, val in hits.iteritems():
            if not val in hits_summ:
                hits_summ[val] = {'count': 0, 'url': key}
            hits_summ[val]['count'] += 1

        ret_hits = []
        for key, value in hits_summ.iteritems():
            ret_hits.append((
                key,
                value['count'],
                value['url'] if value['count'] == 1 else '',
            ))
        ret_hits = sorted(ret_hits, key=lambda tup:tup[0])

        import csv
        csvwriter = csv.writer(ten2ten_f, delimiter=';')
        csvwriter.writerows(ret)
        csvwriter_hits = csv.writer(hits_f, delimiter=';')
        csvwriter_hits.writerows(ret_hits)
        ten2ten_f.close()
        hits_f.close()
        logging.info('Summary complete')
