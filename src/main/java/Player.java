import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Player {

    private String name;
    private ArrayList<Card> cards;
    private int gameMode;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public Player(Socket socket, GameController gameController) {
        this.cards = null;
        this.gameMode = 0;

        System.out.println("New Connection from "+socket.getInetAddress().getHostAddress());
        try {
            objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread isThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    objectInputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

                    while(true) {
                        try {
                            gameController.readInput(Player.this, (ArrayList<String>)objectInputStream.readObject());
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                            socket.close();
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        isThread.start();
    }

    public void setCards(ArrayList<Card> cards) throws IOException {
        this.cards = cards;

        ArrayList<String> output = new ArrayList<String>();
        output.add("playerCards");

        for(Card card : cards) {
            output.add(card.getColor() + card.getType());
        }

        objectOutputStream.writeObject(output);
        objectOutputStream.flush();
        objectOutputStream.reset();
    }

    public void setGameMode(int gameMode) throws IOException {
        this.gameMode = gameMode;

        System.out.println("["+name+", gameMode, "+gameMode+"]");

        objectOutputStream.writeObject(new ArrayList<String>(Arrays.asList("gameMode", Integer.toString(gameMode))));
        objectOutputStream.flush();
        objectOutputStream.reset();
    }

    public void setName(String name) { this.name = name; }

    public int getGameMode() { return gameMode; }

    public String getName() { return name; }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }
}
