package application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import algorithm.graph.Graph;
import java.io.*;

public class GraphJsonUtils {

    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    public static void saveGraph(Graph graph, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(graph, writer);
        }
    }

    public static Graph loadGraph(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            Graph graph = gson.fromJson(reader, Graph.class);
            graph.sortLists();
            return graph;
        }
    }
}
