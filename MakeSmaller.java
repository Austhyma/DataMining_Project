import java.util.*;
import java.io.*;

public class MakeSmaller {
  
  public static ArrayList<String> makeSmaller(ArrayList<String> oldLines, int num) {
    ArrayList<String> newLines = new ArrayList<String>();
    for (int i = 0; i < num; i++) {
      int randomNumber = (int) Math.round(Math.random() * (oldLines.size()-1));
      newLines.add(oldLines.get(randomNumber));
    }
    return newLines;
  }
  
  public static void main(String[] args) throws IOException {
    BufferedReader file = null;
    try {
      file = new BufferedReader(new FileReader(args[0]));
    }
    catch (Exception e) {
    }
    ArrayList<String> lines = new ArrayList<String>();
    String line = file.readLine();
    int count = 0;
    while (count < 100000) {
      count++;
      System.out.println(count);
      lines.add(line);
      line = file.readLine();
    }
    ArrayList<String> lessLines = makeSmaller(lines, Integer.parseInt(args[1]));
    PrintWriter outFile = new PrintWriter(new FileWriter(args[1] + "samples.data"));
    for (int i = 0; i < lessLines.size(); i++) {
      outFile.println(lessLines.get(i));
    }
    outFile.close();
    file.close();
  }
}