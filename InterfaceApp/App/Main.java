package App;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Kosaraju's Algorithm");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(800);

        GridPane rootPane = new GridPane();
        rootPane.setStyle("-fx-background-color: #cce4ef;");

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(30);
        rootPane.getColumnConstraints().add(column1);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(58);
        rootPane.getRowConstraints().add(row1);

        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(40);
        rootPane.getRowConstraints().add(row2);

        VBox leftButtons = buttonsForLeftSide();
        rootPane.add(leftButtons, 0, 0);
        GridPane.setMargin(leftButtons, new Insets(30, 0, 0, 20));

        StackPane areaForGraph = graphArea();
        rootPane.add(areaForGraph, 1, 0);
        GridPane.setHgrow(areaForGraph, Priority.ALWAYS);
        GridPane.setVgrow(areaForGraph, Priority.ALWAYS);
        GridPane.setMargin(areaForGraph, new Insets(30, 20, 0, 20));

        BorderPane answer = textProgramArea("РЕЗУЛЬТАТ:");
        rootPane.add(answer, 0, 1);
        GridPane.setMargin(answer, new Insets(10, 0, 0, 20));

        BorderPane log = textProgramArea("ЛОГ:");
        rootPane.add(log, 1, 1);
        GridPane.setMargin(log, new Insets(10, 20, 0, 20));

        Scene scene = new Scene(rootPane, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private StackPane graphArea(){
        StackPane stackPane = new StackPane();

        ScrollPane graphCanvas = new ScrollPane();
        graphCanvas.setMinHeight(300);
        graphCanvas.setMinWidth(730);
        graphCanvas.setPannable(true);
        graphCanvas.setStyle(
                        "-fx-border-color: #0575ad; " +
                        "-fx-border-width: 6px; " +
                        "-fx-border-radius: 6px; " +
                        "-fx-background-radius: 8;"+
                        "-fx-border-style: solid;"
        );

        Image playImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("play.png")));
        Image pauseImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("pause.png")));
        ImageView imageView = new ImageView(playImage);
        imageView.setFitWidth(27);
        imageView.setFitHeight(27);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Button playButton = new Button("", imageView);
        playButton.setStyle(
                        "-fx-background-radius: 50%; " +
                        "-fx-min-width: 80px; " +
                        "-fx-min-height: 80px; " +
                        "-fx-max-width: 80px; " +
                        "-fx-max-height: 80px; " +
                        "-fx-background-color: #5abff2; " +
                        "-fx-text-fill: black;"+
                        "-fx-border-color: #0575ad; " +
                        "-fx-border-width: 4px; " +
                        "-fx-border-radius: 50%;"
        );
        final boolean[] flagPlay = {false};
        playButton.setOnAction(e -> {
            if (!flagPlay[0]) {
                imageView.setImage(playImage);
                flagPlay[0] = true;
            } else {
                imageView.setImage(pauseImage);
                flagPlay[0] = false;
            }
        });

        stackPane.getChildren().addAll(graphCanvas, playButton);

        StackPane.setAlignment(playButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(playButton, new Insets(10));

        return stackPane;
    }

    private VBox buttonsForLeftSide(){
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        Button fastResult = new Button("Получить результат");
        Button stepsResult = new Button("Выполнить по шагам");
        Button addVertex = new Button("Добавить вершину");
        Button addEdge = new Button("Добавить ребро");
        Button deleteSmth = new Button("Удалить элемент");
        Button saveGraph = new Button("Сохранить граф");
        Button loadGraph = new Button("Загрузить граф");

        final String buttonStyle =
                        "-fx-background-color: #5abff2;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 1.2em;" +
                        "-fx-font-weight: bold;" +
                        "-fx-pref-width: 300px;" +
                        "-fx-pref-height: 40px;" +
                        "-fx-border-color: #0575ad;" +
                        "-fx-border-width: 4px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;";

        for (Button button : Arrays.asList(fastResult, stepsResult, addVertex, addEdge, deleteSmth, saveGraph, loadGraph)) {
            button.setStyle(buttonStyle);

            button.setMaxWidth(Double.MAX_VALUE);
            button.setMaxHeight(Double.MAX_VALUE);

            button.setOnMousePressed(e -> {
                button.setStyle(button.getStyle() +
                        "-fx-background-color: #0575ad;" +
                        "-fx-text-fill: black;"
                );
            });
            button.setOnMouseReleased(e -> {
                button.setStyle(buttonStyle);
            });

            VBox.setVgrow(button, Priority.ALWAYS);
            vbox.getChildren().add(button);
        }

        return vbox;
    }
    private BorderPane textProgramArea(String name){
        BorderPane borderPane = new BorderPane();

        Label titleLabel = new Label(name);
        titleLabel.setStyle(
                        "-fx-alignment: center;"+
                        "-fx-font-size: 1.5em;" +
                        "-fx-font-weight: 900;"+
                        "-fx-text-fill: #0575ad;" +
                        "-fx-padding: 7px;" +
                        "-fx-background-color: #cce4ef;" +
                        "-fx-border-width: 0 0 0 0;"
        );

        borderPane.setTop(titleLabel);

        TextArea answer = new TextArea();
        answer.setEditable(false);
        answer.setWrapText(true);
        //answer.setFocusTraversable(false);
        answer.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #0575ad;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-width: 4.0;" +
                        "-fx-padding: 12px;" +
                        "-fx-font-size: 1.5em;" +
                        "-fx-text-fill: black;" +
                        "-fx-cursor: default;"
        );

        answer.setText("Пример оформленного вывода:\n" +
                " Первая строка\n" +
                " Вторая строка\n"+
                " Третья строка!\n"+
                "4\n\n\n\n\n\n");

        borderPane.setCenter(answer);
        return borderPane;
    }

}

