import pymongo
import storm
import time


DOWNLOAD_TRIES = 5
MATCHSET_WINDOW = 1800000 # 30 min
DISTANCE_THRESHOLD = 0.1 # arbitrary for now


def ia896_capstone_calculate_dist(img1, img2):
    # for now, return distance=0 if image is exactly the same, and 1 otherwise
    if img1['content'] == img2['content']:
        return 0
    else:
        return 1


class MatcherBolt(storm.BasicBolt):
    def process(self, tup):
        url = tup.values[0]
        timestamp = tup.values[1]
        conn = pymongo.Connection('localhost', 27017)
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
                    'last_occurrence': timestamp,
                }
                matchset_id = matches_collection.insert(matchset)
                images_collection.update({'_id': url}, {'$set': {'matchset': matchset_id}})


MatcherBolt().run()
