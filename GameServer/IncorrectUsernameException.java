

/**
 *
 * @author Abrie van Aardt
 */
public class IncorrectUsernameException extends Exception {

    public IncorrectUsernameException() {
        super("The username is not recognised by the server");
    }
}
