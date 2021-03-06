#!/usr/bin/env python

from datetime import datetime
from flask import Flask
from flask import request
import json
import pymongo


if __name__ == '__main__':

    # micro web-server
    app = Flask(__name__)
    app.config.update(dict(
        DEBUG=True#False
    ))
    app.config.from_envvar('FLASKR_SETTINGS', silent=True)

    @app.route('/top10', methods = ['GET'])
    def main():
        """
        Request specs
        GET; params:
          - from: 1414964402
          - to: 1417556402

        Response specs
          - status: {ok, nok}
          - data: list of matches, if status=ok; error msg if status=nok
        """
        ia896_json = lambda x: 'jsonCallback(%s);' % (json.dumps(x))

        try:
            time_from = 1000*long(request.args.get('from', ''))
            time_to = 1000*long(request.args.get('to', ''))
        except:
            return ia896_json({
                'status': 'nok',
                'data': 'invalid input `date` and `to`',
            })

        conn = pymongo.Connection('172.31.26.27', 27017)
        matches_collection = conn['ia896']['twitter_matches']

        start_t = datetime.now()
        ret = matches_collection.aggregate([
            # aggregation pipeline
            {'$match': {'$and': [{'first_occurrence': {'$lt': time_to}}, {'last_occurrence': {'$gt': time_from}}]}},
            {'$unwind': '$occurrences'},
            {'$match': {'$and': [{'occurrences': {'$gte': time_from}}, {'occurrences': {'$lte': time_to}}]}},
            {'$group': {'_id': '$_id', 'count': {'$sum': 1 }}},
            {'$sort': {'count': -1}},
            {'$limit': 10},
        ])
        end_t = datetime.now()

        if not 'ok' in ret or ret['ok'] != 1 or not 'result' in ret or len(ret['result']) == 0:
            return ia896_json({
                'status': 'nok',
                'data': 'query error',
            })

        resp = {
            'status': 'ok',
            'query_time_us': (end_t - start_t).microseconds,
            'data': [],
        }
        max_count = ret['result'][0]['count']
        for doc in ret['result']:
            matchset = matches_collection.find_one(doc['_id'])
            resp['data'].append({
                'id': str(doc['_id']),
                'imgs': matchset['images'],
                'absolute': doc['count'],
                'normalized': '%.3f' % (float(doc['count'])/max_count),
            })
        return ia896_json(resp)


    @app.route('/non-identical-matches', methods = ['GET'])
    def non_identical():
        conn = pymongo.Connection('172.31.26.27', 27017)
        matches_collection = conn['ia896']['twitter_matches']
        images_collection = conn['ia896']['twitter_images']

        output = ''
        count = 0
        for matchset in matches_collection.find():
            if count > 200:
                break
            if len(matchset['images']) > 0:
                phashes = [img['phash'] for img in images_collection.find({'matchset': matchset['_id']})]
                if len(set(phashes)) > 1:
                    count += 1
                    imgs = ['<img src="%s"></img>' % (url) for url in matchset['images']]
                    output += '<hr />' + ''.join(imgs)
        return output


    app.run(host='0.0.0.0', port=54321)
