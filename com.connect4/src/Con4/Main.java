package Con4;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main extends Application {
    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();


        controller = loader.getController();
        controller.createPlayground();
        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);
        Scene scene = new Scene(rootGridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect four");
        primaryStage.setResizable(false);
        primaryStage.show();

    }
        private MenuBar createMenu(){
        //file menu
        Menu fileMenu = new Menu("file");
        MenuItem newGame = new MenuItem("New Game");
        //adding action on new game
        newGame.setOnAction(event -> controller.resetGame());

        //adding action on reset game
        MenuItem resetGame = new MenuItem("Reset Game");

        resetGame.setOnAction(event -> controller.resetGame());

        SeparatorMenuItem sepratorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        //exit game on action
        exitGame.setOnAction(event -> exitGame());
        fileMenu.getItems().addAll(newGame ,resetGame ,sepratorMenuItem,exitGame);
        // Help menu
            Menu helpMenu = new Menu("Help");
            MenuItem aboutGame = new MenuItem("about connect 4");
            aboutGame.setOnAction(event ->aboutConnect4() );


            SeparatorMenuItem seprator = new SeparatorMenuItem();
            MenuItem aboutMe = new MenuItem("About me");
            aboutMe.setOnAction(event -> aboutMe());


            helpMenu.getItems().addAll(aboutGame,seprator,aboutMe);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);
        return menuBar;
    }




    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the developers");
        alert.setHeaderText("Sourabh Singh");
        alert.setContentText("I love to play around the codes and creates game." +
                "Connect four is one of them");
        alert.show();

    }

    private void aboutConnect4() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect four");
        alert.setHeaderText("How to play");
        alert.setContentText("Connect Four is a two-player connection game in which "+
                "the players first choose a color and then take turns dropping "+
                "colored discs from the top into a seven-column, six-row vertically"+
                "suspended grid. The pieces fall straight down, occupying the next " +
                "available space within the column. The objective of the game is to" +
                " be the first to form a horizontal, vertical, or diagonal line of four of " +
                "one's own discs. Connect Four is a solved game. The first player can" +
                " always win by playing the right moves. ");

        alert.show();
    }

    private void exitGame() {
	   Platform.exit();
	   System.exit(0);
    }
    private void resetGame() {
    }

    // resetgame method
    private void resetgame() {
    }





    public static void main(String[] args) {
        launch(args);
    }
}
