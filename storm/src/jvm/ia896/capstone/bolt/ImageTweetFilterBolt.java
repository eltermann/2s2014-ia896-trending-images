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

import java.util.Map;


/**
 * For each tweet, determines whether it contains an image.
 * If so, emits its URL.
 */
public class ImageTweetFilterBolt extends BaseRichBolt {
  OutputCollector _collector;

  @Override
  public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
    _collector = collector;
  }

  @Override
  public void execute(Tuple tuple) {
    //Status status = DataObjectFactory.createStatus(tuple.getString(0));
    Status status = (Status) tuple.getValue(0);
    for (MediaEntity mediaEntity : status.getMediaEntities()) {
      if (mediaEntity.getType().equals("photo")) {
        _collector.emit(tuple, new Values(mediaEntity.getMediaURL()));
        _collector.ack(tuple);
      }
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer ofd) {
    ofd.declare(new Fields("url"));
  }
}
