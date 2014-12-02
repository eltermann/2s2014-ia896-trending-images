import storm
import time


global count
global recurring_imgs


class RecurringImageFilterBolt(storm.BasicBolt):
    def process(self, tup):
        global count
        global recurring_imgs

        RECURRING_WINDOW = 600000 # 10 minutes (in milliseconds)
        RECURRING_THRESHOLD = 2 # how many times the URL must reccur in `window` timeframe

        count += 1

        url = tup.values[0]
        timestamp = tup.values[1]
        now = 1000 * int(time.time())

        if count % 5000 == 0:
            # remove expired occurrences from memory
            count = 0
            for key, value in recurring_imgs.iteritems():
                recent_occurrences = [occurrence for occurrence in value if occurrence > now - RECURRING_WINDOW]
                if len(recent_occurrences) > 0:
                    recurring_imgs[key] = recent_occurrences
                else:
                    _ = recent_occurrences.pop(key, None)

        # add occurrence
        if not url in recurring_imgs or not isinstance(recurring_imgs[url], list):
            recurring_imgs[url] = []
        recurring_imgs[url].append(timestamp)

        # check if url is recurrent
        recurring_count = 0
        for occurrence in recurring_imgs[url]:
            if now - occurrence < RECURRING_WINDOW:
                recurring_count += 1

        if recurring_count >= RECURRING_THRESHOLD:
            storm.emit([url, timestamp])


count = 0
recurring_imgs = {}
RecurringImageFilterBolt().run()
