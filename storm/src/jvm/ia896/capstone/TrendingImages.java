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
import java.util.HashMap;
import java.util.Map;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.AuthorizationException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

import ia896.capstone.bolt.DownloaderBolt;
import ia896.capstone.bolt.RecurringImageFilterBolt;
import ia896.capstone.bolt.ImageFilterBolt;
import ia896.capstone.bolt.MatcherBolt;
import ia896.capstone.bolt.PrinterBolt;
import ia896.capstone.spout.TwitterSampleSpout;


public class TrendingImages {
    public static void main(String[] args) {

        boolean extremeGoHorseModeOn = true;

        String consumerKey, consumerSecret, accessToken, accessTokenSecret;
        String[] arguments, keyWords;

        if (extremeGoHorseModeOn) {
            consumerKey = "0lnz3Rd444lMhGpGW9FHhA";
            consumerSecret = "07HIW8D60YyFCBC419cyCv4tbsyqcVZ5sEshfrA";
            accessToken = "1514119184-eexlGEtbdD0ks4liTs9DBijoi2KwPvvWua8TfDW";
            accessTokenSecret = "9LtsZg7skbJ3SPDnqPKOvUOQtqDxlMq6H9M0CKym0";
            arguments = new String[] {};
            keyWords = new String[] {};
        } else {
            consumerKey = args[0];
            consumerSecret = args[1];
            accessToken = args[2];
            accessTokenSecret = args[3];
            arguments = args.clone();
            keyWords = Arrays.copyOfRange(arguments, 4, arguments.length);
        }



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
        builder.setBolt("downloader", new DownloaderBolt())
                .shuffleGrouping("recurrent-img-filter");

        // 5- downloader -> matcher
        builder.setBolt("matcher", new MatcherBolt())
                .shuffleGrouping("downloader");


        boolean runOnServer = true;

        if (runOnServer) {
            System.out.println("* Running on server...");
            try {
                pleaseRunOnRemoteCluster(builder);
            } catch (Exception e) {
                System.out.println("Ops... " + e.getMessage());
            }
            return;
        }

        Config conf = new Config();
        //conf.setDebug(true);

        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("ia896-topology", conf, builder.createTopology());

        Utils.sleep(99999999);
        cluster.shutdown();
    }

    /**
     * Gently asks the server to run this "old evil spirit" remotely.
     *
     * MUN-HAAAAAAAA!
     */
    static void pleaseRunOnRemoteCluster(TopologyBuilder builder) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        // Map conf = new HashMap();
        // conf.put(Config.TOPOLOGY_WORKERS, 4);

        Config conf = new Config();
        conf.setNumWorkers(20);
        conf.setMaxSpoutPending(5000);

        StormSubmitter.submitTopology("ia896-ftw", conf, builder.createTopology());
    }
}
