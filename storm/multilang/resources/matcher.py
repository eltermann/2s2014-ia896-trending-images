import pHash
import pymongo
import storm
import time


DOWNLOAD_TRIES = 5
MATCHSET_WINDOW = 86400000 # 24h in miliseconds
DISTANCE_THRESHOLD = 2 # pHash distance considered OK


def ia896_capstone_calculate_dist(img1, img2):
    hash1 = long(img1['phash'])
    hash2 = long(img2['phash'])
    if not hash1 or not hash2:
        return 9999
    return pHash.hamming_distance(hash1, hash2)


class MatcherBolt(storm.BasicBolt):
    def process(self, tup):
        url = tup.values[0]
        timestamp = tup.values[1]
        conn = pymongo.Connection('172.31.26.27', 27017)
        images_collection = conn['ia896']['twitter_images']
        matches_collection = conn['ia896']['twitter_matches']

        image = images_collection.find_one(url)
        if not image:
            # something's wrong but there's nothing we can do now
            return

        if 'matchset' in image and image['matchset']:
            # just increment occurrence on matchset
            matchset = matches_collection.find_one(image['matchset'])
            if not matchset:
                # something's wrong but there's nothing we can do now
                return
            if not 'occurrences' in matchset or not isinstance(matchset['occurrences'], list):
                matchset['occurrences'] = []
            matchset['occurrences'].append(timestamp)
            matchset['last_occurrence'] = timestamp
            matches_collection.update({'_id': matchset['_id']}, matchset)
        else:
            # image is not yet bound to a matchset: find an existing matchset
            # that properly fits for this image or create a new matchset
            now = 1000 * int(time.time())
            found_matchset = False
            for matchset in matches_collection.find({'last_occurrence': {'$gte': now - MATCHSET_WINDOW}}):
                if 'representative' in matchset:
                    matchset_representative = images_collection.find_one(matchset['representative'])
                    if matchset_representative:
                        # compare current image to matchset representative
                        dist = ia896_capstone_calculate_dist(image, matchset_representative)
                        if dist <= DISTANCE_THRESHOLD:
                            # we found a proper matchset for this image
                            found_matchset = True
                            break

            if found_matchset:
                # update existing matchset
                if not 'occurrences' in matchset or not isinstance(matchset['occurrences'], list):
                    matchset['occurrences'] = []
                matchset['occurrences'].append(timestamp)
                if not 'images' in matchset or not isinstance(matchset['images'], list):
                    matchset['images'] = []
                matchset['images'].append(url)
                matchset['last_occurrence'] = timestamp
                matches_collection.update({'_id': matchset['_id']}, matchset)
                images_collection.update({'_id': url}, {'$set': {'matchset': matchset['_id']}})
            else:
                # create a matchset for this image
                matchset = {
                    'representative': url,
                    'occurrences': [timestamp],
                    'images': [url],
                    'first_occurrence': timestamp,
                    'last_occurrence': timestamp,
                }
                matchset_id = matches_collection.insert(matchset)
                images_collection.update({'_id': url}, {'$set': {'matchset': matchset_id}})


MatcherBolt().run()
