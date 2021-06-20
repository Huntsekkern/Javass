package ch.epfl.javass;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
* contient le programme principal permettant de jouer une partie locale
* @author Raoul Gerber (304502)
*/
public final class LocalMain extends Application {
    /**
     * Lance le jeu principal (qui accueille les joueurs distants) selon les paramètres passés en arguments
     * Il faut d'abord que les joueurs distants démarrent leur serveur avant lancer ce programme.
     * @param args <j1>…<j4> [<graine>], plus de détails sont disponibles dans le texte de console qui s'affiche en lançant le jeu sans passer d'arguments.
     */
    public static void main(String[] args) { launch(args); }
    
    private static final int DEFAULT_MCTS_ITERATIONS = 10000;
    private static final String DEFAULT_HOSTNAME = "localhost";
    private static final String[] DEFAULT_NAMES = {"Alice", "Bastien", "Colette", "David"};
    
    private static final int MAX_PARAMETERS_COUNT = PlayerId.COUNT + 1;
    
    // TODO Si je change le temps de pause des joueurs et de fin de pli, les remettre à 2 et 1000 avant l'upload.
    private static final int MS_BREAK_AT_END_TRICK = 1000;
    private static final int S_MIN_MCTS_DELAY = 2;
    
    // J'avais mis un assert débile sur playableCards de HandBean qui causerait exactement ce problème, mais l'ai enlevé depuis...

    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {       
        
        
        List<String> arguments = getParameters().getRaw();
        
        if(arguments.size() > MAX_PARAMETERS_COUNT || arguments.size() < PlayerId.COUNT) {
            System.err.println("Utilisation: java ch.epfl.javass.LocalMain <j1>…<j4> [<graine>]");
            System.err.println("où :");
            System.err.println("<jn> spécifie le joueur n, ainsi:");
            System.err.println("h:<nom>  un joueur humain nommé <nom>");
            System.err.println("s:<nom>:<iterations> un joueur simulé nommé <nom> et effectuant <iterations> iterations de son algorithme");
            System.err.println("r:<nom>:<IP> un joueur distant nommé <nom> jouant depuis l'hôte <IP>");
            System.err.println("<graine> est un paramètre optionnel pour les générateurs aléatoires");
            System.err.println("Les paramètres peuvent être omis et des valeurs par défauts seront sélectionnées");
            System.err.println("Par exemple, la syntaxe suivante est valide :");
            System.err.println("s h:Marie r:Céline:128.178.243.14 s::20000");
            System.exit(1);
        }
        
        Random rng = new Random();
        
        if(arguments.size() == (MAX_PARAMETERS_COUNT)) {
            try {
                long seed = Long.parseLong(arguments.get(PlayerId.COUNT));
                rng = new Random(seed);
            } catch (Exception e) {
                System.err.println("Erreur : la graine choisie n'est pas un Long valide");
                System.exit(1);
            }
        }
        
        long[] randoms = new long[MAX_PARAMETERS_COUNT];
        for(int i = 0 ; i < MAX_PARAMETERS_COUNT; ++i) {
            randoms[i] = rng.nextLong();
        }
        
        
        
        Map<PlayerId, Player> ps = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
        
        
        for(int i = 0; i<PlayerId.COUNT; ++i) {
            String player = arguments.get(i);
            String[] parts = player.split(":");
            
            char id = parts[0].charAt(0);
            
            String name = DEFAULT_NAMES[i];
            
            String thirdPart = null;
            

            // assistante a confirmé que pas besoin de transformer les index ci-dessous en constantes.
            if(parts[0].length() > 1) {
                System.err.println("Erreur : S'il y a un charactère après l'identifiant, ce doit être ':'");
                System.exit(1);
            } 
            
            if(parts.length > 1 && !parts[1].isEmpty()) {
                name = parts[1];
            }
            
            if(parts.length > 2 && !parts[2].isEmpty()) {
                thirdPart = parts[2];
            }
            
            
            switch(id) {
            case 'h':
                if(thirdPart != null) {
                    System.err.println("Erreur : un joueur humain ne se définit pas avec un troisième paramètre");
                    System.exit(1);
                }
                ps.put(PlayerId.ALL.get(i), new GraphicalPlayerAdapter());

                break;
                
                
            case 's':
                int iterations = DEFAULT_MCTS_ITERATIONS;
                if(thirdPart != null) {
                    try {
                        iterations = Integer.parseInt(thirdPart);
                    } catch (Exception e) {
                        System.err.println("Erreur : le nombre d'itérations n'est pas un Integer valide");
                        System.exit(1);
                    }
                } 
                if(iterations <= Jass.TRICKS_PER_TURN) {
                    System.err.println("Erreur : le nombre d'itérations ne peut pas être plus petit que 10");
                    System.exit(1);
                }
                ps.put(PlayerId.ALL.get(i), new PacedPlayer(new MctsPlayer(PlayerId.ALL.get(i), randoms[i+1], iterations), S_MIN_MCTS_DELAY));
                break;
                
                
            case 'r':
                String hostName = DEFAULT_HOSTNAME;
                if(thirdPart != null) {
                    hostName = thirdPart;
                } 
                try {
                    ps.put(PlayerId.ALL.get(i), new RemotePlayerClient(hostName));
                } catch (Exception e) {
                    System.err.println("Erreur : lors de la connexion au serveur d'un joueur distant");
                    System.exit(1);
                }
                break;
                
                
            default:
                System.err.println("Erreur : l'identifiant du type de joueur n'est pas une lettre valide (h, s ou r)");
                System.exit(1);
                break;
            }
            
            
            ns.put(PlayerId.ALL.get(i), name);
        }

        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(randoms[0], ps, ns);
            while (! g.isGameOver()) {
              g.advanceToEndOfNextTrick();
              try { Thread.sleep(MS_BREAK_AT_END_TRICK); } catch (Exception e) {throw new Error(e);}
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
      }

}
