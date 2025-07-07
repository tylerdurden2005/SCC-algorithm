package application;

import algorithm.graph.Edge;
import algorithm.graph.Vertex;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import algorithm.graph.Graph;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class GraphJsonUtils {

    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    public static void saveGraph(Graph graph, String filePath) throws IOException {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(graph, writer);
        }
    }

    public static Graph loadGraph(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            Graph graph = gson.fromJson(reader, Graph.class);
            if (graph == null) throw new IOException("Graph is null");
            if (graph.getEdgeList() == null) throw new IOException("Edges list is null");
            if (graph.getVertexList() == null) throw new IOException("Vertexes list is null");
            Set<Integer> vertexIds = new HashSet<>();
            for (Vertex v : graph.getVertexList()) {
                if (v.getId() <= 0) throw new IOException("Vertex ID is null");
                if (v.getId() > graph.getVertexCount()) throw new IOException("Vertex ID is null");
                if (!vertexIds.add(v.getId())) throw new IOException("Duplicate vertex ID: ");
                if (v.getLabel() == null) throw new IOException("Vertex ID is null");
                if (v.getColor() == null) throw new IOException("Vertex ID is null");
                if (v.getX() <=0.0) throw new IOException("Vertex ID is null");
                if (v.getY() <= 0.0) throw new IOException("Vertex ID is null");
            }
            for (Edge e : graph.getEdgeList()) {
                int from = e.getSource();
                int to = e.getTarget();
                if (from>graph.getVertexCount() | to > graph.getVertexCount()) throw new IOException("Vertex ID is null");
                if (from<=0 | to<=0) throw new IOException("Vertex ID is null");
                if (e.getEdgeType()==null) throw new IOException("Vertex ID is null");
                if (e.getColor()==null) throw new IOException("Vertex ID is null");
                if (graph.getVertexList().get(from-1) == null) throw new IOException("Edge source vertex not found");
                if (graph.getVertexList().get(to-1)==null) throw new IOException("Edge target vertex not found");
            }
            graph.sortLists();
            return graph;
        } catch (JsonSyntaxException | JsonIOException e){

            throw new IOException(e);
        }
        catch (IOException e){
            throw new IOException(e);
        }
    }
}

