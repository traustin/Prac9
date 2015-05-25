

/**
 * Created by wernermostert on 2015/04/26.
 */
public class MailHeader {
    public String subject;
    public String sender;
    public int size;
    public int indexOnServer;

    public MailHeader(String sub, String send, int size_){
        subject = sub;
        sender = send;
        size = size_;        
    }

    public MailHeader(int size){
        this.size = size;
        subject = "";
        sender = "";        
    }

    @Override
    public String toString(){
        return "Sender: "+sender+"\nSize: "+size+"\nSubject: "+subject+"\n";
    }
}
