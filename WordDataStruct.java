import java.util.ArrayList;

import java.io.*;

//DEPRECATED. Rather poor MachineLearning algorithm. Counts the number of high frequency words in spam comments, and high frequency words in healthy comments, 
//and outputs a file ready for logistic regression based on the scores. Best I got was 53% accuracy; bagofwords_analysis.py yielded 94% accuracy for the same data
public class WordDataStruct{
  static ArrayList<ArrayList<WordData>> big = new ArrayList<ArrayList<WordData>>();//the data structure to organize words in comments and collect frequencies. On the first level, the nth branch corresponds to the collection of words n characters long.
  static ArrayList<String[]> comments = new ArrayList<String[]>();//data structure used to preserve sentence structure for later scoring
  static ArrayList<Integer> spamData = new ArrayList<Integer>();
  static int listSize = 0;//value to be used later on for making an array of the appropriate size
  
  public static void main(String[] args){
    for (int i = 0; i < 51; i++){
      big.add(new ArrayList<WordData>());
    }
    try{
    BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\bzlis\\Documents\\YoutTube spam datasets\\Youtube-MASTER.txt"));
    String line = br.readLine();
    while ((line = br.readLine()) != null){//building tree to store WordData objects
      String[] cols = line.split("\t");
      String[] words = cols[0].split(" ");
      for (int j = 0; j < words.length; j++){
        String[] cleanedWords = CommentCleaner.cleanString(words[j]);
        if (j == 0){
        comments.add(cleanedWords);
        spamData.add(Integer.parseInt(cols[1]));
        }
        else{
          comments.set(comments.size()-1, combineString(comments.get(listSize), cleanedWords));
        }
        for (int k = 0; k < cleanedWords.length; k++){
          if (big.get(cleanedWords[k].length()-1).isEmpty()){
            big.get(cleanedWords[k].length()-1).add(new WordData(cleanedWords[k], Integer.parseInt(cols[1]), 1-Integer.parseInt(cols[1])));
          }
          else{
            int m = 0;
            while ((m < big.get(cleanedWords[k].length()-1).size()) && (big.get(cleanedWords[k].length()-1).get(m).word.equals(cleanedWords[k]) == false))
              m++;
            if ((m < big.get(cleanedWords[k].length()-1).size())){
              big.get(cleanedWords[k].length()-1).get(m).healthyCount+=(1-Integer.parseInt(cols[1]));
              big.get(cleanedWords[k].length()-1).get(m).spamCount+=Integer.parseInt(cols[1]);
            }
            else
              big.get(cleanedWords[k].length()-1).add(new WordData(cleanedWords[k], Integer.parseInt(cols[1]), 1-Integer.parseInt(cols[1])));
          }
        }
      }
            listSize++;
    }
    br.close();
 //   double sum = 0.0;
    
    //FOR OBTAINING THE SPAM AND HEALTHY COUNTS OF ALL WORDS IN THE INPUT FILE
    int[] spamScore = new int[listSize];//the array for storing the sum of spam word counts in a sentence
    int[] healthyScore = new int[listSize];//the array for storing the sum of healthy word counts in a sentence

    for (int i = 0; i < comments.size(); i++){//scoring sentences
            //  double temp = 0.0;
      for (int j = 0; j < comments.get(i).length; j++){

        for (int m = 0; m < big.get(comments.get(i)[j].length()-1).size(); m++){
          if (big.get(comments.get(i)[j].length()-1).get(m).word.equals(comments.get(i)[j])){
            if ((double)big.get(comments.get(i)[j].length()-1).get(m).spamCount/(big.get(comments.get(i)[j].length()-1).get(m).spamCount + big.get(comments.get(i)[j].length()-1).get(m).healthyCount) > .7)
              spamScore[i] += big.get(comments.get(i)[j].length()-1).get(m).spamCount;
            //temp+= (double)big.get(comments.get(i)[j].length()-1).get(m).spamCount/big.get(comments.get(i)[j].length()-1).get(m).totalCount;
            if ((double)big.get(comments.get(i)[j].length()-1).get(m).healthyCount/(big.get(comments.get(i)[j].length()-1).get(m).spamCount + big.get(comments.get(i)[j].length()-1).get(m).healthyCount) > .7)
            healthyScore[i] += big.get(comments.get(i)[j].length()-1).get(m).healthyCount;
          }
        }
      }
      spamScore[i] = (int)Math.sqrt(spamScore[i]);
      healthyScore[i] = (int)Math.sqrt(spamScore[i]);
    }
    
      //sum += temp/comments.get(i).length;
    //  spamScore[i] = spamScore[i]/comments.get(i).length;
      //healthyScore[i] = healthyScore[i]/comments.get(i).length;
      //scoreList[i+1] = Integer.parseInt(comments.get(i+1)[0]);
    
 //   System.out.println("Average score: " + sum/listSize);
    
//FOR GETTING AN EXCEL-READY LIST OF DATA
    for (int z = 0; z < listSize; z++){
      if ((spamScore[z] != 0) || (healthyScore[z] != 0))
      System.out.println(spamData.get(z)+"," +spamScore[z] + "," + healthyScore[z]+"," + comments.get(z).length);
    }
    
    /*FOR TESTING THE SCORES OF A SINGLE SENTENCE
    for (int q = 0; q < big.size(); q++){
      for (int r = 0; r < big.get(q).size(); r++){
        if (big.get(q).get(r).word.equals("like"))
          System.out.println("like: " + big.get(q).get(r).spamCount + ", " + big.get(q).get(r).healthyCount);
        if (big.get(q).get(r).word.equals("this"))
          System.out.println("this: " + big.get(q).get(r).spamCount + ", " + big.get(q).get(r).healthyCount);
        if (big.get(q).get(r).word.equals("song"))
          System.out.println("song: " + big.get(q).get(r).spamCount + ", " + big.get(q).get(r).healthyCount);
        if (big.get(q).get(r).word.equals("!"))
          System.out.println("!: " + big.get(q).get(r).spamCount + ", " + big.get(q).get(r).healthyCount);
      }
    }
   */
    
    // FOR ASSESSING NEW COMMENTS
    /*
    BufferedReader br2 = new BufferedReader(new FileReader("C:\\Users\\bzlis\\Documents\\YoutTube spam datasets\\Youtube01-Psy.txt"));
    String line2 = br2.readLine();
    int correct = 0;
    int total = 0;
    while ((line2 = br2.readLine()) != null){
      int testSpamWords = 0;
      int testHlthWords = 0;
      int numWordsOnLine = 0;
      String[] cols = line2.split("\t");
      String[] words = cols[0].split(" ");
      for (int j = 0; j < words.length; j++){
        String[] cleanedWords = CommentCleaner.cleanString(words[j]);
        numWordsOnLine += cleanedWords.length;
        for (int k = 0; k < cleanedWords.length; k++){
          int m = 0;
          while (m < big.get(cleanedWords[k].length()-1).size()){
            if (big.get(cleanedWords[k].length()-1).get(m).word.equals(cleanedWords[k])){
              testSpamWords+=big.get(cleanedWords[k].length()-1).get(m).spamCount;
              testHlthWords+=big.get(cleanedWords[k].length()-1).get(m).healthyCount;
            }
            m++;
          }
        }
      }
      if (closeEnough(getPr(testSpamWords, testHlthWords, numWordsOnLine), Integer.parseInt(cols[1])))
        correct++;
      total++;
    }
    System.out.println((double)correct/total);
    br2.close();
    */
    } catch(FileNotFoundException ex){
      System.out.println("Can't find file");
    } catch(IOException e){
      System.out.println("Cant read");
    }
  }
  
  public static String[] combineString(String[] first, String[] second){//not my code
        int length = first.length + second.length;
        String[] result = new String[length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
  
  public static double spamProbability(double beta, double mean){
    return Math.exp((mean + beta)/(1+Math.exp(mean + beta)));
  }
  
  public static boolean closeEnough(double probability, int truth){
    if ((truth == 0) && (probability <=0.5))
      return true;
    if ((truth == 1) && (probability > .5))
      return true;
    else
      return false;
  }
  
  public static double getPr(int spam, int health, int n){
        //Logistic regression coefficients from Excel analysis
    double beta0 = -.001;
    double beta1 = 0.00048158;
    double beta2 = -0.001;
    double beta3 = .002;
    
    return Math.exp(beta0 + beta1*spam + beta2*health + beta3*n)/(1 +Math.exp(beta0 + beta1*spam + beta2*health + beta3*n));
  }
}