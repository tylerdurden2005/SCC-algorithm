package algorithm;
import algorithm.graph.*;

import java.util.ArrayList;

public class SCC {

    public static ArrayList<ArrayList<Integer>> find_SCC(Graph graph, StringBuilder log){

        boolean[] visited = new boolean[graph.getVertexCount()];
        ArrayList<Integer> exit_order = count_exit_order(graph, visited, log);

        log.append("- Стек: " + exit_order + "\n");
        log.append("- Исходный граф транспонируется\n");
        log.append("- Обходом в глубину транспонированного графа формируются КСС\n");

        Graph transposed_graph = transpose_graph(graph);
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        visited = new boolean[graph.getVertexCount()];

        int counter = 0;
        for (int i = exit_order.size() - 1; i >= 0; i--){
            if (!visited[exit_order.get(i) - 1]){
                counter++;
                log.append("- Формируется компонента сильной связности " + counter + "\n");
                ArrayList<Integer> component = new ArrayList<>();
                dfs(transposed_graph, exit_order.get(i), visited, component, 0, log, 1);

                result.add(component);
            }
        }

        for (int i = 0; i < result.size(); i++){
            for (Integer v : result.get(i)){
                graph.getVertexList().get(v - 1).setSCCNumber(i + 1);
            }
        }
        log.append("- Полученные КСС: " + result + "\n");
        return result;
    }

    private static ArrayList<Integer> count_exit_order(Graph graph, boolean[] visited, StringBuilder log){
        ArrayList<Integer> exit_order = new ArrayList<>();
        int time = 0;
        for (int i = 0; i < visited.length; i++){
            if (!visited[i]){
                time++;
                time = dfs(graph, i + 1, visited, exit_order, time, log, 0);
            }
        }
        return exit_order;
    }

    public static void print_colors(Graph graph){
        for (int i = 0; i < graph.getVertexCount(); i++){
            System.out.println(graph.getVertexList().get(i).getId() + ": " + graph.getVertexList().get(i).getColor());
        }
        System.out.println();
    }

    private static int dfs(Graph graph, int vertex, boolean[] visited, ArrayList<Integer> stack, int time, StringBuilder log, int flag){
        visited[vertex - 1] = true;
        graph.getVertexList().get(vertex - 1).setEntryTime(time);
        graph.getVertexList().get(vertex - 1).setColor(Color.GRAY);
        log.append("- Посещена вершина " + vertex + "\n");

        int edge_index = search_edge(graph, vertex, visited, 0, log, flag);
        while (edge_index != -1){
            time++;
            time = dfs(graph, graph.getEdgeList().get(edge_index).getTarget(), visited, stack, time, log, flag);
            edge_index = search_edge(graph, vertex, visited, edge_index, log, flag);
        }
        time++;
        stack.add(vertex);
        graph.getVertexList().get(vertex - 1).setExitTime(time);
        graph.getVertexList().get(vertex - 1).setColor(Color.BLACK);
        graph.getVertexList().get(vertex - 1).setStackNumber(graph.getVertexCount() - stack.size());
        if (flag == 0) {
            log.append("- Вершина " + vertex + " добавлена в стек\n");
        } else {
            log.append("- Вершина " + vertex + " принадлежит текущей КСС\n");
        }
        return time;
    }

    // поиск подходящего для обхода в глубину ребра
    private static int search_edge(Graph graph, int vertex, boolean[] visited, int last_stop, StringBuilder log, int flag){
        for (int i = last_stop; i < graph.getEdgeCount(); i++){
            if (graph.getEdgeList().get(i).getSource() == vertex && !visited[graph.getEdgeList().get(i).getTarget() - 1]){
                graph.getEdgeList().get(i).setEdgeType(EdgeType.TREE);
                log.append("- Переход по ребру (" + graph.getEdgeList().get(i).getSource() + ", "
                        + graph.getEdgeList().get(i).getTarget() + ")\n");
                return i;
            }
            Vertex source_vertex = graph.getVertexList().get(graph.getEdgeList().get(i).getSource() - 1);
            Vertex target_vertex = graph.getVertexList().get(graph.getEdgeList().get(i).getTarget() - 1);
            if (source_vertex.getId() == vertex && target_vertex.getColor() == Color.GRAY
                    && graph.getEdgeList().get(i).getEdgeType() == EdgeType.NONE){
                graph.getEdgeList().get(i).setEdgeType(EdgeType.BACK);
                if (flag == 0) {
                    log.append("- Ребро (" + source_vertex.getId() + ", " + target_vertex.getId() + ") обратное\n");
                }

            } else if (source_vertex.getId() == vertex && target_vertex.getColor() == Color.BLACK
                    && graph.getEdgeList().get(i).getEdgeType() == EdgeType.NONE){
                if (source_vertex.getEntryTime() < target_vertex.getEntryTime()){
                    graph.getEdgeList().get(i).setEdgeType(EdgeType.FORWARD);
                    if (flag == 0) {
                        log.append("- Ребро (" + source_vertex.getId() + ", " + target_vertex.getId() + ") направленное вперёд\n");
                    }
                } else {
                    graph.getEdgeList().get(i).setEdgeType(EdgeType.CROSS);
                    if (flag == 0) {
                        log.append("- Ребро (" + source_vertex.getId() + ", " + target_vertex.getId() + ") поперечное\n");
                    }
                }
            }
        }
        return -1;
    }

    // транспонирование графа
    private static Graph transpose_graph(Graph graph){
        ArrayList<Vertex> new_vertexes = new ArrayList<>();
        for (int i = 0; i < graph.getVertexCount(); i++){
            new_vertexes.add(new Vertex(
                    graph.getVertexList().get(i).getId(),
                    graph.getVertexList().get(i).getLabel(),
                    graph.getVertexList().get(i).getX(),
                    graph.getVertexList().get(i).getY()
            ));
        }

        ArrayList<Edge> new_edges = new ArrayList<>();
        for (int i = 0; i < graph.getEdgeCount(); i++){
            new_edges.add(new Edge(
                    graph.getEdgeList().get(i).getTarget(),
                    graph.getEdgeList().get(i).getSource(),
                    graph.getEdgeList().get(i).getColor()
            ));
        }
        return new Graph(
                new_edges,
                new_vertexes
        );
    }
}