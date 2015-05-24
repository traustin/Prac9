import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

/**
 * Created by Trevor on 2015-05-24.
 */
public class WordGenerator {
    private String document;
    private BufferedReader br;

    public WordGenerator(String document) {
        this.document = document;
        br = null;
    }

    public String getWord(){
        String word = "";
        int count = 0;
        try {
            br = new BufferedReader(new FileReader(document));

            while((br.readLine()!=null)){
                count++;
            }
            br = new BufferedReader(new FileReader(document));
            Random rand = new Random();
            int number = rand.nextInt((count+1));
            for(int i = 0;  i <  number; i++) {
                word = br.readLine();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return word;
    }
}
