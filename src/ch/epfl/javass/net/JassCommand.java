package ch.epfl.javass.net;

/**
* énumère les 7 types de messages échangés par le client et le serveur
* la méthode name() permet de retourner la String correspondante à la valeur.
* la méthode JassCommand.valueOf(String) fait l'inverse et retourne la valeur correspondante.
* @author Raoul Gerber (304502)
*/
public enum JassCommand {
//    setPlayers  
    PLRS(),
//    setTrump    
    TRMP(),
//    updateHand  
    HAND(),
//    updateTrick 
    TRCK(),
//    cardToPlay  
    CARD(),
//    updateScore 
    SCOR(),
//    setWinningTeam  
    WINR();
    
}
