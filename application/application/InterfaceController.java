package application;

import algorithm.SCC;
import algorithm.graph.CustomColor;
import algorithm.graph.Edge;
import algorithm.graph.Graph;
import algorithm.graph.Vertex;
import javafx.animation.PauseTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static application.GraphJsonUtils.loadGraph;

class InterfaceController{
    Stage primaryStage;
    private CustomInterface interfaceApp;
    private Pane graphCanvas;
    private Button deleteAll;
    private Button createVertex;
    private Button createEdge;
    private Button deleteSmth;
    private Button fastResult;
    private Button stepsResult;
    private Button saveGraph;
    private Button loadGraph;

    String styleActiveButton;
    Button activeButton;
    boolean[] isEditMode = {false};

    boolean[] isAlgorithm = {false};
    boolean isFinishAlgoWithTranspose = false;
    private Button playPause;
    private double r = 27;
    private AnimationController animationController;
    private Graph[] baseGraph = {null};

    private int max_vertexes = 20;
    private Graph graphForStart;
    private Image playImage;
    private Image pauseImage;
    private boolean stepByStepMode = false;
    private boolean isFast = false;

    public InterfaceController(CustomInterface interfaceApp, Stage stage){
        this.primaryStage = stage;
        this.interfaceApp = interfaceApp;
        graphCanvas = interfaceApp.getGraphCanvas();
        controlAllButtons();
        animationController = new AnimationController(graphCanvas, interfaceApp);
    }
    private void controlAllButtons(){
        unpackButtons();
        controlDeleteAllBtn();
        controlCreateVertexBtn();
        controlCreateEdgeBtn();
        controlDeleteSmthBtn();
        controlLoadGraphBtn();
        controlSaveGraphBtn();
        controlFastResultBtn();
        controlPlayButton();
        controlStepsResultBtn();
    }
    private void unpackButtons(){
        VBox leftButtons = interfaceApp.getLeftButtons();
        for (Node node : leftButtons.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                String text = button.getText();
                if (text.equals("Очистить граф")){
                    deleteAll = button;
                }
                else if (text.equals("Добавить вершину")){
                    createVertex = button;
                }
                else if (text.equals("Добавить ребро")){
                    createEdge = button;
                }
                else if (text.equals("Удалить элемент")){
                    deleteSmth = button;
                }
                else if (text.equals("Получить результат")){
                    fastResult = button;
                }
                else if (text.equals("Выполнить по шагам")){
                    stepsResult = button;
                }
                else if (text.equals("Сохранить граф")){
                    saveGraph = button;
                }
                else if (text.equals("Загрузить граф")){
                    loadGraph = button;
                }
            }
        }
        StackPane areaForGraph = interfaceApp.getAreaForGraph();
        playPause = (Button) areaForGraph.getChildren().get(1);
    }
    private void checkEditMode(){
        if (activeButton == null) return;
        String text = activeButton.getText();
        if (isEditMode[0]){
            isEditMode[0] = false;
            if (text.equals("Добавить вершину")){
                createVertex.setStyle(styleActiveButton);
                graphCanvas.setOnMouseClicked(null);
            }
            else if (text.equals("Добавить ребро")){
                createEdge.setStyle(styleActiveButton);
                graphCanvas.setOnMouseClicked(null);
            }
            else if (text.equals("Удалить элемент")){
                deleteSmth.setStyle(styleActiveButton);
                graphCanvas.setOnMouseClicked(null);
            }
        }
    }

    private void controlPlayButton(){
        ImageView playPauseImageView = (ImageView) playPause.getGraphic();
        playImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("play.png")));
        pauseImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("pause.png")));

        playPause.setOnAction(e->{
            checkEditMode();
            if (!stepByStepMode){
                if (isFast){
                    isFast = false;
                }
                if (!graphCanvas.getChildren().isEmpty()){
                    if (isFinishAlgoWithTranspose){
                        isFinishAlgoWithTranspose = false;
                        if (baseGraph[0] != null){
                            animationController.transposeEdges(baseGraph[0]);
                            baseGraph[0] = createGraph(graphCanvas);
                            drawGraphFromLoad(baseGraph[0]);
                        }
                    }
                    if (isAlgorithm[0]) {
                        animationController.togglePause();
                        if (animationController.isPaused()) {
                            fastResult.setStyle(interfaceApp.giveResultStyle());
                            playPauseImageView.setImage(playImage);
                        } else {
                            playPauseImageView.setImage(pauseImage);
                        }
                    } else {
                        changeColorNon();
                        stepsResult.setStyle(interfaceApp.giveResultStyle()+"-fx-background-color: #b7c2c2;"+"-fx-border-color: #6a6b71;");
                        fastResult.setStyle(interfaceApp.giveResultStyle()+"-fx-background-color: #b7c2c2;"+"-fx-border-color: #6a6b71;");
                        String log_message = "";
                        Node center1 = interfaceApp.getLog().getCenter();
                        if (center1 != null && center1 instanceof TextArea) {
                            ((TextArea) center1).setText(log_message);
                        }
                        String result_message = "";
                        Node center2 = interfaceApp.getAnswer().getCenter();
                        if (center2 != null && center2 instanceof TextArea) {
                            ((TextArea) center2).setText(result_message);
                        }
                        isAlgorithm[0] = true;
                        playPauseImageView.setImage(pauseImage);
                        for (Node node : graphCanvas.getChildren()) {
                            if (node instanceof StackPane) {
                                StackPane stackpane = (StackPane) node;
                                if (stackpane.getChildren().size() ==  3){
                                    stackpane.getChildren().remove(2);
                                }
                            }
                        }
                        baseGraph[0]= createGraph(graphCanvas);
                        graphForStart = createGraph(graphCanvas);
                        StringBuilder log = new StringBuilder();
                        ArrayList<ArrayList<Integer>> result = SCC.find_SCC(baseGraph[0], log);
                        try {

                            animationController.visualizeAlgorithm(baseGraph[0], 500);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }

                        animationController.getTimeline().setOnFinished(event -> {
                            playPauseImageView.setImage(playImage);
                            isAlgorithm[0] = false;
                            String result_string = "";
                            for (ArrayList<Integer> component : result){
                                result_string += component + "\n";
                            }
                            Node center = interfaceApp.getAnswer().getCenter();
                            if (center != null && center instanceof TextArea) {
                                ((TextArea) center).setText(result_string);
                            }
                            isFinishAlgoWithTranspose = true;
                            changeColorDefault();
                            stepsResult.setStyle(interfaceApp.giveResultStyle());
                            fastResult.setStyle(interfaceApp.giveResultStyle());
                        });
                    }
                }
            }

        });
    }

    private void controlStepsResultBtn(){
        String style = stepsResult.getStyle();
        stepsResult.setOnAction(e -> {
            checkEditMode();
            if (!isAlgorithm[0]) {
                if (!graphCanvas.getChildren().isEmpty()) {
                    if (isFinishAlgoWithTranspose){
                        isFinishAlgoWithTranspose = false;
                        if (baseGraph[0] != null){
                            animationController.transposeEdges(baseGraph[0]);
                            baseGraph[0] = createGraph(graphCanvas);
                            drawGraphFromLoad(baseGraph[0]);
                        }
                    }
                    if (isFast){
                        isFast = false;
                    }

                    if (!stepByStepMode){
                        changeColorNon();
                        playPause.setStyle(interfaceApp.givePlayStyle()+"-fx-background-color: #b7c2c2;"+"-fx-border-color: #6a6b71;");
                        String log_message = "";
                        Node center1 = interfaceApp.getLog().getCenter();
                        if (center1 != null && center1 instanceof TextArea) {
                            ((TextArea) center1).setText(log_message);
                        }
                        String result_message = "";
                        Node center2 = interfaceApp.getAnswer().getCenter();
                        if (center2 != null && center2 instanceof TextArea) {
                            ((TextArea) center2).setText(result_message);
                        }
                        for (Node node : graphCanvas.getChildren()) {
                            if (node instanceof StackPane) {
                                StackPane stackpane = (StackPane) node;
                                if (stackpane.getChildren().size() ==  3){
                                    stackpane.getChildren().remove(2);
                                }
                            }
                        }
                        stepByStepMode = true;
                        stepsResult.setText("Следующий шаг");
                        stepsResult.setStyle(style+ "-fx-background-color: #0575ad;"+"-fx-border-color: #081459;");

                        baseGraph[0] = createGraph(graphCanvas);

                        graphForStart = createGraph(graphCanvas);

                        StringBuilder log = new StringBuilder();
                        SCC.find_SCC(baseGraph[0], log);

                        animationController.resetSteps();
                        animationController.prepareStepExecution(baseGraph[0], 1);

                        boolean has_more_steps = true;
                        for (int i = 0; i < 4; i++) {
                            if (has_more_steps) {
                                has_more_steps = animationController.executeNextStep();
                            }
                        }
                    } else {
                        boolean has_more_steps = true;
                        for (int i = 0; i < 4; i++) {
                            if (has_more_steps) {
                                stepsResult.setText("Следующий шаг");
                                stepsResult.setStyle(style+ "-fx-background-color: #0575ad;");
                                has_more_steps = animationController.executeNextStep();
                            }
                            stepsResult.setText("Следующий шаг");
                        }
                        if (!has_more_steps){
                            stepByStepMode = false;
                            stepsResult.setText("Выполнить по шагам");
                            stepsResult.setStyle(style);
                            StringBuilder log = new StringBuilder();
                            String result_string = "";
                            ArrayList<ArrayList<Integer>> result = SCC.find_SCC(baseGraph[0], log);
                            for (ArrayList<Integer> component : result){
                                result_string += component + "\n";
                            }
                            Node center_result = interfaceApp.getAnswer().getCenter();
                            if (center_result != null && center_result instanceof TextArea) {
                                ((TextArea) center_result).setText(result_string);
                            }
                            animationController.resetSteps();
                            isFinishAlgoWithTranspose = true;
                            changeColorDefault();
                            playPause.setStyle(interfaceApp.givePlayStyle());
                        }
                    }
                }
            }
        });
    }

    private void controlFastResultBtn() {
        String style = fastResult.getStyle();
        fastResult.setOnMousePressed(e -> {
            checkEditMode();
            if (animationController.isPaused() || !isAlgorithm[0] || stepByStepMode){
                Graph graph = null;
                if (isFinishAlgoWithTranspose) {
                    isFinishAlgoWithTranspose = false;
                    if (baseGraph[0] != null) {
                        animationController.transposeEdges(baseGraph[0]);
                        baseGraph[0] = createGraph(graphCanvas);
                        drawGraphFromLoad(baseGraph[0]);
                    }
                } else if (isAlgorithm[0] || stepByStepMode) {
                    changeColorDefault();
                    if (stepByStepMode) playPause.setStyle(interfaceApp.givePlayStyle());
                    if (isAlgorithm[0]) stepsResult.setStyle(interfaceApp.giveResultStyle());

                    Node answer = interfaceApp.getAnswer().getCenter();
                    Node log = interfaceApp.getLog().getCenter();
                    if (answer instanceof TextArea && log instanceof TextArea) {
                        TextArea answerTextArea = (TextArea) answer;
                        TextArea logTextArea = (TextArea) log;
                        answerTextArea.clear();
                        logTextArea.clear();
                    }
                    graphCanvas.getChildren().clear();
                    drawGraphFromLoad(graphForStart);
                    if (isAlgorithm[0]) isAlgorithm[0] = false;
                    else if (stepByStepMode) {
                        stepByStepMode = false;
                        stepsResult.setText("Выполнить по шагам");
                        stepsResult.setStyle(stepsResult.getStyle()+"-fx-background-color: #0c98df;");
                    }
                    isFinishAlgoWithTranspose= false;
                }
                fastResult.setStyle(fastResult.getStyle() + "-fx-background-color: #0575ad;"+"-fx-border-color: #081459;");
                for (Node node : graphCanvas.getChildren()) {
                    if (node instanceof StackPane) {
                        StackPane stackpane = (StackPane) node;
                        if (stackpane.getChildren().size() == 3) {
                            stackpane.getChildren().remove(2);
                        }
                    }
                }
                graph = createGraph(graphCanvas);
                StringBuilder log = new StringBuilder();
                ArrayList<ArrayList<Integer>> result = SCC.find_SCC(graph, log);
                Node center1 = interfaceApp.getLog().getCenter();
                if (center1 != null && center1 instanceof TextArea) {
                    ((TextArea) center1).setText(log.toString());
                }
                String result_string = "";
                for (ArrayList<Integer> component : result) {
                    result_string += component + "\n";
                }
                Node center2 = interfaceApp.getAnswer().getCenter();
                if (center2 != null && center2 instanceof TextArea) {
                    ((TextArea) center2).setText(result_string);
                }
                List<Vertex> vertexes = graph.getVertexList();
                for (Vertex v : vertexes) {
                    Node found = graphCanvas.lookup("#vertex_id" + v.getId());
                    if (found instanceof StackPane) {
                        StackPane stackpane = (StackPane) found;
                        Label numberSCC = new Label(String.valueOf(v.getSCCNumber()));
                        numberSCC.setTextFill(Color.BLACK);
                        numberSCC.setStyle(
                                "-fx-text-fill: #f538b9;" +
                                        "-fx-font-size: 1.6em;" +
                                        "-fx-font-weight: 900;");
                        StackPane.setAlignment(numberSCC, Pos.BOTTOM_RIGHT);
                        StackPane.setMargin(numberSCC, new Insets(0, 7, 7, 0));
                        stackpane.getChildren().add(numberSCC);
                    }
                }
                isFast = true;
            }
        });
        fastResult.setOnMouseReleased(e -> {
            if (animationController.isPaused() || isFast) fastResult.setStyle(style);
        });
    }
    private void changeColorDefault(){
        createEdge.setStyle(interfaceApp.giveEditStyle());
        createVertex.setStyle(interfaceApp.giveEditStyle());
        deleteAll.setStyle(interfaceApp.giveEditStyle());
        deleteSmth.setStyle(interfaceApp.giveEditStyle());

        saveGraph.setStyle(interfaceApp.giveFileStyle());
        loadGraph.setStyle(interfaceApp.giveFileStyle());
    }
    private void changeColorNon(){
        createEdge.setStyle(interfaceApp.giveEditStyle()+"-fx-background-color: #b7c2c2;"+"-fx-border-color: #6a6b71;");
        createVertex.setStyle(interfaceApp.giveEditStyle()+"-fx-background-color: #b7c2c2;"+"-fx-border-color: #6a6b71;");
        deleteAll.setStyle(interfaceApp.giveEditStyle()+"-fx-background-color: #b7c2c2;"+"-fx-border-color: #6a6b71;");
        deleteSmth.setStyle(interfaceApp.giveEditStyle()+"-fx-background-color: #b7c2c2;"+"-fx-border-color: #6a6b71;");

        saveGraph.setStyle(interfaceApp.giveFileStyle()+"-fx-background-color: #b7c2c2;"+"-fx-border-color: #6a6b71;");
        loadGraph.setStyle(interfaceApp.giveFileStyle()+"-fx-background-color: #b7c2c2;"+"-fx-border-color: #6a6b71;");
    }
    private void controlDeleteAllBtn(){

        String style = deleteAll.getStyle();
        deleteAll.setOnMousePressed(e->{
            if (!isAlgorithm[0] && !stepByStepMode){
                checkEditMode();
                isFinishAlgoWithTranspose = false;
                graphCanvas.getChildren().clear();
                deleteAll.setStyle(deleteAll.getStyle() + "-fx-background-color: #0575ad;"+"-fx-border-color: #081459;");
                String log_message = "";
                Node center1 = interfaceApp.getLog().getCenter();
                if (center1 != null && center1 instanceof TextArea) {
                    ((TextArea) center1).setText(log_message);
                }
                String result_message = "";
                Node center2 = interfaceApp.getAnswer().getCenter();
                if (center2 != null && center2 instanceof TextArea) {
                    ((TextArea) center2).setText(result_message);
                }
            }
        });
        deleteAll.setOnMouseReleased(e->{
            if (!isAlgorithm[0] && !stepByStepMode) deleteAll.setStyle(style);
        });
    }
    private void controlDeleteSmthBtn(){
        deleteSmth.setOnAction(e->{
            if (!isAlgorithm[0] && !stepByStepMode){
                if (activeButton != deleteSmth){
                    checkEditMode();
                    activeButton = deleteSmth;
                    styleActiveButton = deleteSmth.getStyle();
                }
                isEditMode[0] = !isEditMode[0];
                if (isEditMode[0]) {
                    deleteSmth.setStyle(deleteSmth.getStyle()+ "-fx-background-color: #0575ad;"+"-fx-border-color: #081459;");
                    graphCanvas.setOnMouseClicked(event ->
                    {
                        double x = event.getX();
                        double y = event.getY();
                        List<Node> nodesToRemove = new ArrayList<>();
                        for (Node node : graphCanvas.getChildren()) {
                            if (node instanceof StackPane) {
                                Bounds bounds = node.getBoundsInParent();
                                if (bounds.contains(x, y)) {
                                    nodesToRemove.add(node);
                                    for (Node edges : graphCanvas.getChildren()){
                                        if (edges instanceof CubicCurve){
                                            CubicCurve edge = (CubicCurve) edges;
                                            if (edge.getProperties().containsKey("source")
                                                    && edge.getProperties().get("source") == node) {
                                                nodesToRemove.add(edge);
                                            }
                                        }
                                        else if (edges instanceof ClassicEdge){
                                            ClassicEdge edge = (ClassicEdge) edges;
                                            if (edge.getProperties().containsKey("source")
                                                    && edge.getProperties().get("source") == node) {
                                                nodesToRemove.add(edge);
                                            }
                                            else if (edge.getProperties().containsKey("target")
                                                    && edge.getProperties().get("target") == node){
                                                nodesToRemove.add(edge);
                                            }
                                        }
                                    }
                                }
                            }
                            else if (node.contains(x, y)) {
                                nodesToRemove.add(node);
                            }
                        }
                        graphCanvas.getChildren().removeAll(nodesToRemove);
                    });
                }
                else {
                    deleteSmth.setStyle(styleActiveButton);
                    activeButton = null;
                    graphCanvas.setOnMouseClicked(null);
                }
            }
        });
    }

    private void controlCreateVertexBtn(){
        createVertex.setOnAction(e ->
        {
            if (!isAlgorithm[0] && !stepByStepMode){

                if (activeButton != createVertex ){
                    checkEditMode();
                    activeButton = createVertex;
                    styleActiveButton = createVertex.getStyle();
                }

                isEditMode[0] = !isEditMode[0];
                if (isEditMode[0]) {
                    createVertex.setStyle(createVertex.getStyle()+ "-fx-background-color: #0575ad;"+"-fx-border-color: #081459;");
                    graphCanvas.setOnMouseClicked(event ->
                    {
                        int[] lb = new int[100];
                        for (int i = 1; i < 101; i++){
                            lb[i-1] = i;
                        }
                        int vertex_counter = 1;
                        for (Node node : graphCanvas.getChildren()) {
                            if (node instanceof StackPane) {
                                if (vertex_counter >= max_vertexes){
                                    Alert alert = createAlert("Максимальное количество вершин = " + max_vertexes + "!");
                                    alert.setAlertType(Alert.AlertType.WARNING);
                                    alert.setTitle("Превышено ограничение");
                                    alert.showAndWait();
                                    return;
                                }
                                vertex_counter++;
                                Text textNode = (Text) ((StackPane) node).getChildren().get(1);
                                String str = textNode.getText();

                                int val = Integer.parseInt(str);
                                lb[val - 1] = 1000;
                            }
                        }
                        OptionalInt new_label = Arrays.stream(lb).min();

                        StackPane container = createContainerVertex(event.getX(), event.getY(), "" + new_label.getAsInt());

                        Text text = (Text) container.getChildren().get(1);

                        container.getProperties().put("vertex_x", event.getX());
                        container.getProperties().put("vertex_y", event.getY());
                        container.getProperties().put("vertex_label", text.getText());

                        graphCanvas.getChildren().add(container);
                    });
                }
                else {
                    createVertex.setStyle(styleActiveButton);
                    activeButton = null;
                    graphCanvas.setOnMouseClicked(null);
                }
            }
        });
    }


    private void controlCreateEdgeBtn(){
        createEdge.setOnAction(e->{
            if (!isAlgorithm[0] && !stepByStepMode){
                if (activeButton != createEdge){
                    checkEditMode();
                    activeButton = createEdge;
                    styleActiveButton = createEdge.getStyle();
                }

                isEditMode[0] = !isEditMode[0];
                if (isEditMode[0]) {
                    createEdge.setStyle(createEdge.getStyle()+ "-fx-background-color: #0575ad;"+"-fx-border-color: #081459;");
                    final StackPane[] selectedNode = {null};
                    graphCanvas.setOnMouseClicked(event ->
                    {
                        StackPane vertex = findStackPaneAt(event.getX(), event.getY());
                        if (vertex != null){
                            String style = "-fx-fill: white; " +
                                    "-fx-stroke: black; " +
                                    "-fx-stroke-width: 3.0;";
                            if (selectedNode[0] == null) {
                                selectedNode[0] = vertex;
                                vertex.getChildren().get(0).setStyle(style+"-fx-stroke: #0d3bc5;"+"-fx-stroke-width: 6.0;");
                            }
                            else {
                                StackPane source = selectedNode[0];
                                StackPane target = vertex;
                                if (source == target){
                                    double centerX = source.getLayoutX() + source.getWidth() / 2;
                                    double centerY = source.getLayoutY() + source.getHeight() / 2;
                                    CubicCurve loop = drawLoopEdge(centerX, centerY, r);
                                    loop.getProperties().put("source", vertex);
                                    loop.getProperties().put("target", vertex);
                                    graphCanvas.getChildren().add(loop);
                                }
                                else{

                                    double[] coordinates = findCoordForEdge(source, target);
                                    ClassicEdge edge = new ClassicEdge(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
                                    edge.getProperties().put("source", source);
                                    edge.getProperties().put("target", target);
                                    graphCanvas.getChildren().add(edge);
                                }
                                selectedNode[0].getChildren().get(0).setStyle(style);
                                selectedNode[0] = null;

                            }
                        }
                    });
                }
                else {
                    createEdge.setStyle(styleActiveButton);
                    activeButton = null;
                    graphCanvas.setOnMouseClicked(null);
                }
            }
        });
    }
    private void controlLoadGraphBtn(){
        String style = loadGraph.getStyle();
        loadGraph.setOnAction(e-> {
            if (!isAlgorithm[0] && !stepByStepMode){
                checkEditMode();
                PauseTransition pause = new PauseTransition(Duration.millis(70));
                loadGraph.setStyle(loadGraph.getStyle()+ "-fx-background-color: #0575ad;"+"-fx-border-color: #081459;");
                pause.setOnFinished(event -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Выберите файл c графом (json файл)");
                    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("json файлы", "*.json");
                    fileChooser.getExtensionFilters().add(filter);
                    File selectedFile = fileChooser.showOpenDialog(primaryStage);

                    if (selectedFile != null) {
                        String filePath = selectedFile.getAbsolutePath();
                        try{
                            Graph graph = loadGraph(filePath);
                            drawGraphFromLoad(graph);
                            isFinishAlgoWithTranspose = false;
                        } catch (IOException ex) {

                            Alert alert = createAlert("Ошибка при загрузке.\nПопробуйте другой файл.");
                            alert.setAlertType(Alert.AlertType.WARNING);
                            alert.initOwner(primaryStage);
                            alert.show();
                        }
                    }
                    loadGraph.setStyle(style);
                });
                pause.play();

            }
        });
    }

    private void controlSaveGraphBtn(){
        String style = saveGraph.getStyle();
        saveGraph.setOnAction(e->{
            if (!isAlgorithm[0] && !stepByStepMode){
                checkEditMode();
                saveGraph.setStyle(saveGraph.getStyle() + "-fx-background-color: #0575ad;"+"-fx-border-color: #081459;");
                PauseTransition pause = new PauseTransition(Duration.millis(70));
                pause.setOnFinished(event -> {
                    Graph graph = createGraph(graphCanvas);
                    String filename = "graph";
                    for (int i = 0; i < 10; i++){
                        int random_num = new Random().nextInt(10);
                        filename += random_num;
                    }
                    filename += ".json";
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Сохранить файл");
                    fileChooser.setInitialFileName(filename);

                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("json", "*.json")
                    );

                    File file = fileChooser.showSaveDialog(primaryStage);
                    if (file != null) {
                        try {
                            String fullPath = file.getAbsolutePath();
                            GraphJsonUtils.saveGraph(graph, fullPath);
                            Alert alert = createAlert("Граф успешно сохранён!");
                            alert.initOwner(primaryStage);
                            alert.show();
                        } catch (IOException ex) {
                            Alert alert = createAlert("Ошибка сохранения файла!");
                            alert.setAlertType(Alert.AlertType.WARNING);
                            alert.initOwner(primaryStage);
                            alert.show();
                        }
                    }
                    saveGraph.setStyle(style);
                });
                pause.play();
            }
        });
    }

    private StackPane createContainerVertex(double x, double y, String label){
        Circle circle = new Circle(r);
        circle.setStyle(
                "-fx-fill: white; " +
                        "-fx-stroke: black; " +
                        "-fx-stroke-width: 3.0;"
        );

        Text text = new Text(label);
        text.setStyle("-fx-font-size: 1.3em;");

        StackPane container = new StackPane();
        container.getChildren().addAll(circle, text);

        container.setLayoutX(x - r);
        container.setLayoutY(y - r);

        container.getProperties().put("vertex_x", x);
        container.getProperties().put("vertex_y", y);
        container.getProperties().put("vertex_label", text.toString());
        return container;
    }
    private Alert createAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        Text text = new Text(message);
        text.setStyle("-fx-font-size: 14px; -fx-fill: #0575ad; -fx-font-weight: bold;");
        StackPane container = new StackPane(text);
        container.setPadding(new Insets(10));
        container.setAlignment(Pos.CENTER);
        alert.getDialogPane().setContent(container);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #cce4ef;" +
                        "-fx-border-color: #0575ad;" +
                        "-fx-border-width: 3px;"
        );
        ButtonType buttonOk = new ButtonType("Ок", ButtonBar.ButtonData.OTHER);
        alert.getButtonTypes().setAll(buttonOk);
        Button btnOk= (Button) alert.getDialogPane().lookupButton(buttonOk);
        String buttonStyle =
                "-fx-border-color: #0575ad;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        " -fx-text-fill: #0575ad; " +
                        "-fx-font-weight: bold;" +
                        " -fx-background-radius: 5px;";
        btnOk.setStyle(buttonStyle);
        btnOk.setFocusTraversable(false);
        return alert;
    }

    private static Graph createGraph(Pane graphCanvas){
        ArrayList<Vertex> vertexes = new ArrayList<>();
        ArrayList<Edge> edges = new ArrayList<>();

        int[] lb = new int[100];
        for (int i = 1; i < 101; i++){
            lb[i - 1] = i;
        }
        int counter = 0;
        for (Node node : graphCanvas.getChildren()){
            if (node instanceof StackPane) {
                counter++;
                StackPane container = (StackPane) node;
                String label = (String) container.getProperties().get("vertex_label");
                lb[Integer.parseInt(label) - 1] = 0;
            }
        }
        int flag = 0;
        for (int i = 0; i < counter; i++){
            if (lb[i] != 0){
                flag = 1;
                break;
            }
        }

        counter = 0;
        for (Node node : graphCanvas.getChildren()) {
            if (node instanceof StackPane) {
                counter++;
                StackPane container = (StackPane) node;
                double x = (Double) container.getProperties().get("vertex_x");
                double y = (Double) container.getProperties().get("vertex_y");
                String label = (String) container.getProperties().get("vertex_label");
                int id = Integer.parseInt(label);
                if (flag == 1){
                    id = counter;
                }
                container.getProperties().put("vertex_id", id);
                container.setId("vertex_id"+id);

                Vertex vertex = new Vertex(id, label, x, y);
                vertexes.add(vertex);
            }
        }

        for (Node node : graphCanvas.getChildren()) {
            if (node instanceof ClassicEdge || node instanceof CubicCurve) {
                StackPane source = (StackPane) node.getProperties().get("source");
                StackPane target = (StackPane) node.getProperties().get("target");

                if (source != null && target != null) {
                    Integer source_id = (Integer) source.getProperties().get("vertex_id");
                    Integer target_id = (Integer) target.getProperties().get("vertex_id");
                    node.setId("edge_id"+source_id+"_"+target_id);
                    Edge edge = new Edge(source_id, target_id, CustomColor.BLACK);
                    edges.add(edge);
                }
            }
        }
        return new Graph(edges, vertexes);
    }
    private void drawGraphFromLoad(Graph graph){
        if (!(graphCanvas.getChildren().isEmpty())){
            graphCanvas.getChildren().clear();
        }
        List<Vertex> vertexes = graph.getVertexList();
        List<Edge> edges = graph.getEdgeList();
        List<StackPane> drawVertexes = new ArrayList<StackPane>();
        for (Vertex v : vertexes){
            double x = v.getX();
            double y = v.getY();
            StackPane container = createContainerVertex(x, y, v.getLabel());
            Text text = (Text) container.getChildren().get(1);

            container.getProperties().put("vertex_x", x);
            container.getProperties().put("vertex_y", y);
            container.getProperties().put("vertex_label", text.getText());
            drawVertexes.add(container);
            graphCanvas.getChildren().add(container);
        }

        for (Edge e : edges){
            int target = e.getTarget() - 1;
            int source = e.getSource() - 1;
            Vertex v1 = vertexes.get(source);
            Vertex v2 = vertexes.get(target);
            if (v1 != v2){
                double[] coord = findCoordForDrawEdge(v1, v2);
                ClassicEdge edge = new ClassicEdge(coord[0], coord[1], coord[2], coord[3]);
                edge.getProperties().put("source",drawVertexes.get(source));
                edge.getProperties().put("target", drawVertexes.get(target));
                graphCanvas.getChildren().add(edge);
            }
            else{
                CubicCurve loop = drawLoopEdge(v1.getX(), v1.getY(), r);
                loop.getProperties().put("source", drawVertexes.get(source));
                loop.getProperties().put("target", drawVertexes.get(target));
                graphCanvas.getChildren().add(loop);
            }
        }
    }
    private  double[] findCoordForDrawEdge(Vertex v1, Vertex v2){
        double dx = v2.getX() - v1.getX();
        double dy = v2.getY() - v1.getY();
        double distance = Math.hypot(dx, dy);
        double unitX = dx / distance;
        double unitY = dy / distance;

        double startX = v1.getX() + unitX * r;
        double startY = v1.getY()+ unitY * r;
        double endX = v2.getX() - unitX * r;
        double endY = v2.getY() - unitY * r;

        return new double[]{startX, startY, endX, endY};
    }

    private StackPane findStackPaneAt(double x, double y) {
        for (Node node : graphCanvas.getChildren()) {
            if (node instanceof StackPane) {
                Bounds bounds = node.getBoundsInParent();
                if (bounds.contains(x, y)) {
                    return (StackPane) node;
                }
            }
        }
        return null;
    }

    private double[] findCoordForEdge(StackPane source, StackPane target){
        double centerX1 = source.getLayoutX() + source.getWidth() / 2;
        double centerY1 = source.getLayoutY() + source.getHeight() / 2;
        double centerX2 = target.getLayoutX() + target.getWidth() / 2;
        double centerY2 = target.getLayoutY() + target.getHeight() / 2;

        double dx = centerX2 - centerX1;
        double dy = centerY2 - centerY1;
        double distance = Math.hypot(dx, dy);
        double unitX = dx / distance;
        double unitY = dy / distance;

        double startX = centerX1 + unitX * r;
        double startY = centerY1 + unitY * r;
        double endX = centerX2 - unitX * r;
        double endY = centerY2 - unitY * r;

        return new double[]{startX, startY, endX, endY};
    }
    private CubicCurve drawLoopEdge(double centerX, double centerY, double r){

        double angle = 45;
        double startX = centerX - r * Math.cos(Math.toRadians(angle));
        double startY = centerY + r * Math.sin(Math.toRadians(angle));

        double controlX1 = startX + 50 * Math.cos(Math.toRadians(180));
        double controlY1 = startY - 50 * Math.sin(Math.toRadians(180));

        double controlX2 = startX + 50 * Math.cos(Math.toRadians(angle + 180 + 45));
        double controlY2 = startY - 50 * Math.sin(Math.toRadians(angle + 180 + 45));

        CubicCurve loop = new CubicCurve(
                startX, startY,
                controlX1, controlY1,
                controlX2, controlY2,
                startX, startY
        );
        loop.setStroke(Color.BLACK);
        loop.setFill(null);
        loop.setStrokeWidth(4);
        return loop;
    }
}