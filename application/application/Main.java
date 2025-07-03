package application;

import algorithm.SCC;
import algorithm.graph.Edge;
import algorithm.graph.Graph;
import algorithm.graph.Vertex;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static application.GraphJsonUtils.loadGraph;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        settingsInterface(primaryStage);
        primaryStage.show();
    }

    private void settingsInterface(Stage primaryStage){
        primaryStage.setTitle("Kosaraju's Algorithm");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(800);

        CustomInterface interfaceApp = new CustomInterface();
        InterfaceController controller = new InterfaceController(interfaceApp, primaryStage);
        GridPane rootPane = interfaceApp.getRootPane();

        Scene scene = new Scene(rootPane, 900, 700);
        primaryStage.setScene(scene);
    }
}

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
    private Button playPause;

    public InterfaceController(CustomInterface interfaceApp, Stage stage){
        this.primaryStage = stage;
        this.interfaceApp = interfaceApp;
        graphCanvas = interfaceApp.getGraphCanvas();
        controlAllLeftButtons();
    }

    private void controlAllLeftButtons(){
        unpackButtons();
        controlDeleteAllBtn();
        controlCreateVertexBtn();
        controlCreateEdgeBtn();
        controlDeleteSmthBtn();
        controlLoadGraphBtn();
        controlSaveGraphBtn();
        controlFastResultBtn();
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
    private void controlDeleteAllBtn(){
        String style = deleteAll.getStyle();
        deleteAll.setOnMousePressed(e->{
            checkEditMode();
            graphCanvas.getChildren().clear();
            deleteAll.setStyle(deleteAll.getStyle() +
                    "-fx-background-color: #0575ad;" +
                    "-fx-text-fill: black;"
            );
        });
        deleteAll.setOnMouseReleased(e->{
            deleteAll.setStyle(style);
        });
    }
    private void controlDeleteSmthBtn(){
        deleteSmth.setOnAction(e->{
            if (activeButton != deleteSmth){
                checkEditMode();
                activeButton = deleteSmth;
                styleActiveButton = deleteSmth.getStyle();
            }
            isEditMode[0] = !isEditMode[0];
            if (isEditMode[0]) {
                deleteSmth.setStyle(deleteSmth.getStyle()+
                        "-fx-background-color: #0575ad;" +
                        "-fx-text-fill: black;");
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
        });
    }

    private void controlCreateVertexBtn(){
        createVertex.setOnAction(e ->
        {
            if (activeButton != createVertex ){
                checkEditMode();
                activeButton = createVertex;
                styleActiveButton = createVertex.getStyle();
            }

            isEditMode[0] = !isEditMode[0];
            if (isEditMode[0]) {
                createVertex.setStyle(createVertex.getStyle()+
                        "-fx-background-color: #0575ad;" +
                        "-fx-text-fill: black;");
                graphCanvas.setOnMouseClicked(event ->
                {
                    int[] lb = new int[100];
                    for (int i = 1; i < 101; i++){
                        lb[i-1] = i;
                    }
                    for (Node node : graphCanvas.getChildren()) {
                        if (node instanceof StackPane) {
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
        });
    }
    private StackPane createContainerVertex(double x, double y, String label){
        Circle circle = new Circle(30);
        circle.setStyle(
                        "-fx-fill: white; " +
                        "-fx-stroke: black; " +
                        "-fx-stroke-width: 3.0;"
        );

        Text text = new Text(label);
        text.setStyle("-fx-font-size: 1.3em;");

        StackPane container = new StackPane();
        container.getChildren().addAll(circle, text);

        container.setLayoutX(x - 30);
        container.setLayoutY(y - 30);

        container.getProperties().put("vertex_x", x);
        container.getProperties().put("vertex_y", y);
        container.getProperties().put("vertex_label", text.toString());
        return container;
    }

    private void controlCreateEdgeBtn(){

        createEdge.setOnAction(e->{
            if (activeButton != createEdge){
                checkEditMode();
                activeButton = createEdge;
                styleActiveButton = createEdge.getStyle();
            }

            isEditMode[0] = !isEditMode[0];
            if (isEditMode[0]) {
                createEdge.setStyle(createEdge.getStyle()+
                        "-fx-background-color: #0575ad;" +
                        "-fx-text-fill: black;");
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
                                CubicCurve loop = drawLoopEdge(centerX, centerY, 30);
                                loop.getProperties().put("source", vertex);
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
        });
    }
    private void controlLoadGraphBtn(){

        loadGraph.setOnMousePressed(e-> {
            checkEditMode();
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
                }
                catch (IOException ex) {
                }
            }
        });
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
                double[] coord = findCoordFroDrawEdge(v1, v2);
                ClassicEdge edge = new ClassicEdge(coord[0], coord[1], coord[2], coord[3]);
                edge.getProperties().put("source",drawVertexes.get(source));
                edge.getProperties().put("target", drawVertexes.get(target));
                graphCanvas.getChildren().add(edge);
            }
            else{
                CubicCurve loop = drawLoopEdge(v1.getX(), v1.getY(), 30);
                loop.getProperties().put("source", drawVertexes.get(source));
                graphCanvas.getChildren().add(loop);
            }
        }
    }
    private  double[] findCoordFroDrawEdge(Vertex v1, Vertex v2){
        double r = 30;

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
    private void controlSaveGraphBtn(){
        String style = saveGraph.getStyle();
        saveGraph.setOnMousePressed(e->{
            checkEditMode();
            saveGraph.setStyle(saveGraph.getStyle() +
                    "-fx-background-color: #0575ad;" +
                    "-fx-text-fill: black;"
            );
            Graph graph = createGraph(graphCanvas);
            String filename = "graph";
            for (int i = 0; i < 10; i++){
                int random_num = new Random().nextInt(10);
                filename += random_num;
            }
            filename += ".json";
            try {
                GraphJsonUtils.saveGraph(graph, filename);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        saveGraph.setOnMouseReleased(e->{
            saveGraph.setStyle(style);
        });
    }
    private void controlFastResultBtn(){
        String style = fastResult.getStyle();
        fastResult.setOnMousePressed(e->{
            checkEditMode();
            fastResult.setStyle(fastResult.getStyle()+
                    "-fx-background-color: #0575ad;" +
                    "-fx-text-fill: black;");
            for (Node node : graphCanvas.getChildren()) {
                if (node instanceof StackPane) {
                    StackPane stackpane = (StackPane) node;
                    if (stackpane.getChildren().size() ==  3){
                        stackpane.getChildren().remove(2);
                    }
                }
            }
            Graph graph = createGraph(graphCanvas);
            StringBuilder log = new StringBuilder();
            ArrayList<ArrayList<Integer>> result = SCC.find_SCC(graph, log);
            Node center1 = interfaceApp.getLog().getCenter();
            if (center1 != null && center1 instanceof TextArea) {
                ((TextArea) center1).setText(log.toString());
            }
            String result_string = "";
            for (ArrayList<Integer> component : result){
                result_string += component + "\n";
            }
            Node center2 = interfaceApp.getAnswer().getCenter();
            if (center2 != null && center2 instanceof TextArea) {
                ((TextArea) center2).setText(result_string);
            }

            List<Vertex> vertexes = graph.getVertexList();
            List<Double> coordX = new ArrayList<Double>();
            List<Double> coordY = new ArrayList<Double>();
            for (Vertex v : vertexes){
                coordX.add(v.getX());
                coordY.add(v.getY());
            }
            for (Node node : graphCanvas.getChildren()){
                if (node instanceof StackPane){
                    StackPane stackpane = (StackPane) node;
                    for (int i=0; i<coordY.size(); i++){
                        if (stackpane.getProperties().containsKey("vertex_x") &&
                                stackpane.getProperties().get("vertex_x").equals(coordX.get(i)) &&
                                stackpane.getProperties().get("vertex_y").equals(coordY.get(i))){
                            Label numberSCC = new Label(String.valueOf(vertexes.get(i).getSCCNumber()));
                            numberSCC.setTextFill(Color.BLACK);
                            numberSCC.setStyle(
                                    "-fx-text-fill: #f538b9;" +
                                    "-fx-font-size: 1.6em;" +
                                    "-fx-font-weight: 900;" );
                            StackPane.setAlignment(numberSCC, Pos.BOTTOM_RIGHT);
                            StackPane.setMargin(numberSCC, new Insets(0, 10, 10, 0));
                            stackpane.getChildren().add(numberSCC);
                            break;
                        }
                    }
                }
            }

        });
        fastResult.setOnMouseReleased(e->{
            fastResult.setStyle(style);
        });
    }
    private static Graph createGraph(Pane graphCanvas){
        ArrayList<Vertex> vertexes = new ArrayList<>();
        ArrayList<Edge> edges = new ArrayList<>();

        for (Node node : graphCanvas.getChildren()) {
            if (node instanceof StackPane) {
                StackPane container = (StackPane) node;
                double x = (Double) container.getProperties().get("vertex_x");
                double y = (Double) container.getProperties().get("vertex_y");
                String label = (String) container.getProperties().get("vertex_label");
                int id = Integer.parseInt(label);
                container.getProperties().put("vertex_id", id);

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

                    Edge edge = new Edge(source_id, target_id, algorithm.graph.Color.BLACK);
                    edges.add(edge);
                }
            }
        }
        Graph graph = new Graph(edges, vertexes);
        return graph;
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
        double r = 30;

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

class ClassicEdge extends Group{
    private Line line;
    private Line leftWing;
    private Line rightWing;

    public ClassicEdge(double startX, double startY, double endX, double endY){
        line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(4);

        double angle = Math.atan2(endY - startY, endX - startX);
        double arrowLength = 10;
        double arrowAngle = Math.toRadians(30);

        double x1 = endX - arrowLength * Math.cos(angle - arrowAngle);
        double y1 = endY - arrowLength * Math.sin(angle - arrowAngle);

        double x2 = endX - arrowLength * Math.cos(angle + arrowAngle);
        double y2 = endY - arrowLength * Math.sin(angle + arrowAngle);

        leftWing = new Line(endX, endY, x1, y1);
        rightWing = new Line(endX, endY, x2, y2);

        leftWing.setStroke(Color.BLACK);
        rightWing.setStroke(Color.BLACK);

        leftWing.setStrokeWidth(4);
        rightWing.setStrokeWidth(4);
        getChildren().addAll(line, leftWing, rightWing);
    }
}

