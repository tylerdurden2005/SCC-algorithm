import graph.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        // пример для тестирования 1 (КСС: {1, 2, 3}, {4, 5}, {6})
        Vertex[] vertex_list1 = {
                new Vertex(1),
                new Vertex(2),
                new Vertex(3),
                new Vertex(4),
                new Vertex(5),
                new Vertex(6)
        };
        ArrayList<Vertex> vertexes1 = new ArrayList<>(Arrays.asList(vertex_list1));

        Edge[] edge_array1 = {
                new Edge(1, 2),
                new Edge(2, 3),
                new Edge(3, 1),
                new Edge(3, 4),
                new Edge(4, 5),
                new Edge(5, 4),
                new Edge(6, 5)
        };
        ArrayList<Edge> edges1 = new ArrayList<>(Arrays.asList(edge_array1));

        Graph graph1 = new Graph(edges1, vertexes1);

        ArrayList<ArrayList<Integer>> result1 = SCC.find_SCC(graph1);
        System.out.println("Результат для графа 1:");
        for (ArrayList<Integer> component : result1){
            for (Integer elem : component){
                System.out.print(elem + " ");
            }
            System.out.println();
        }
        System.out.println();

        // пример для тестирования 2 (КСС: {1, 2, 3, 4}, {5}, {6}, {7})
        Vertex[] vertex_list2 = {
                new Vertex(1),
                new Vertex(2),
                new Vertex(3),
                new Vertex(4),
                new Vertex(5),
                new Vertex(6),
                new Vertex(7)
        };
        ArrayList<Vertex> vertexes2 = new ArrayList<>(Arrays.asList(vertex_list2));

        Edge[] edge_array2 = {
                new Edge(1, 2),
                new Edge(1, 3),
                new Edge(2, 3),
                new Edge(3, 4),
                new Edge(4, 1),
                new Edge(4, 2),
                new Edge(4, 4),
                new Edge(4, 5),
                new Edge(5, 6),
                new Edge(7, 3),
                new Edge(7, 6)
        };
        ArrayList<Edge> edges2 = new ArrayList<>(Arrays.asList(edge_array2));

        Graph graph2 = new Graph(edges2, vertexes2);

        ArrayList<ArrayList<Integer>> result2 = SCC.find_SCC(graph2);
        System.out.println("Результат для графа 2:");
        for (ArrayList<Integer> component : result2){
            for (Integer elem : component){
                System.out.print(elem + " ");
            }
            System.out.println();
        }
        System.out.println();

        // пример для тестирования 3 (КСС: {1})
        Vertex[] vertex_list3 = {
                new Vertex(1)
        };
        ArrayList<Vertex> vertexes3 = new ArrayList<>(Arrays.asList(vertex_list3));

        Edge[] edge_array3 = {
                new Edge(1, 1)
        };
        ArrayList<Edge> edges3 = new ArrayList<>(Arrays.asList(edge_array3));

        Graph graph3 = new Graph(edges3, vertexes3);

        ArrayList<ArrayList<Integer>> result3 = SCC.find_SCC(graph3);
        System.out.println("Результат для графа 3:");
        for (ArrayList<Integer> component : result3){
            for (Integer elem : component){
                System.out.print(elem + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
