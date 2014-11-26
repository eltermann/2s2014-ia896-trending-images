package br.unicamp.ia896.tweetow;

import java.io.*;

public class Main {



    public void compareDirs(File dirA, File dirB) throws Exception {
        ImagePHash iph = new ImagePHash();

        File[] filesA = dirA.listFiles();
        File[] filesB = dirB.listFiles();

        System.out.println("# " + dirA.getName() + " & " + dirB.getName());
        for (File a : filesA) {
            String iphA = iph.getHash(new FileInputStream(a));
            for (File b : filesB) {
                String iphB = iph.getHash(new FileInputStream(b));

                int distance = iph.distance(iphA, iphB);

                String output = String.format("%s ~ %s = %d", a.getName(), b.getName(), distance);
                System.out.println(output);
            }
            System.out.println("");
        }

    }

    public static void main(String args[]) throws Exception {
        // Runtime.getRuntime().exec("pwd");
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

}
