package br.unicamp.ia896.tweetow;

import br.unicamp.ia896.tweetow.comm.IDataReceiver;
import br.unicamp.ia896.tweetow.comm.MessageServer;
import br.unicamp.ia896.tweetow.comm.MessageServerCallback;
import br.unicamp.ia896.tweetow.comm.MessageServerImpl;
import br.unicamp.ia896.tweetow.phash.ImagePHash;

import java.io.*;

public class Main {

    public void compareDirs(File dirA, File dirB) throws Exception {
        ImagePHash iph = new ImagePHash();

        File[] filesA = dirA.listFiles();
        File[] filesB = dirB.listFiles();

        long hashStart, hashStop;
        long distanceStart, distanceStop;

        System.out.println("# " + dirA.getName() + " & " + dirB.getName());
        for (File a : filesA) {
            String iphA = iph.getHash(new FileInputStream(a));
            for (File b : filesB) {
                hashStart = System.currentTimeMillis();
                String iphB = iph.getHash(new FileInputStream(b));
                hashStop = System.currentTimeMillis();

                distanceStart = System.currentTimeMillis();
                int distance = iph.distance(iphA, iphB);
                distanceStop = System.currentTimeMillis();

                String output = String.format("%s ~ %s = %d \t\t hash time: %dms, distance time: %dms",
                        a.getName(), b.getName(), distance,
                        hashStop - hashStart, distanceStop - distanceStart);
                System.out.println(output);
            }
            System.out.println("");
        }

    }

    public void test(String[] args) throws Exception {
        ImagePHash iph = new ImagePHash();
        // argh, text mode plz! String = javax.swing.JOptionPane.showInputDialog(null, "Image path...");

        if (args.length > 1) {
            // String distance = "" + iph.distance(args[0], args[1]);
            new Main().compareDirs(new File(args[0]), new File(args[1]));
            // System.out.println(distance);
        } else {
            String phash = iph.getHash(new FileInputStream(new File(args[0])));
            System.out.println(phash);
        }
    }

    public void fireInTheHole(int port, boolean debug) {
        System.out.println("* Using port " + port + ", debug is " + debug);

        MessageServer server = new MessageServerImpl(port);
        server.setCallback(new CallBackImpl(server, debug));
        server.start();
    }

    public static void main(String args[]) throws Exception {
        System.out.println("-= TweeTow v0.1 =-");
        int port = 10101;
        boolean debug = false;

        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
                if (args.length > 1)
                    debug = "true".equalsIgnoreCase(args[1]);

            } catch (Exception e) {
                System.out.println("Usage:\njava -jar tweetow.jar [port] [debug<true/false>]\n\nDefault port is 10101.");
                return;
            }
        }

        new Main().fireInTheHole(port, debug);
    }

    class CallBackImpl implements MessageServerCallback {

        MessageServer server;
        ImagePHash pHash = new ImagePHash();
        boolean debug;

        public CallBackImpl(MessageServer server, boolean debug) {
            this.server = server;
            this.debug = debug;
        }

        @Override
        public void messageReceived(IDataReceiver source, String message) {
            String[] parts = message.split("[ ]");

            if (parts.length < 2) {
                error(source);
                source.close();
                return;
            }


            try {
                String command = parts[0];
                if ("hash".equalsIgnoreCase(command)) {
                    calculePHash(source, parts[1]);
                } else if ("compare".equalsIgnoreCase(command) && parts.length == 3) {
                    compareHashes(source, parts[1], parts[2]);
                    return;
                } else if ("all".equalsIgnoreCase(command) && parts.length == 3) {
                    // TODO get hash of 2 files and compare

                } else if ("exit".equalsIgnoreCase(command)) {
                    server.stop();
                    System.out.println("Farewell...");
                } else {
                    error(source);
                }
            } catch (Exception e) {
                // TODO log
            } finally {
                source.close();
            }
        }

        private void compareHashes(IDataReceiver source, String hashA, String hashB) {
            try {
                int result = pHash.distance(hashA, hashB);
                source.sendMessage(String.valueOf(result));
            } catch (Exception e) {
                // TODO log
                error(source);
            }
        }

        private void calculePHash(IDataReceiver source, String filePath) throws Exception {
            File file = new File(filePath);
            if (file.exists()) {
                long start = System.currentTimeMillis();
                FileInputStream in = new FileInputStream(file);
                String hash = pHash.getHash(in);
                long end = System.currentTimeMillis();
                if (debug) {
                    // TODO USE LOG
                    System.out.println("* phash calculation time: " + (end-start) + "ms");
                }


                source.sendMessage(hash);
            } else {
                error(source);
            }
        }

        private void error(IDataReceiver source) {
            try {
                source.sendMessage("error");
            } catch (Exception e) {
                // TODO log
            }

        }

    }


}
