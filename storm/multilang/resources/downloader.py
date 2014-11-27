from bson.binary import Binary
import gridfs
import pymongo
import requests
import storm

DOWNLOAD_TRIES = 5

class DownloaderBolt(storm.BasicBolt):
    def process(self, tup):
        url = tup.values[0]
        conn = pymongo.Connection('localhost', 27017)
        collection = conn['ia896']['twitter_images']

        if collection.find_one(url):
            storm.logInfo('downloader hit')
            storm.emit(tup.values)
            return
        else:
            storm.logInfo('downloader miss...')
            # try N times to download the image
            tries = 0
            while tries < DOWNLOAD_TRIES:
                tries += 1
                response = requests.get(url)
                storm.logInfo('downloading...')
                if response.status_code == 200:
                    doc = {
                        '_id': url,
                        'content': Binary(response.content),
                    }
                    collection.insert(doc)
                    storm.emit(tup.values)
                    return


DownloaderBolt().run()
