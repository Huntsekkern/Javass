package ch.epfl.javass.gui;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * représente l'interface graphique d'un joueur humain
 * @author Raoul Gerber (304502)
 */
public final class GraphicalPlayer {
    
    private final PlayerId player;
    private final Map<PlayerId, String> playerNames;
    private final ScoreBean sb;
    private final TrickBean tb;
    private final HandBean hb;
    private final ArrayBlockingQueue<Card> queue;

    private Stage window;
    private final Scene scene;
        
    private static final int TRICK_CARD_WIDTH = 120;
    private static final int TRICK_CARD_HEIGHT = 180; 
    private static final int TRUMP_WIDTH = 101;
    private static final int TRUMP_HEIGHT = 101; 
    private static final int HAND_CARD_WIDTH = 80;
    private static final int HAND_CARD_HEIGHT = 120; 
    
    private static final int TRICK_CARD_FULL_WIDTH = 240;
    private static final int HAND_CARD_FULL_WIDTH = 160;
    
    private static final double CARD_FULL_VISIBLE = 1;
    private static final double CARD_SHADED = 0.2;
    private static final int BLUR_RADIUS = 4;
    
    private final ObservableMap<Card, Image> trickCardMap = createCardMap(TRICK_CARD_FULL_WIDTH);
    private final ObservableMap<Card.Color, Image> trumpMap = createTrumpMap();
    private final ObservableMap<Card, Image> handCardMap = createCardMap(HAND_CARD_FULL_WIDTH);
    

