from bson.binary import Binary
import cv2
import gridfs
import numpy as np
import pymongo
import requests
import storm


DOWNLOAD_TRIES = 5
DISPLAY_IMAGES = False # change to True to allow cv2.imshow() display images


def display_image(content):
    display_image.counter = display_image.counter+1 if hasattr(display_image, 'counter') else 0

    def load_img(content, dtype):
        try:
            img = cv2.imdecode(np.fromstring(content, dtype=dtype, sep=""), cv2.CV_LOAD_IMAGE_COLOR)
        except:
            return None
        return img

    for dtype in ['int16', 'int8']:
        img = load_img(content, dtype)
        if not img is None:
            cv2.imshow(str(display_image.counter), img)
            cv2.waitKey(0)
            break


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
            if DISPLAY_IMAGES:
                display_image(image['content'])
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
                        'content': Binary(response.content),
                        'last_occurrence': timestamp,
                        'occurrences': [timestamp],
                    }
                    images_collection.insert(image)
                    if DISPLAY_IMAGES:
                        display_image(image['content'])
                    storm.emit([url, timestamp])
                    return


DownloaderBolt().run()
