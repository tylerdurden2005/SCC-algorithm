import graph.*;

import java.util.ArrayList;

public class SCC {

    public static ArrayList<ArrayList<Integer>> find_SCC(Graph graph){
        // список посещенных при обходе в глубину вершин
        boolean[] visited = new boolean[graph.getVertexCount()];
        // расчет порядка выхода вершин
        ArrayList<Integer> exit_order = count_exit_order(graph, visited);

        // транспонирование исходного графа
        Graph transposed_graph = transpose_graph(graph);
        // список найденных компонент сильной связности
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        visited = new boolean[graph.getVertexCount()];

        // поиск КСС обходом в глубину транспонированного графа в обратном порядке выхода вершин
        for (int i = exit_order.size() - 1; i >= 0; i--){
            if (!visited[exit_order.get(i) - 1]){
                // текущая КСС
                ArrayList<Integer> component = new ArrayList<>();
                dfs(transposed_graph, exit_order.get(i), visited, component);

                result.add(component);
            }
        }
        return result;
    }

    private static ArrayList<Integer> count_exit_order(Graph graph, boolean[] visited){
        ArrayList<Integer> exit_order = new ArrayList<>();
        // вызов обхода в глубину для еще не посещенных вершин
        for (int i = 0; i < visited.length; i++){
            if (!visited[i]){
                dfs(graph, i + 1, visited, exit_order);
            }
        }
        return exit_order;
    }

    // обход в глубину
    private static void dfs(Graph graph, int vertex, boolean[] visited, ArrayList<Integer> stack){
        visited[vertex - 1] = true;
        // индекс следующего, исходящего из текущей вершины ребра в графе
        int edge_index = search_edge(graph, vertex, visited, 0);
        // продолжение обхода в глубину, пока не кончатся подходящие ребра
        while (edge_index != -1){
            dfs(graph, graph.getEdgeList().get(edge_index).getTarget(), visited, stack);
            edge_index = search_edge(graph, vertex, visited, edge_index);
        }
        // после завершения обхода для текущей вершины она добавляется в стек
        stack.add(vertex);
    }

    // поиск подходящего для обхода в глубину ребра
    private static int search_edge(Graph graph, int vertex, boolean[] visited, int last_stop){
        // ребра ищутся, начиная с последнего посещенного
        for (int i = last_stop; i < graph.getEdgeCount(); i++){
            // ребро подходит, если оно не ведет в уже посещенную вершину и исходит из запрашиваемой вершины
            if (graph.getEdgeList().get(i).getSource() == vertex && !visited[graph.getEdgeList().get(i).getTarget() - 1]){
                return i;
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