    /**
     * Construit la majorité de l'interface graphique du jeu
     * @param player identité du joueur pilotant cette interface graphique
     * @param playerNames table associative des noms des joueurs à leur identité
     * @param sb ScoreBean
     * @param tb TrickBean
     */
    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> playerNames, ScoreBean sb, TrickBean tb, HandBean hb, ArrayBlockingQueue<Card> queue) {
        this.player = player;
        this.playerNames = playerNames;
        this.sb = sb;
        this.tb = tb;
        this.hb = hb;
        this.queue = queue;
        

        //StackPane contient le mainPane et les deux victoryPanes transparents au début. Chaque VictoryPane est un Border Pane.
        // Le mainPane est composé de deux GridPane
        BorderPane mainPane = new BorderPane();
        
        GridPane scorePane = createScorePane();
        GridPane trickPane = createTrickPane();
        HBox handPane = createHandPane();
        
        mainPane.setTop(scorePane);
        mainPane.setCenter(trickPane);
        mainPane.setBottom(handPane);
        
        // une itération sur TeamId ici serait possible, mais prendrait plus de lignes sans gagner en compréhension 
        BorderPane victoryPane1 = createVictoryPanes(TeamId.TEAM_1);
        BorderPane victoryPane2 = createVictoryPanes(TeamId.TEAM_2);
        
        
        StackPane finalPane = new StackPane(mainPane, victoryPane1, victoryPane2);
        
        scene = new Scene(finalPane);
    }
    
    /**
     * Construit et retourne l'instance de Stage représentant la fenêtre
     * @return l'instance de Stage représentant la fenêtre
     */
    public Stage createStage() {
        window = new Stage(StageStyle.UTILITY);
        window.setScene(scene);
        window.setTitle("Javass - " + playerNames.get(player));
        return window;
    }

    
    // Il faut ajouter des listeners sur les objets qu'on retravaille (différence des points par ex.) qui updatent ce qui est affiché par une lambda
    // Sinon il juste faut créer des Text, dont on bind l'attribut TextProperty au truc du beans.
    
    private GridPane createScorePane() {
        GridPane scorePane = new GridPane();
        scorePane.setStyle("-fx-font: 16 Optima; -fx-background-color: lightgray; -fx-padding: 5px; -fx-alignment: center;");
        

        for(TeamId team : TeamId.ALL) {
            
            int teamNum = team.ordinal();
            
            // Names
            Text teamNames = new Text(teamNames(team) + " : "); 
            GridPane.setHalignment(teamNames, HPos.RIGHT);
            scorePane.add(teamNames, 0, teamNum);
            
            // TurnPoints
            Text turnScore = new Text();
            turnScore.textProperty().bind(Bindings.convert(sb.turnPointsProperty(team)));
            GridPane.setHalignment(turnScore, HPos.RIGHT);
            scorePane.add(turnScore, 1, teamNum);
            
            // Difference Last Trick
            Text difference = new Text();
            sb.turnPointsProperty(team).addListener(
                    (o, oV, nV) -> difference.setText(" (+" + ((nV.intValue() > oV.intValue()) ? nV.intValue()-oV.intValue() : 0) + ") "));
            scorePane.add(difference, 2, teamNum);
            
            // Total
            Text total = new Text("/ Total : ");
            GridPane.setHalignment(total, HPos.RIGHT);
            scorePane.add(total, 3, teamNum);
            
            // GamePoints
            Text gameScore = new Text();
            gameScore.textProperty().bind(Bindings.convert(sb.gamePointsProperty(team)));
            scorePane.add(gameScore, 4, teamNum);
        }

      
        return scorePane;
    }
    
    
    private Rectangle createHalo(PlayerId playerId) {
        Rectangle halo = new Rectangle(TRICK_CARD_WIDTH, TRICK_CARD_HEIGHT);
        halo.setStyle("-fx-arc-width: 20; -fx-arc-height: 20; -fx-fill: transparent; -fx-stroke: lightpink; -fx-stroke-width: 5; -fx-opacity: 0.5;");
        halo.setEffect(new GaussianBlur(BLUR_RADIUS));
        halo.visibleProperty().bind(tb.winningPlayer().isEqualTo(playerId));
        return halo;
    }
    
    private Text createNameText(PlayerId playerId) {
        Text name = new Text(playerNames.get(playerId));
        name.setStyle("-fx-font: 14 Optima;");
        return name;
    }
    
    private ObservableMap<Card.Color, Image> createTrumpMap(){
        Map<Card.Color, Image> trumpMap = new EnumMap<Card.Color, Image>(Card.Color.class);
        for(int i = 0; i<Card.Color.COUNT; ++i) {
            trumpMap.put(Card.Color.ALL.get(i), new Image("/trump_" + i + ".png"));
        }
        return FXCollections.unmodifiableObservableMap(FXCollections.observableMap(trumpMap));
    }
    
    private ObservableMap<Card, Image> createCardMap(int width){
        ObservableMap<Card, Image> cardMap = FXCollections.observableHashMap();
        for(int c=0; c<Card.Color.COUNT; ++c) {
            for(int r=0; r<Card.Rank.COUNT; ++r) {
                cardMap.put(Card.of(Card.Color.ALL.get(c), Card.Rank.ALL.get(r)), new Image("/card_" +c+ "_" +r+ "_" +width+ ".png"));
            }
        }
        return FXCollections.unmodifiableObservableMap(cardMap);
    }
    
    
    private GridPane createTrickPane() {
        GridPane trickPane = new GridPane();
        trickPane.setStyle("-fx-background-color: whitesmoke; -fx-padding: 5px;  -fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray; -fx-alignment: center;");
        
        // Le VBox ci-dessous est le child de 4 des 5 trickPane
        // VBox contenant un Text et un Stack Pane
        // StackPane contenant un Rectangle et une ImageView
        // imageProperty de ImageView binded au trick du bean du pli
        
        //TODO optional the rotate trick to have the same order of players, players_name, etc.
        
        VBox[] cards = new VBox[PlayerId.COUNT];
        
        for(int i = 0; i<PlayerId.COUNT; ++i) {
            // ensure that for i = 0, it's our own card, and so on
            PlayerId playerId = PlayerId.ALL.get(((player.ordinal()+i)%PlayerId.COUNT));
            
            ImageView card = new ImageView();
            card.setFitWidth(TRICK_CARD_WIDTH);
            card.setFitHeight(TRICK_CARD_HEIGHT);
            card.imageProperty().bind(Bindings.valueAt(trickCardMap, Bindings.valueAt(tb.trick(), playerId)));
            Rectangle halo = createHalo(playerId);
            StackPane stack = new StackPane(card, halo);
            
            Text name = createNameText(playerId);
            GridPane.setHalignment(name, HPos.CENTER);
            
            VBox box = new VBox();
            box.setStyle("-fx-padding: 5px; -fx-alignment: center;");
            
            if(i == 0) {
                box.getChildren().addAll(stack, name);
            } else {
                box.getChildren().addAll(name, stack);
            }
   
            cards[i] = box;
        }
        
        ImageView trump = new ImageView();
        trump.setFitWidth(TRUMP_WIDTH);
        trump.setFitHeight(TRUMP_HEIGHT);
        
        trump.imageProperty().bind(Bindings.valueAt(trumpMap, tb.trump()));
      
        // opponent card, left whole column
        trickPane.add(cards[3], 0, 0, 1, 3);
        GridPane.setValignment(cards[1], VPos.CENTER);
        
        // partner card, center top
        trickPane.add(cards[2], 1, 0);
        
        // trump, center, center
        trickPane.add(trump, 1, 1);
        GridPane.setHalignment(trump, HPos.CENTER);
        
        // own card, center bottom
        trickPane.add(cards[0], 1, 2);
        
        // opponent card, right whole column
        trickPane.add(cards[1], 2, 0, 1, 3);
        GridPane.setValignment(cards[3], VPos.CENTER);
                
        
        return trickPane;
    }
    
    
    
    private BorderPane createVictoryPanes(TeamId winningTeam) {
         String teamNames = teamNames(winningTeam);       
                 
         Text winners = new Text();
         winners.textProperty().bind(
                         Bindings.format(
                             (teamNames + " ont gagné avec %1$s points contre %2$s."), 
                             sb.totalPointsProperty(winningTeam), 
                             sb.totalPointsProperty(winningTeam.other())));  

        BorderPane victoryPane = new BorderPane(winners);
        victoryPane.setStyle("-fx-font: 16 Optima;  -fx-background-color: white;");
        
        BooleanBinding hasWon = sb.winningTeamProperty().isEqualTo(winningTeam);
        victoryPane.visibleProperty().bind(hasWon);
        
        return victoryPane;
        
    }
    
    private HBox createHandPane() {
        HBox handPane = new HBox();
        handPane.setStyle("-fx-background-color: lightgray;  -fx-spacing: 5px;  -fx-padding: 5px;");
        
        for(int i = 0; i<Jass.HAND_SIZE; ++i) {
            ImageView cardImage = new ImageView();
            cardImage.setFitWidth(HAND_CARD_WIDTH);
            cardImage.setFitHeight(HAND_CARD_HEIGHT);
            ObjectBinding<Card> card = Bindings.valueAt(hb.hand(), i);
            cardImage.imageProperty().bind(Bindings.valueAt(handCardMap, card));
            
            cardImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    try {
                        queue.put(card.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            
            BooleanBinding isPlayable = Bindings.createBooleanBinding(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return hb.playableCards().contains(card.get());
                }
            },
                    hb.hand(), hb.playableCards());
            
            cardImage.opacityProperty().bind(Bindings.when(isPlayable).then(CARD_FULL_VISIBLE).otherwise(CARD_SHADED));
            cardImage.disableProperty().bind(isPlayable.not());
            
            handPane.getChildren().add(cardImage);
        }
        
        
        return handPane;
    }
    
    private String teamNames(TeamId team) {
        if(team == TeamId.TEAM_1) {
            return (playerNames.get(PlayerId.PLAYER_1) + " et " + playerNames.get(PlayerId.PLAYER_3));
        } else {
            return (playerNames.get(PlayerId.PLAYER_2) + " et " + playerNames.get(PlayerId.PLAYER_4));
        }
    }
    
}
