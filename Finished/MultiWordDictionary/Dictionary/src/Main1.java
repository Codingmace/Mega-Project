
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.Hashtable;
import java.util.Scanner;

/**
 *
 * @author MasterWard
 */
public class Main1 {

    public static void main(String[] args) throws FileNotFoundException {
        File wordDirectory = new File("./words");
        FilenameFilter txtFilter = (File f1, String name) -> name.endsWith(".txt");
        File wordPath[] = wordDirectory.listFiles(txtFilter);
        Hashtable<String, Integer> dictionary = new Hashtable<>();
        for (File a : wordPath) {
            try {
                // Puts to read only to speed up maybe???
                //a.setReadOnly();
                // System.out.println(a.getPath() + " is set to readOnly");
                Scanner scan = new Scanner(a);
                while (scan.hasNextLine()) {
                    String curName = scan.nextLine().trim();
//                    System.out.println(curName);
                    Integer b = dictionary.get(curName);
                    if (b == null) { // Needs to be put in
                        dictionary.put(curName, 1);
                    } else { // It contains the value
                        dictionary.replace(curName, b + 1);
                    }
//                    dictionary.get(scan.nextLine().trim());
                }
                System.out.println("The table size is: " + dictionary.size());

                System.out.println(a.getName() + " has been read");
                scan.close();
            } catch (Exception e) {
                System.out.println(a.getName() + " is a troubling file");
                System.out.println(e.getMessage());
                return;
            } finally {
                System.out.println();
            }
        }
        FileWriter fw; // Skip for now
        // Speed up for the next time... Adds the frequency once sorted 
        /* Update can add Sorting based on numbers then sort that based on alphabetically */
        boolean consolidate = false; // To write the mega file
        if (consolidate) {
            try {
                fw = new FileWriter(new File("WordCount.txt"));

                // The File contains word space number of frequency appears
                for (String ns : dictionary.keySet()) {
                    fw.write(ns + " " + dictionary.get(ns) + "\r\n");
//                System.out.print(ns + " ");
//                System.out.println(dictionary.get(ns));
                }
                fw.flush();
                fw.close();
            } catch (Exception e) {
                System.out.println("There is an error of " + e.getMessage());
            }
        }
        File q = new File("./Definitions");
        File qp[] = q.listFiles(txtFilter);
        for (File h : qp) {
            Scanner scan = new Scanner(h); // Could potentially add Try-catch
            while (scan.hasNextLine()) {
                String word = scan.next().trim();
                /* 2 Options Words or Prefix */
                String p = scan.nextLine().trim();
                if (p.contains("(") && p.contains(")")) {
                    String pos = p.substring(p.indexOf("("), p.indexOf(")") + 1).trim();
                    String def = p.substring(p.indexOf(")") + 1).trim();
//                    System.out.println("POS: " + pos);
//                    System.out.println("DEF: " + def);
                } else {
                    System.out.println(p);
                }
                // scan.nextLine();
            }
            System.out.println("Read through the file " + h.getName());
        }
        // Need to store the definitions into a hash table
        // Reach it by getting the hash
        // Check to see if it is a word first
    }
}
