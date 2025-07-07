package algorithm.graph;

import java.util.ArrayList;

import algorithm.StepwiseExecution;
import com.google.gson.annotations.Expose;

public class Graph extends Element {
    @Expose
    private ArrayList <Edge> edges;
    @Expose
    private ArrayList <Vertex> vertexes;
    private StepwiseExecution steps;

    public Graph(ArrayList<Edge> edges, ArrayList<Vertex> vertexes){
        this.edges = edges;
        this.vertexes = vertexes;
        sortLists();
        this.steps = new StepwiseExecution();
    }

    public void sortLists(){
        sortEdgeList();
        sortVertexList();
    }

    public int getVertexCount(){
        return vertexes.size();
    }

    public ArrayList<Vertex> getVertexList(){
        return vertexes;
    }

    public int getEdgeCount(){
        return edges.size();
    }

    public ArrayList<Edge> getEdgeList(){
        return edges;
    }

    private void sortVertexList(){
        vertexes.sort((v1, v2) -> {
            return Integer.compare(v1.getId(), v2.getId());
        });
    }

    private void sortEdgeList(){
        edges.sort((e1, e2) -> {
            int cmp = Integer.compare(e1.getSource(), e2.getSource());
            if (cmp != 0) return cmp;
            return Integer.compare(e1.getTarget(), e2.getTarget());
        });
    }

    public StepwiseExecution getSteps(){
        return steps;
    }

    public void transpose(){
        for (Edge e : edges){
            int source = e.getSource();
            int target = e.getTarget();
            e.setSource(target);
            e.setTarget(source);
        }
    }
}
