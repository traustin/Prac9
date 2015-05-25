public class Main {
    public static String pop3ServerName = "pop3.mweb.co.za";
    public static String pop3Username = "werner.mostert1@mweb.co.za";
    public static String pop3Password = "1aeolus1";
    public static String smtpServer = "localhost";
    public static int smtpPort = 8111;
    public static int pop3Port = 110;


    public static void main(String[] args) {
        GameServer game;

        if(args.length > 0)
            game = new GameServer(new Integer(args[0]));
        else
            game = new GameServer(8190);

        game.run();
    }
}
