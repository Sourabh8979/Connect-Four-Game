package Con4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.effect.Light;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	//declaring rules for games
	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String discColor1 = "#24303E";
	private static final String discColor2 = "#4CAA88";

	private static String PLAYER_ONE ;
	private static String PLAYER_TWO ;

	private boolean isPlayerOneTurn = true;

	private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;

	@FXML
	public Label playerNameLabel;

	@FXML
	public TextField playerOneTextField ,playerTwoTextField ;

	@FXML
	public Button setNamesButton;

	private boolean isAllowedToInsert = true;    // flag to avoid same color disc added   *******

	public void createPlayground(){

		Shape rectangleWithHoles = createGameStructuralGrid();

		rootGridPane.add(rectangleWithHoles,0,1);
		List<Rectangle> rectangleList = createClickableColumns();

		for (Rectangle rectangle:rectangleList) {
			rootGridPane.add(rectangle,0,1);
		}
		setNamesButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String input1 = playerOneTextField.getText();
				String input2 = playerTwoTextField.getText();
				PLAYER_ONE = input1;
				PLAYER_TWO = input2;
			}
		});

	}
	private Shape createGameStructuralGrid(){

		Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER,(ROWS+1) * CIRCLE_DIAMETER);

		for(int row=0;row<ROWS;row++) {
			for (int col = 0; col < COLUMNS; col++){
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER/2);
				circle.setCenterX(CIRCLE_DIAMETER/2);
				circle.setCenterY(CIRCLE_DIAMETER/2);
				circle.setSmooth(true);

				circle.setTranslateX(col * (CIRCLE_DIAMETER +5) + CIRCLE_DIAMETER/4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER+5) + CIRCLE_DIAMETER/4);

				rectangleWithHoles = Shape.subtract(rectangleWithHoles,circle);


			}
		}


		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}
	public List<Rectangle> createClickableColumns(){

		List<Rectangle> rectangleList = new ArrayList<>();

		for(int col=0;col<COLUMNS;col++){
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER,(ROWS+1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER +5) +CIRCLE_DIAMETER/4);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int column = col;
			  // when disc is being droped then no more disc is inserted
				rectangle.setOnMouseClicked(event -> {
					if(isAllowedToInsert) {
						isAllowedToInsert = false;
						insertDisc(new Disc(isPlayerOneTurn), column);

					}
				});
			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void insertDisc(Disc disc,int column){
		int row = ROWS - 1;
		while (row >= 0){
			if(getDiscIfPresent(row ,column) == null)
				break;

			row--;
		}
		if(row < 0) //if row is full we cannot insert more disc
			return;


		insertedDiscArray[row][column] = disc;
		insertedDiscsPane.getChildren().add(disc);

		disc.setTranslateX(column * (CIRCLE_DIAMETER +5) +CIRCLE_DIAMETER/4);

		int currentRow = row;

		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);

		translateTransition.setToY(row * (CIRCLE_DIAMETER+5) + CIRCLE_DIAMETER/4);

		translateTransition.setOnFinished(event -> {
			isAllowedToInsert = true; // finally the disc is dropped allowed next player to iinsert disc
			if(gameEnded(currentRow ,column)){
			gameOver();
			return;
			}

			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE:PLAYER_TWO);

		});
		translateTransition.play();
	}
	private  boolean gameEnded(int row ,int column){


		List<Point2D> verticalsPoints = IntStream.rangeClosed(row - 3 ,row +3) //range of row values 0,1,2,3,4,5
										.mapToObj(r -> new Point2D(r ,column)) //index of each element present in col
										.collect(Collectors.toList());
		//for horizontals
		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3 ,column +3) //range of row values 0,1,2,3,4,5
										.mapToObj(col -> new Point2D(row ,col)) //index of each element present in col [row][col]
				                        .collect(Collectors.toList());
		//for diagonal one
		Point2D startPoint1 = new Point2D(row - 3,column + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0 ,6)
										.mapToObj(i-> startPoint1.add(i , -i))
										.collect(Collectors.toList());
		//for diagonal second
		Point2D startPoint2 = new Point2D(row - 3,column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0 ,6)
				.mapToObj(i-> startPoint2.add(i , i))
				.collect(Collectors.toList());


		boolean isEnded = checkCombinations(verticalsPoints) || checkCombinations(horizontalPoints)
						|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
		return  isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;
		for (Point2D point:points) {

		int rowIndexForArray = (int) point.getX();
		int columnIndexForArray = (int) point.getY();
		Disc disc = getDiscIfPresent(rowIndexForArray ,columnIndexForArray);
		if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn){
			chain++;
			if(chain ==4){
				return true;
			}
		}else{
			chain =0;
		}

		}
		return false;
	}
	private Disc getDiscIfPresent(int row ,int column){ //To prevant array index outofBoundException
	if(row >=ROWS || row < 0 || column >=COLUMNS || column < 0 ) //if row or column index are invalid
		return null;

	return insertedDiscArray[row][column];
	}

	private void gameOver(){
		String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
		System.out.println("Winner is "+winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("Winner is "+winner);
		alert.setContentText("Want to play again ");

		ButtonType yesBtn = new ButtonType("yes");
		ButtonType noBtn = new ButtonType("No ,Exit");
		alert.getButtonTypes().setAll(yesBtn ,noBtn);

		Platform.runLater(  () ->{
			Optional<ButtonType> btnClicked = alert.showAndWait();
			if(btnClicked.isPresent() && btnClicked.get() == yesBtn){
				//User choose yes to reset the game
				resetGame();
			}else{
				//User choose no to quit the game
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public void resetGame() {
		insertedDiscsPane.getChildren().clear();  //Remove all inserted disc from pane
		for(int row = 0;row < insertedDiscArray.length;row++){ // structurally make all the elements to nulll
			for(int col = 0;col < insertedDiscArray[row].length;col++){
				insertedDiscArray[row][col] = null;
			}
		}
		isPlayerOneTurn = true;  // Let player one start the game
		playerNameLabel.setText(PLAYER_ONE);

		createPlayground();  //prepare new playground
	}

	//this class helps in determining the colour of disc
	private static class Disc extends Circle{

		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove){
		this.isPlayerOneMove = isPlayerOneMove;
		setRadius(CIRCLE_DIAMETER/2);
		setFill(isPlayerOneMove?Color.valueOf(discColor1):Color.valueOf(discColor2));
		setCenterX(CIRCLE_DIAMETER/2);
		setCenterY(CIRCLE_DIAMETER/2);
		}
	}

	public void initialize(URL location, ResourceBundle resources) {

	}
}
