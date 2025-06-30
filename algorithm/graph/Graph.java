package graph;

import java.util.ArrayList;

public class Graph {
    ArrayList <Edge> edges;
    ArrayList <Vertex> vertexes;

    public Graph(ArrayList<Edge> edges, ArrayList<Vertex> vertexes){
        this.edges = edges;
        this.vertexes = vertexes;
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
}
