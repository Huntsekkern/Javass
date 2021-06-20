package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
* contient le programme principal permettant de jouer à une partie distante.
* @author Raoul Gerber (304502)
*/
public final class RemoteMain extends Application {
    /**
     * Lance le serveur d'un joueur distant, à lancer avant le programme LocalMain
     * @param args ne pas passer d'arguments
     */
    public static void main(String[] args) { launch(args); }

    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Thread gameThread = new Thread(() -> {
            RemotePlayerServer server = new RemotePlayerServer(new GraphicalPlayerAdapter());
            server.run();
        });
        gameThread.setDaemon(true);
        gameThread.start();
        
        System.out.println("La partie commencera à la connexion du client...");
    }
}
