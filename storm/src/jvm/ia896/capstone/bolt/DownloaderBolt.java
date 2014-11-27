package ia896.capstone.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.task.ShellBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.IRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;


/**
 * Check if image is already downloaded. If not, download it.
 */
public class DownloaderBolt extends ShellBolt implements IRichBolt {

  public DownloaderBolt() {
    super("python", "downloader.py");
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
