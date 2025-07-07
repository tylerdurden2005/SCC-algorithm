package application;

import algorithm.Element;
import algorithm.Operations;
import algorithm.graph.*;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class AnimationController {

    private Pane graphCanvas;
    private Timeline timeline;
    private boolean isPaused = false;
    private CustomInterface interfaceApp;
    private int currentStep = 0;
    private List<Runnable> stepActions = new ArrayList<>();

    public AnimationController(Pane pane, CustomInterface interfaceApp){
        graphCanvas = pane;
        this.interfaceApp = interfaceApp;
    }
    private String giveColor(CustomColor color){
        switch (color) {
            case BLACK:
                return "black";
            case GRAY:
                return "gray";
            case LIGHTGRAY:
                return "lightgray";
            case WHITE:
                return "white";
            case GREEN:
                return "green";
            case CYAN:
                return "skyblue";
            case BLUE:
                return "steelblue";
            case PURPLE:
                return "mediumpurple";
            case LIGHTGREEN:
                return "darkseagreen";
            default:
                return "black";
        }
    }

    public void prepareStepExecution(Graph graph, int millis){
        stepActions.clear();
        ArrayList<Pair<Operations, Element>> steps = graph.getSteps().getOperationSequence();
        ArrayList<CustomColor> colors = graph.getSteps().getColors();
        ArrayList<String> logs = graph.getSteps().getLogs();
        int color_counter = 0;
        int flag = 0;
        for (int i = 0; i < steps.size(); i++) {
            final int step_index = i;
            Operations current_operation = steps.get(i).getKey();
            Element element = steps.get(i).getValue();
            final int final_color_counter = color_counter;

            if (current_operation == Operations.COLOR) {
                color_counter++;
            }
            if (current_operation == Operations.TRANSPOSE){
                flag = 1;
            }
            int final_flag = flag;
            stepActions.add(() -> processOperation(current_operation, element, colors, final_color_counter,
                    graph, step_index, logs, millis, final_flag));
        }
    }

    public boolean executeNextStep() {
        if (currentStep < stepActions.size()) {
            stepActions.get(currentStep).run();
            currentStep++;
            return true;
        }
        return false;
    }

    public void resetSteps() {
        currentStep = 0;
        stepActions.clear();
    }

    public void transposeEdges(Graph graph){
        defaultColorAll(graph);
        List<Edge> edges = graph.getEdgeList();
        for (Edge e : edges){
            Node found = graphCanvas.lookup("#edge_id"+e.getSource()+"_"+e.getTarget());
            if (found instanceof ClassicEdge){
                ClassicEdge edge = (ClassicEdge) found;
                ClassicEdge transposeEdge = new ClassicEdge(edge.getEndX(), edge.getEndY(), edge.getStartX(), edge.getStartY());
                StackPane found1 = (StackPane)edge.getProperties().get("source");
                StackPane found2 = (StackPane)edge.getProperties().get("target");
                transposeEdge.getProperties().put("source", found2);
                transposeEdge.getProperties().put("target", found1);
                int id1 = (int)found2.getProperties().get("vertex_id");
                int id2 = (int)found1.getProperties().get("vertex_id");
                transposeEdge.setId("edge_id"+id1+"_"+id2);
                graphCanvas.getChildren().removeAll(found);
                graphCanvas.getChildren().add(transposeEdge);
            }
        }
        graph.transpose();
    }
    private void uncolorVertexContour(Vertex vertex){
        Node found = graphCanvas.lookup("#vertex_id"+vertex.getId());
        if (found instanceof StackPane){
            StackPane v  =(StackPane) found;
            Circle circle = (Circle) v.getChildren().get(0);
            circle.setStyle(circle.getStyle()+";-fx-stroke-width: 3.0;"+  "-fx-stroke: black;");
        }
    }

    private void uncolorVertexBackground(Vertex vertex){
        Node found = graphCanvas.lookup("#vertex_id"+vertex.getId());
        if (found instanceof StackPane){
            StackPane v  =(StackPane) found;
            Circle circle = (Circle) v.getChildren().get(0);
            circle.setStyle(circle.getStyle()+";-fx-fill: white;");
        }
    }

    private void colorVertexBackground(Vertex vertex, CustomColor customColor){
        String color = giveColor(customColor);
        Node found = graphCanvas.lookup("#vertex_id"+vertex.getId());
        if (found instanceof StackPane){
            StackPane v  =(StackPane) found;
            Circle circle = (Circle) v.getChildren().get(0);
            circle.setStyle( circle.getStyle()+";-fx-fill: " + color +";");
        }
    }

    private void colorVertexContour(Vertex vertex, CustomColor customColor){
        String color = giveColor(customColor);
        Node found = graphCanvas.lookup("#vertex_id"+vertex.getId());
        if (found instanceof StackPane){
            StackPane v  =(StackPane) found;
            Circle circle = (Circle) v.getChildren().get(0);
            circle.setStyle( circle.getStyle()+
                    ";-fx-stroke: " + color +";"+
                    "-fx-stroke-width: 4.0;");
        }
    }
    private void colorEdge(Edge edge, CustomColor customColor){
        String color = giveColor(customColor);
        Node found = graphCanvas.lookup("#edge_id"+edge.getSource()+"_"+edge.getTarget());
        if (found instanceof ClassicEdge){
            ClassicEdge e = (ClassicEdge) found;
            e.setAnotherColorLines(Color.valueOf(color));
        }
        else if (found instanceof CubicCurve){
            CubicCurve loop = (CubicCurve) found;
            loop.setStroke(Color.valueOf(color));
        }
    }
    private void uncolorEdge(Edge edge){
        Node found = graphCanvas.lookup("#edge_id"+edge.getSource()+"_"+edge.getTarget());
        if (found instanceof ClassicEdge){
            ClassicEdge e = (ClassicEdge) found;
            e.setAnotherColorLines(Color.BLACK);
        }
        else if (found instanceof CubicCurve){
            CubicCurve loop = (CubicCurve) found;
            loop.setStroke(Color.BLACK);
        }
    }

    private void addNumber(Vertex vertex, int flag){
        Node found = graphCanvas.lookup("#vertex_id"+vertex.getId());
        if (found instanceof StackPane){
            StackPane stackpane = (StackPane)  found;
            Label number;
            if (flag == 0){
                number = new Label(String.valueOf(vertex.getBypassNumber()));
            } else {
                number = new Label(String.valueOf(vertex.getSCCNumber()));
            }
            number.setTextFill(Color.BLACK);
            number.setStyle(
                    "-fx-text-fill: #f538b9;" +
                            "-fx-font-size: 1.6em;" +
                            "-fx-font-weight: 900;" );
            StackPane.setAlignment(number, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(number, new Insets(0, 7, 7, 0));
            stackpane.getChildren().add(number);
        }
    }
    private void removeNumber(Vertex vertex) {
        Node found = graphCanvas.lookup("#vertex_id" + vertex.getId());
        if (found instanceof StackPane) {
            StackPane stackPane = (StackPane) found;

            List<Node> nodesToRemove = new ArrayList<>();

            for (Node child : stackPane.getChildren()) {
                if (child instanceof Label) {
                    if (child.getStyle().contains("-fx-text-fill: #f538b9;")) {
                        nodesToRemove.add(child);
                    }
                }
            }
            stackPane.getChildren().removeAll(nodesToRemove);
        }
    }
    private void defaultColorAll(Graph graph){
        List<Vertex> vertexes = graph.getVertexList();
        List<Edge> edges = graph.getEdgeList();
        for (Vertex v : vertexes){
            uncolorVertexBackground(v);
            uncolorVertexContour(v);
            removeNumber(v);
        }
        for (Edge e : edges){
            uncolorEdge(e);
        }
    }

    private void colorElement(Element element, CustomColor color){
        if (element instanceof Vertex){
            colorVertexBackground((Vertex) element, color);
        } else {
            colorEdge((Edge) element, color);
        }
    }


    public void visualizeAlgorithm(Graph graph, int millis) throws InterruptedException {
        if (timeline != null) {
            timeline.stop();
        }
        isPaused = false;
        timeline = new Timeline();
        ArrayList<Pair<Operations, Element>> steps = graph.getSteps().getOperationSequence();
        ArrayList<CustomColor> colors = graph.getSteps().getColors();
        ArrayList<String> logs = graph.getSteps().getLogs();
        long delay_millis = 0;
        int color_counter = 0;
        int flag = 0;
        for (int i = 0; i < steps.size(); i++){

            Operations current_operation = steps.get(i).getKey();
            if (current_operation == Operations.TRANSPOSE){
                flag = 1;
            }

            int final_color_counter = color_counter;
            int final_i = i;
            int final_flag = flag;
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(delay_millis),
                    e -> processOperation(current_operation, steps.get(final_i).getValue(),
                            colors, final_color_counter, graph, final_i, logs, millis, final_flag)

            );
            if (current_operation == Operations.COLOR){
                color_counter++;
            }

            timeline.getKeyFrames().add(keyFrame);
            delay_millis += millis;
        }
        timeline.play();
    }

    private void processOperation(Operations operation, Element element, ArrayList<CustomColor> colors, int colorIndex,
                                  Graph graph, int i, ArrayList<String> logs, int millis, int flag) {
        Node center = interfaceApp.getLog().getCenter();
        if (center != null && center instanceof TextArea) {
            TextArea text_area = (TextArea) center;
            if (i == 0) {
                text_area.setText(logs.get(i));
            } else {
                text_area.appendText(logs.get(i));
            }
            text_area.setStyle(text_area.getStyle() +
                    "-fx-transition: all 0.3s ease;" +
                    "-fx-animation: none;"
            );

            Platform.runLater(() -> {
                text_area.positionCaret(text_area.getText().length());
                text_area.setScrollTop(Double.MAX_VALUE);
            });
        }
        switch (operation) {
            case ENTER:
                colorVertexContour((Vertex) element, CustomColor.GREEN);
                if (flag == 0){
                    addNumber((Vertex) element, flag);
                }
                break;
            case LEAVE:
                uncolorVertexContour((Vertex) element);
                break;
            case NUMBER:
                addNumber((Vertex) element, flag);
                break;
            case TRANSPOSE:
                transposeEdges(graph);
                break;
            case GO:
                Edge edge = (Edge) element;
                Vertex sourceVertex = graph.getVertexList().get(edge.getSource() - 1);
                Vertex targetVertex = graph.getVertexList().get(edge.getTarget() - 1);


                colorEdge(edge, CustomColor.GREEN);

                PauseTransition firstHalf = new PauseTransition(Duration.millis(millis / 2));
                firstHalf.setOnFinished(event1 -> {
                    if (sourceVertex != null) {
                        uncolorVertexContour(sourceVertex);
                    }
                    if (targetVertex != null) {
                        colorVertexContour(targetVertex, CustomColor.GREEN);
                    }
                });

                PauseTransition secondHalf = new PauseTransition(Duration.millis(millis / 2));
                secondHalf.setOnFinished(event2 -> {
                    uncolorEdge(edge);
                    if (flag == 0){
                        colorEdge(edge, CustomColor.LIGHTGREEN);
                    }

                });

                firstHalf.play();
                secondHalf.play();
                break;
            case COLOR:
                colorElement(element, colors.get(colorIndex));
                break;
        }
    }

    public void togglePause() {
        if (timeline != null) {
            if (isPaused) {
                timeline.play();
            } else {
                timeline.pause();
            }
            isPaused = !isPaused;
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public Timeline getTimeline() {
        return timeline;
    }

}