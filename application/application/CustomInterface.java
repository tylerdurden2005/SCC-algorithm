package application;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Arrays;
import java.util.Objects;

public class CustomInterface {
    private  GridPane rootPane;
    private  VBox leftButtons;
    private  StackPane areaForGraph;
    private  BorderPane answer;
    private BorderPane log;
    private Pane graphCanvas;

    private String editStyle;
    private String fileStyle;
    private String playStyle;
    private String resultStyle;

    public CustomInterface(){
        createGridPane();
    }
    private  void createGridPane(){
        rootPane = new GridPane();
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

        leftButtons = buttonsForLeftSide();
        rootPane.add(leftButtons, 0, 0);
        GridPane.setMargin(leftButtons, new Insets(30, 0, 0, 20));

        areaForGraph = graphArea();
        rootPane.add(areaForGraph, 1, 0);
        GridPane.setHgrow(areaForGraph, Priority.ALWAYS);
        GridPane.setVgrow(areaForGraph, Priority.ALWAYS);
        GridPane.setMargin(areaForGraph, new Insets(30, 20, 0, 20));

        answer = textProgramArea("РЕЗУЛЬТАТ:");
        rootPane.add(answer, 0, 1);
        GridPane.setMargin(answer, new Insets(10, 0, 0, 20));

        log = textProgramArea("ЛОГ:");
        rootPane.add(log, 1, 1);
        GridPane.setMargin(log, new Insets(10, 20, 0, 20));
    }

    private StackPane graphArea(){
        StackPane stackPane = new StackPane();

        Pane contentPane = new Pane();
        contentPane.setMinSize(2000, 2000);
        graphCanvas = contentPane;

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMinHeight(300);
        scrollPane.setMinWidth(730);
        scrollPane.setPannable(true);
        scrollPane.setStyle(
                "-fx-border-color: #0575ad; " +
                        "-fx-border-width: 6px; " +
                        "-fx-border-radius: 6px; " +
                        "-fx-background-radius: 8;"+
                        "-fx-border-style: solid;"
        );
        scrollPane.setContent(contentPane);

        Image playImage = new Image(Objects.requireNonNull(CustomInterface.class.getResourceAsStream("play.png")));
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
        playStyle = playButton.getStyle();

        stackPane.getChildren().addAll(scrollPane, playButton);

        StackPane.setAlignment(playButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(playButton, new Insets(0, 0, 20, 10));

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
        Button deleteAll = new Button("Очистить граф");
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

        editStyle = buttonStyle;
        for (Button button : Arrays.asList(fastResult, stepsResult, addVertex, addEdge, deleteSmth, deleteAll, saveGraph, loadGraph)) {
            button.setStyle(buttonStyle);
            String text = button.getText();
            if (text.equals("Получить результат") || text.equals("Выполнить по шагам")){
                button.setStyle(button.getStyle()+"-fx-background-color: #0c98df;");
                resultStyle = button.getStyle();
            }
            if (text.equals("Сохранить граф") || text.equals("Загрузить граф")){
                button.setStyle(button.getStyle()+"-fx-background-color: #d3effd;");
                fileStyle = button.getStyle();
            }
            button.setMaxWidth(Double.MAX_VALUE);
            button.setMaxHeight(Double.MAX_VALUE);
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

        TextArea text_area = new TextArea();
        text_area.setEditable(false);
        text_area.setWrapText(true);

        text_area.setStyle(
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

        if (name.equals("ЛОГ:")){
            text_area.textProperty().addListener((observable, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    boolean atBottom = text_area.getScrollTop() + text_area.getHeight() >=
                            text_area.getMaxHeight() - 10;

                    if (atBottom) {
                        text_area.positionCaret(text_area.getText().length());
                        text_area.setScrollTop(Double.MAX_VALUE);
                    }
                });
            });
        }

        borderPane.setCenter(text_area);
        return borderPane;
    }

    public GridPane getRootPane(){
        return rootPane;
    }

    public VBox getLeftButtons(){
        return leftButtons;
    }

    public BorderPane getAnswer(){
        return answer;
    }

    public BorderPane getLog(){
        return log;
    }

    public StackPane getAreaForGraph(){
        return areaForGraph;
    }

    public Pane getGraphCanvas(){
        return graphCanvas;
    }

    public String giveEditStyle(){
        return editStyle;
    }

    public String giveFileStyle(){
        return fileStyle;
    }

    public String givePlayStyle(){
        return playStyle;
    }

    public String giveResultStyle(){
        return resultStyle;
    }
}
