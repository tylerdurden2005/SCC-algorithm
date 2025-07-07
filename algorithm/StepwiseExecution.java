package algorithm;

import algorithm.graph.*;
import javafx.util.Pair;

import java.util.ArrayList;

public class StepwiseExecution {
    private ArrayList<Pair<Operations, Element>> operation_sequence;
    private ArrayList<CustomColor> colors;
    private ArrayList<String> logs;
    private boolean logs_modified = false;

    public StepwiseExecution(){
        operation_sequence = new ArrayList<>();
        colors = new ArrayList<>();
        logs = new ArrayList<>();
    }

    public ArrayList<Pair<Operations, Element>> getOperationSequence(){
        return operation_sequence;
    }

    public ArrayList<CustomColor> getColors(){
        return colors;
    }

    public ArrayList<String> getLogs() {
        return logs;
    }

    public void enterVertex(Vertex vertex){
        operation_sequence.add(new Pair(Operations.ENTER, vertex));
    }

    public void leaveVertex(Vertex vertex){
        operation_sequence.add(new Pair<>(Operations.LEAVE, vertex));
    }

    public void colorVertex(Vertex vertex, CustomColor color){
        operation_sequence.add(new Pair<>(Operations.COLOR, vertex));
        colors.add(color);
    }

    public void numberVertex(Vertex vertex){
        operation_sequence.add(new Pair<>(Operations.NUMBER, vertex));
    }

    public void goEdge(Edge edge){
        operation_sequence.add(new Pair<>(Operations.GO, edge));
    }

    public void colorEdge(Edge edge, CustomColor color){
        operation_sequence.add(new Pair<>(Operations.COLOR, edge));
        colors.add(color);
    }

    public void transposeGraph(Graph graph){
        operation_sequence.add(new Pair<>(Operations.TRANSPOSE, graph));
    }

    public void addLog(String log){
        logs.add(log);
    }

    public void modifyLogs(){
        if (!logs_modified) {
            String previous_before = logs.get(0);
            for (int i = 1; i < logs.size(); i++) {
                String current_before = logs.get(i);
                logs.set(i, logs.get(i).substring(previous_before.length()));
                previous_before = current_before;
            }
            logs_modified = true;
        }
    }
}