/*
        ScrollPane graphCanvas = new ScrollPane();
        graphCanvas.setMinHeight(300);
        graphCanvas.setMinWidth(730);
        graphCanvas.setPannable(true);
        graphCanvas.setMaxWidth(Double.MAX_VALUE);
        graphCanvas.setMaxHeight(Double.MAX_VALUE);
        gridpane.add(graphCanvas, 1, 0);
        GridPane.setHgrow(graphCanvas, Priority.ALWAYS);
        GridPane.setVgrow(graphCanvas, Priority.ALWAYS);
        GridPane.setMargin(graphCanvas, new Insets(30, 20, 0, 20));*/

/*
    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Kosaraju's Algorithm");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(800);

        AnchorPane anchorPane = new AnchorPane();
        VBox leftButtons = buttonsForLeftSide();
        AnchorPane.setTopAnchor(leftButtons, 10.0);
        AnchorPane.setLeftAnchor(leftButtons, 10.0);
        AnchorPane.setBottomAnchor(leftButtons, 250.0);

// Правый ScrollPane
        ScrollPane graphCanvas = new ScrollPane();
        graphCanvas.setPannable(true);
        AnchorPane.setTopAnchor(graphCanvas, 10.0);
        AnchorPane.setRightAnchor(graphCanvas, 10.0);
        AnchorPane.setBottomAnchor(graphCanvas, 250.0);

// Динамическая привязка: левый край graphCanvas = правый край leftButtons + промежуток
        graphCanvas.layoutXProperty().bind(
                leftButtons.layoutXProperty()
                        .add(leftButtons.widthProperty())
                        .add(10) // промежуток между элементами
        );

        // Текстовое поле снизу слева:
        TextField leftTextField = new TextField();
        leftTextField.setPromptText("Слева снизу");
        TextField rightTextField = new TextField();
        rightTextField.setPromptText("Справа снизу");

        // Добавление элементов в AnchorPane
        anchorPane.getChildren().addAll(leftButtons, graphCanvas, leftTextField, rightTextField);
        AnchorPane.setBottomAnchor(leftTextField, 0.0);
        AnchorPane.setLeftAnchor(leftTextField, 0.0);
        AnchorPane.setRightAnchor(leftTextField, anchorPane.getWidth() - 200); // Динамическая привязка

        // Текстовое поле снизу справа:
        AnchorPane.setBottomAnchor(rightTextField, 0.0);
        AnchorPane.setLeftAnchor(rightTextField, 200.0); // Начинается после VBox
        AnchorPane.setRightAnchor(rightTextField, 0.0);

        Scene scene = new Scene(anchorPane, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }*/