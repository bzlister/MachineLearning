public class WordData{
  String word;
  int spamCount = 0;
  int healthyCount = 0;
  
  public WordData(String w, int sC, int hC){
    this.word = w;
    this.spamCount = sC;
    this.healthyCount = hC;
  }
}