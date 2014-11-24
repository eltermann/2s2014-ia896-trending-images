package ia896.capstone.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;


/**
 * For each tweet, determines whether it contains an image.
 * If so, emits its URL.
 */
public class DownloaderBolt extends BaseRichBolt {
  OutputCollector _collector;

  @Override
  public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
    _collector = collector;
  }

  @Override
  public void execute(Tuple tuple) {
    //String url = tuple.getString(0)
    // TODO - check and download
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer ofd) {
    //ofd.declare(new Fields("url"));
    // TODO
  }
}
