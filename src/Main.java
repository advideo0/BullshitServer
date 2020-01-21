import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(3232);
        System.out.println("Server started");
        GameController gameController = new GameController();

        while(true) {
            try {
                gameController.addPlayer(new Player(serverSocket.accept(), gameController));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
