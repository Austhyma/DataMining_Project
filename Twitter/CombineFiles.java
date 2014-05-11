import java.io.*;
import java.util.*;

public class CombineFiles {
  public static ArrayList<String> extractLines(String number, boolean buzz) throws IOException {
    ArrayList<String> lines = new ArrayList<String>();
    String filename = (buzz) ? number + "samples/" + number + "-Buzz-Twitter-Absolute-Sigma-500.data" : number + "samples/" + number + "-NonBuzz-Twitter-Absolute-Sigma-500.data";
    BufferedReader file = null;
    try {
      file = new BufferedReader(new FileReader(filename));
    }
    catch (Exception e) {
      System.out.println("File does not exist");
    }
    String line = file.readLine();
    int count = 0; 
    while (line != null) {
      lines.add(line);
      line = file.readLine();
    }
    file.close();
    return lines;
  }
  
  public static void main(String[] args) throws IOException {
    ArrayList<String> allLines = extractLines(args[0], true);
    allLines.addAll(extractLines(args[0], false));
    PrintWriter outFile = new PrintWriter (new FileWriter(args[0] + "samples.data"));
    for (int i = 0; i < allLines.size(); i++) {
      outFile.println(allLines.get(i));
    }
    System.out.println(allLines.size());
    outFile.close();
  }
}