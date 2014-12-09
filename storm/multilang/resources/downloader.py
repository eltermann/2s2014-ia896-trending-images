import os
import sys

from bson.binary import Binary
import numpy as np
import pHash
import pymongo
import random
import requests
import storm


DOWNLOAD_TRIES = 5


def get_phash(content):
    # the library receives a filepath, so we can't convert directly from memory
    tmp_filename = '/tmp/capstone_hash_%s' % (random.randint(0, sys.maxint))
    f = open(tmp_filename, 'w')
    f.write(content)
    f.close()
    imghash = pHash.imagehash(tmp_filename)
    os.remove(tmp_filename)
    return imghash


class DownloaderBolt(storm.BasicBolt):
    def process(self, tup):
        url = tup.values[0]
        timestamp = tup.values[1]

        conn = pymongo.Connection('localhost', 27017)
        images_collection = conn['ia896']['twitter_images']

        # check if image is downloaded; if not, download it
        image = images_collection.find_one(url)
        if image:
            # image already downloaded; update its record
            image['last_occurrence'] = timestamp

            if 'occurrences' in image and isinstance(image['occurrences'], list):
                image['occurrences'].append(timestamp)
            else:
                image['occurrences'] = [timestamp]

            images_collection.update({'_id': image['_id']}, image)
            storm.emit([url, timestamp])
            return
        else:
            # try N times to download the image
            tries = 0
            while tries < DOWNLOAD_TRIES:
                tries += 1
                response = requests.get(url)
                if response.status_code == 200:
                    image = {
                        '_id': url,
                        'phash': str(get_phash(response.content)),
                        'first_occurrence': timestamp,
                        'last_occurrence': timestamp,
                        'occurrences': [timestamp],
                    }
                    images_collection.insert(image)
                    storm.emit([url, timestamp])
                    return


DownloaderBolt().run()
