package ia896.capstone.bolt;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Date;
import java.util.Map;
import java.util.ArrayList; 
import java.util.HashMap; 


/**
 * For each image url, check if it's a recurring URL; if so, emit it.
 *
 * Recurring URLs detection algorith
 * =================================
 *
 * History information is kept in memory.
 * fieldsGrouping ensures that a recurring URL will hit the same thread.
 * `history` is a hash map that maps (String) `url` to (List) `lastOccurrences`
 * Expired occurrences are removed from `history` from times to times.
 * When an URL arrives, it's checked in `history` and, if enough occurrences
 * are found, the URL is emitted.
 */
public class RecurringImageFilterBolt extends BaseRichBolt {
  OutputCollector _collector;
  HashMap<String, ArrayList<Long>> history;
  int count;

  final int RECURRING_WINDOW = 600000; // 10 minutes (in seconds)
  final int RECURRING_THRESHOLD = 2; // how many times the URL must reccur in `window` timeframe

  @Override
  public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
    _collector = collector;
    history = new HashMap<String, ArrayList<Long>>();
    count = 0;
  }

  @Override
  public void execute(Tuple tuple) {
    count++;
    if (count % 10000 == 0) {
      // Clean expired occurrences
      // TODO

      count = 0; // avoid overflow
    }

    String url = tuple.getString(0);
    Long timestamp = (Long) tuple.getValue(1);

    // Update history
    ArrayList<Long> occurrences;
    occurrences = history.get(url);
    if (occurrences == null) {
      occurrences = new ArrayList<Long>();
    }
    occurrences.add(timestamp);
    history.put(url, occurrences);

    // Check if URL is recurrent
    int recurring_count = 0;
    Long now = System.currentTimeMillis() / 1000;
    for (Long occurrence : occurrences) {
      if (now - occurrence < RECURRING_WINDOW) {
        recurring_count++;
      }
    }

    if (recurring_count >= RECURRING_THRESHOLD) {
      _collector.emit(tuple, new Values(url, timestamp));
      _collector.ack(tuple);
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer ofd) {
    ofd.declare(new Fields("url"));
  }
}
