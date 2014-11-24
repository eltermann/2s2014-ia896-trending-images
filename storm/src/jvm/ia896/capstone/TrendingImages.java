/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ia896.capstone;

import java.util.Arrays;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

import ia896.capstone.bolt.DownloaderBolt;
import ia896.capstone.bolt.RecurringImageFilterBolt;
import ia896.capstone.bolt.ImageFilterBolt;
import ia896.capstone.bolt.PrinterBolt;
import ia896.capstone.spout.TwitterSampleSpout;


public class TrendingImages {
    public static void main(String[] args) {
        String consumerKey = args[0]; 
        String consumerSecret = args[1]; 
        String accessToken = args[2]; 
        String accessTokenSecret = args[3];
        String[] arguments = args.clone();
        String[] keyWords = Arrays.copyOfRange(arguments, 4, arguments.length);
        
        TopologyBuilder builder = new TopologyBuilder();
        
        // 1- spout
        builder.setSpout("twitter", new TwitterSampleSpout(consumerKey, consumerSecret,
                                accessToken, accessTokenSecret, keyWords));

        // 2- spout -> images filter
        builder.setBolt("img-filter", new ImageFilterBolt())
                .shuffleGrouping("twitter");

        // 3- images filter -> recurrent images filter
        builder.setBolt("recurrent-img-filter", new RecurringImageFilterBolt())
                .fieldsGrouping("img-filter", new Fields("url"));

        // TODO - testing
        builder.setBolt("print", new PrinterBolt())
                .shuffleGrouping("recurrent-img-filter");

        // 4- recurrent images filter -> downloader
        //builder.setBolt("downloader", new DownloaderBolt())
        //        .fieldsGrouping("recurrent-img-filter", new Fields("url"));

        Config conf = new Config();
        //conf.setDebug(true);
        
        
        LocalCluster cluster = new LocalCluster();
        
        cluster.submitTopology("ia896-topology", conf, builder.createTopology());
        
        Utils.sleep(99999999);
        cluster.shutdown();
    }
}
