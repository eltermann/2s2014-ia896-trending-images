package ia896.capstone.bolt;

import backtype.storm.task.ShellBolt;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;

import java.util.Map;


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
public class RecurringImageFilterBolt extends ShellBolt implements IRichBolt {

  public RecurringImageFilterBolt() {
    super("python", "recurring_filter.py");
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer ofd) {
    ofd.declare(new Fields("url", "timestamp"));
  }

  @Override
  public Map<String, Object> getComponentConfiguration() {
    return null;
  }
}
