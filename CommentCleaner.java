import java.util.LinkedList;
//This class 'cleans' YouTube comment text for analysis
public class CommentCleaner{
  
  static String punctuation = ".,!?#$%^&*-~:+;[]{}???????????()/\"<>0123456789";
  
  public static String[] cleanString(String input){
    LinkedList<String> output = new LinkedList<String>();
    if (input.contains("http") || (input.contains("ww.")))//web address
      output.add("URL_ADDRESS");
    else {//punctuation
      String temp = "";
      for (int i = 0; i < input.length(); i++){
        if (temp.length() > 50)
          temp = "LONG_WORD";
        if (punctuation.contains((input.substring(i, i+1)))){//non-letter characters
            if (temp.length() > 0){
              output.add(temp);
              temp = "";
            }
          if ((punctuation.indexOf(input.charAt(i)) > 33) && ((output.size() == 0) || (output.peekLast().equals("INSTANCE_OF_NUMBER") == false))){//there is a number in the current wordblock
            output.add("INSTANCE_OF_NUMBER");
          }
          if (punctuation.indexOf(input.charAt(i)) <= 33){//any random punctuation
            output.add(input.substring(i, i+1));
          }
        }
        else {//normal words
          temp+=input.charAt(i);
        }
      }
       if (temp.length() > 0){//catching any final ending normal words
              output.add(temp);
              temp = "";
      }
    }
    return output.toArray(new String[output.size()]);
  }
}