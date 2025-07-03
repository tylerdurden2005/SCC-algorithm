package algorithm.graph;

import java.util.ArrayList;
import com.google.gson.annotations.Expose;

public class Graph {
    @Expose
    ArrayList <Edge> edges;
    @Expose
    ArrayList <Vertex> vertexes;

    public Graph() {}

    public Graph(ArrayList<Edge> edges, ArrayList<Vertex> vertexes){
        this.edges = edges;
        this.vertexes = vertexes;
        sortLists();
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

    public void printGraph(){
        System.out.println("vertexes:");
        for (Vertex v : vertexes){
            System.out.println(v.getId() + " (in: " + v.getEntryTime() + ", out: " + v.getExitTime() +
                    ", stack: " + v.getStackNumber() + ", color: " + v.getColor() + ", SCC: " + v.getSCCNumber() + ", coord: (" +
                    v.getX() + ", " + v.getY() + "))");
        }
        System.out.println("\nedges:");
        for (Edge e : edges){
            System.out.println("(" + e.getSource() + ", " + e.getTarget() + ") type: " + e.getEdgeType());
        }
        System.out.println();
    }


}
