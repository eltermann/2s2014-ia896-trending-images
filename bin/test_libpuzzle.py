#!/usr/bin/python

import os
import sys
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))

import csv
import logging
import pymongo
import pypuzzle

from timgs.settings import mongodb_credentials

FILES_BASEDIR = os.path.join(os.path.dirname(__file__), '../../dados')
VARIANTS = ['naointendo', 'smile', 'grayscale']

logging.basicConfig(format='[logging] %(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)


if __name__ == '__main__':
    with open('libpuzzle.csv', 'w') as f:
        # connect to database
        dbconn = pymongo.Connection(mongodb_credentials.HOST, mongodb_credentials.PORT)
        dbcollection = dbconn['ia896']['raw_tweets']

        puzzle = pypuzzle.Puzzle()
        csvwriter = csv.writer(f, delimiter=';')
        csvwriter.writerow(['id'] + VARIANTS)

        total = 1000#dbcollection.count()
        count = 0
        rowid = 1

        logging.info('Starting libpuzzle test')
        for tweet in dbcollection.find().limit(1000):
            count += 1
            if count % 100 == 0:
                logging.info('%s%%', float(100*count) / total)

            imgurl = tweet['entities']['media'][0]['media_url']
            filename = imgurl.split('/')[-1]

            orig_filepath = os.path.join(FILES_BASEDIR, 'orig', filename)

            distances = []
            for variant in VARIANTS:
                filepath = os.path.join(FILES_BASEDIR, variant, filename)
                if os.path.isfile(filepath):
                    # compute image hash
                    distances.append(puzzle.get_distance_from_file(orig_filepath, filepath))

            if len(distances) == len(VARIANTS):
                csvwriter.writerow([rowid] + distances)
                rowid += 1

        f.close()
        logging.info('Libpuzzle complete')
