import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class GameController {

    private ArrayList<Player> players;
    private ArrayList<String> leaderBoard;
    private ArrayList<Card> openCards;
    private ArrayList<Card> gameCards;
    private Card toldCard;
    private int currentPlayer;
    private int oldPlayer;
    private int currentCardCount;

    public GameController() {

        players = new ArrayList<Player>();
        leaderBoard = new ArrayList<String>();
        openCards = new ArrayList<Card>();
        gameCards = new ArrayList<Card>();
    }

    public void readInput(Player pPlayer, ArrayList<String> pInput) throws IOException {
        switch(pInput.get(0)) {
            case "playerName":
                if(pPlayer.getGameMode() == 0) {
                    pPlayer.setName(pInput.get(1));
                }

                ArrayList<String> output = new ArrayList<String>();
                output.add("playersNames");
                for(Player player : players) {
                    output.add(player.getName());
                }

                for(Player player : players) {
                    player.getObjectOutputStream().writeObject(output);
                    player.getObjectOutputStream().flush();
                    player.getObjectOutputStream().reset();
                }
                System.out.println("Player "+pInput.get(1)+" joined");

                pPlayer.setGameMode(1);
                pPlayer.getObjectOutputStream().writeObject(new ArrayList<String>(Arrays.asList("startLoadingAnimation")));
                pPlayer.getObjectOutputStream().flush();
                pPlayer.getObjectOutputStream().reset();

                break;

            case "startGame":
                if(pPlayer.getGameMode() == 1) {
                    startGame();
                }
                break;

            case "lie":
                if(pPlayer.getGameMode() == 2 || pPlayer.getGameMode() == 3) {
                    boolean isLying = false;
                    for(int i = gameCards.size() - currentCardCount; i < gameCards.size(); i++) {
                        if(!gameCards.get(i).getType().equals(toldCard.getType())) {
                            isLying = true;
                        }
                    }

                    showTurnedGameCards();

                    if(isLying) {
                        ArrayList<Card> iCards = players.get(oldPlayer).getCards();
                        iCards.addAll(gameCards);
                        players.get(oldPlayer).setCards(iCards);
                        gameCards.clear();
                        removeDuplicatedCards(players.get(oldPlayer));

                        updateOpenCards();
                        updatePlayerCardCount(players.get(oldPlayer));
                    } else {
                        ArrayList<Card> iCards = pPlayer.getCards();
                        iCards.addAll(gameCards);
                        pPlayer.setCards(iCards);
                        gameCards.clear();
                        removeDuplicatedCards(pPlayer);

                        updateOpenCards();
                        updatePlayerCardCount(pPlayer);
                    }

                    currentPlayer = oldPlayer;
                    nextPlayer();

                    for(Player player : players) {
                        player.setGameMode(0);
                    }

                    players.get(currentPlayer).setGameMode(4);
                }
                break;

            case "playedCards":
                if(!pInput.contains("toldCard")) {
                    if(pPlayer.getGameMode() == 3 && pInput.size() == currentCardCount + 1) {
                        ArrayList<Card> iCard = pPlayer.getCards();
                        for(int i = 1; i < pInput.size(); i++) {
                            gameCards.add(new Card(pInput.get(i).substring(0, 2), pInput.get(i).substring(2)));
                            for(Card card : pPlayer.getCards()) {
                                if(card.getColor().equals(pInput.get(i).substring(0, 2)) && card.getType().equals(pInput.get(i).substring(2))) {
                                    iCard.remove(card);
                                    break;
                                }
                            }
                        }
                        pPlayer.setCards(iCard);
                        updateGameCards();
                        updatePlayerCardCount(pPlayer);
                        oldPlayer = currentPlayer;
                        pPlayer.setGameMode(0);
                        nextPlayer();
                        if(players.get(currentPlayer).getGameMode() != 1) {
                            players.get(currentPlayer).setGameMode(3);
                        }
                    }
                } else {
                    if(pPlayer.getGameMode() == 4 && pInput.size() < 8 && pInput.size() > 3) {
                        currentCardCount = 0;

                        ArrayList<Card> iCard = pPlayer.getCards();
                        for(int i = 1; i < pInput.indexOf("toldCard"); i++) {
                            gameCards.add(new Card(pInput.get(i).substring(0, 2), pInput.get(i).substring(2)));
                            for(Card card : pPlayer.getCards()) {
                                if(card.getColor().equals(pInput.get(i).substring(0, 2)) && card.getType().equals(pInput.get(i).substring(2))) {
                                    iCard.remove(card);
                                    break;
                                }
                            }
                            currentCardCount++;
                        }
                        pPlayer.setCards(iCard);
                        toldCard = new Card(pInput.get(pInput.indexOf("toldCard") + 1).substring(0, 2), pInput.get(pInput.indexOf("toldCard") + 1).substring(2));
                        for(Player player : players) {
                            player.setGameMode(2);
                        }
                        updateGameCards();
                        updatePlayerCardCount(pPlayer);
                        oldPlayer = currentPlayer;
                        pPlayer.setGameMode(0);
                        nextPlayer();
                        if(players.get(currentPlayer).getGameMode() != 1) {
                            players.get(currentPlayer).setGameMode(3);
                        }
                    }
                }
                break;
        }
    }

    public void startGame() throws IOException {
        if(players.size() > 1) {
            System.out.println("Starting new Game");
            Card[] iCards;
            ArrayList<Integer> random = new ArrayList<Integer>();

            if (players.size() < 7) {
                iCards = new Card[]{new Card("he", "7"), new Card("he", "8"), new Card("he", "9"), new Card("he", "10"), new Card("he", "B"), new Card("he", "D"), new Card("he", "K"), new Card("he", "A"), new Card("ka", "7"), new Card("ka", "8"), new Card("ka", "9"), new Card("ka", "10"), new Card("ka", "B"), new Card("ka", "D"), new Card("ka", "K"), new Card("ka", "A"), new Card("kr", "7"), new Card("kr", "8"), new Card("kr", "9"), new Card("kr", "10"), new Card("kr", "B"), new Card("kr", "D"), new Card("kr", "K"), new Card("kr", "A"), new Card("pi", "7"), new Card("pi", "8"), new Card("pi", "9"), new Card("pi", "10"), new Card("pi", "B"), new Card("pi", "D"), new Card("pi", "K"), new Card("pi", "A")};
                for (int i = 0; i < 32; i++) {
                    int r = (int) (Math.random() * 32);

                    if (r == 32) {
                        r = 0;
                    }

                    if (random.contains(r)) {
                        i--;
                    } else {
                        random.add(r);
                    }
                }
            } else {
                iCards = new Card[]{new Card("he", "7"), new Card("he", "8"), new Card("he", "9"), new Card("he", "10"), new Card("he", "B"), new Card("he", "D"), new Card("he", "K"), new Card("he", "A"), new Card("ka", "7"), new Card("ka", "8"), new Card("ka", "9"), new Card("ka", "10"), new Card("ka", "B"), new Card("ka", "D"), new Card("ka", "K"), new Card("ka", "A"), new Card("kr", "7"), new Card("kr", "8"), new Card("kr", "9"), new Card("kr", "10"), new Card("kr", "B"), new Card("kr", "D"), new Card("kr", "K"), new Card("kr", "A"), new Card("pi", "7"), new Card("pi", "8"), new Card("pi", "9"), new Card("pi", "10"), new Card("pi", "B"), new Card("pi", "D"), new Card("pi", "K"), new Card("pi", "A"), new Card("he", "7"), new Card("he", "8"), new Card("he", "9"), new Card("he", "10"), new Card("he", "B"), new Card("he", "D"), new Card("he", "K"), new Card("he", "A"), new Card("ka", "7"), new Card("ka", "8"), new Card("ka", "9"), new Card("ka", "10"), new Card("ka", "B"), new Card("ka", "D"), new Card("ka", "K"), new Card("ka", "A"), new Card("kr", "7"), new Card("kr", "8"), new Card("kr", "9"), new Card("kr", "10"), new Card("kr", "B"), new Card("kr", "D"), new Card("kr", "K"), new Card("kr", "A"), new Card("pi", "7"), new Card("pi", "8"), new Card("pi", "9"), new Card("pi", "10"), new Card("pi", "B"), new Card("pi", "D"), new Card("pi", "K"), new Card("pi", "A")};
                for (int i = 0; i < 64; i++) {
                    int r = (int) (Math.random() * 64);

                    if (r == 64) {
                        r = 0;
                    }

                    if (random.contains(r)) {
                        i--;
                    } else {
                        random.add(r);
                    }
                }
            }

            for (int i = 0; i < players.size(); i++) {
                ArrayList<Card> cards = new ArrayList<Card>();
                for (int j = (i * (iCards.length / players.size())); j < (i + 1) * (iCards.length / players.size()); j++) {
                    cards.add(iCards[random.get(j)]);
                }

                players.get(i).setCards(cards);
                players.get(i).setGameMode(0);
                updatePlayerCardCount(players.get(i));
            }

            for (int i = iCards.length - (iCards.length % players.size()); i < iCards.length; i++) {
                openCards.add(iCards[random.get(i)]);
            }

            updateOpenCards();

            currentPlayer = (int) (Math.random() * players.size());
            if (currentPlayer == players.size()) {
                currentPlayer = 0;
            }

            nextPlayer();
            players.get(currentPlayer).setGameMode(4);
            stopLoadingAnimation();
        }
    }

    public void addPlayer(Player pPlayer) {
        players.add(pPlayer);
    }

    public void nextPlayer() throws IOException {
        if(players.size() - 1 == leaderBoard.size()){
            gameCards.clear();
            openCards.clear();
            leaderBoard.clear();
            updateGameCards();
            updateOpenCards();
            startLoadingAnimation();

            for(Player player : players) {
                player.setGameMode(1);
                player.setCards(new ArrayList<Card>());
                updatePlayerCardCount(player);
            }

        } else {

            if (currentPlayer == players.size() - 1) {
                currentPlayer = 0;
            } else {
                currentPlayer++;
            }

            if (players.get(currentPlayer).getCards().isEmpty()) {
                leaderBoard.add(players.get(currentPlayer).getName());
                nextPlayer();
            }

            ArrayList<String> output = new ArrayList<String>();
            output.add("focusPlayer");
            output.add(Integer.toString(currentPlayer));
            System.out.println(output);

            for (Player player : players) {
                player.getObjectOutputStream().writeObject(output);
                player.getObjectOutputStream().flush();
                player.getObjectOutputStream().reset();
            }
        }
    }

    public void removeDuplicatedCards(Player pPlayer) throws IOException {

        int[] iCards = new int[7];
        String[] cardName = {"7", "8", "9", "10", "B", "D", "K"};

        for(Card card : pPlayer.getCards()) {
            switch(card.getType()) {
                case "7":
                    iCards[0]++;
                    break;
                case "8":
                    iCards[1]++;
                    break;
                case "9":
                    iCards[2]++;
                    break;
                case "10":
                    iCards[3]++;
                    break;
                case "B":
                    iCards[4]++;
                    break;
                case "D":
                    iCards[5]++;
                    break;
                case "K":
                    iCards[6]++;
                    break;
            }
        }

        for(Card card : openCards) {
            switch(card.getType()) {
                case "7":
                    iCards[0]++;
                    break;
                case "8":
                    iCards[1]++;
                    break;
                case "9":
                    iCards[2]++;
                    break;
                case "10":
                    iCards[3]++;
                    break;
                case "B":
                    iCards[4]++;
                    break;
                case "D":
                    iCards[5]++;
                    break;
                case "K":
                    iCards[6]++;
                    break;
            }
        }

        int k = 0;
        for(int i : iCards) {
            if(i == 4) {
                for(int j = pPlayer.getCards().size() - 1; j >= 0; j--) {
                    if(pPlayer.getCards().get(j).getType().equals(cardName[k])) {
                        openCards.add(pPlayer.getCards().get(j));
                        pPlayer.getCards().remove(j);
                    }
                }
            }
            k++;
        }

        pPlayer.setCards(pPlayer.getCards());
    }

    public void updateOpenCards() throws IOException {
        ArrayList<String> output = new ArrayList<String>();
        output.add("openCards");
        for(Card card : openCards) {
            output.add(card.getColor() + card.getType());
        }

        System.out.println(output);

        for(Player player : players) {
            player.getObjectOutputStream().writeObject(output);
            player.getObjectOutputStream().flush();
            player.getObjectOutputStream().reset();
        }
    }

    public void updateGameCards() throws IOException {
        ArrayList<String> output = new ArrayList<String>();
        output.add("gameCards");
        output.add(Integer.toString(gameCards.size()));
        output.add(toldCard.getColor() + toldCard.getType());

        System.out.println(output);

        for(Player player : players) {
            player.getObjectOutputStream().writeObject(output);
            player.getObjectOutputStream().flush();
            player.getObjectOutputStream().reset();
        }
    }

    public void updatePlayerCardCount(Player pPlayer) throws IOException {
        ArrayList<String> output = new ArrayList<String>();
        output.add("playerCardCount");
        output.add(Integer.toString(players.indexOf(pPlayer)));
        output.add(Integer.toString(pPlayer.getCards().size()));

        System.out.println(output);

        for(Player player : players) {
            player.getObjectOutputStream().writeObject(output);
            player.getObjectOutputStream().flush();
            player.getObjectOutputStream().reset();
        }

        pPlayer.setCards(pPlayer.getCards());
    }

    public void showTurnedGameCards() throws IOException {
        ArrayList<String> output = new ArrayList<String>();
        output.add("turnedGameCards");
        for(int i = gameCards.size() - currentCardCount; i < gameCards.size(); i++) {
            output.add(gameCards.get(i).getColor() + gameCards.get(i).getType());
        }

        System.out.println(output);

        for(Player player : players) {
            player.getObjectOutputStream().writeObject(output);
            player.getObjectOutputStream().flush();
            player.getObjectOutputStream().reset();
        }
    }

    public void startLoadingAnimation() throws IOException {
        System.out.println("[startLoadingAnimation]");


        for (Player player : players) {
            player.getObjectOutputStream().writeObject(new ArrayList<String>(Arrays.asList("startLoadingAnimation")));
            player.getObjectOutputStream().flush();
            player.getObjectOutputStream().reset();
        }
    }
    public void stopLoadingAnimation() throws IOException {
        System.out.println("[stopLoadingAnimation]");


        for(Player player : players) {
            player.getObjectOutputStream().writeObject(new ArrayList<String>(Arrays.asList("stopLoadingAnimation")));
            player.getObjectOutputStream().flush();
            player.getObjectOutputStream().reset();
        }
    }
}
