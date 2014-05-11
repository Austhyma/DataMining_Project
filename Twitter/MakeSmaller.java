import java.util.*;
import java.io.*;

public class MakeSmaller {
  public static final double BUZZ = 0.197;
  public static final double NONBUZZ = 0.803;
  
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
      System.out.println("File does not exist");
    }
    ArrayList<String> lines = new ArrayList<String>();
    String line = file.readLine();
    int count = 0;
    while (line != null) {
      lines.add(line);
      line = file.readLine();
      count++;
    }
    int num = (int) Math.round(Double.parseDouble(args[1])*NONBUZZ);
    System.out.println(num);
    ArrayList<String> lessLines = makeSmaller(lines, num);
    PrintWriter outFile = new PrintWriter(new FileWriter(args[1] + "-" + args[0]));
    for (int i = 0; i < lessLines.size(); i++) {
      outFile.println(lessLines.get(i));
    }
    outFile.close();
    file.close();
  }
}