

/**
 * Created by wernermostert on 2015/04/25.
 */
public class NotPOP3ServerException extends Exception{
    public NotPOP3ServerException(){
        super("The server connected to did not respond in a manner consistent with a POP3 server");
    }
}
