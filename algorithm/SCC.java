package algorithm;
import algorithm.graph.*;

import java.util.ArrayList;

public class SCC {

    public static ArrayList<ArrayList<Integer>> find_SCC(Graph graph, StringBuilder log){
        log.append("1 ЭТАП. Расчет порядка выхода вершин.\n");
        boolean[] visited = new boolean[graph.getVertexCount()];
        ArrayList<Integer> exit_order = count_exit_order(graph, visited, log);

        ArrayList<String> exit_order_for_log = new ArrayList<>();
        for (int i = 0; i < exit_order.size(); i++){
            exit_order_for_log.add(graph.getVertexList().get(exit_order.get(i) - 1).getLabel());
        }

        log.append("- Стек: " + exit_order_for_log + "\n");
        log.append("\n2 ЭТАП. Транспонирование графа.\n");
        graph.getSteps().transposeGraph(graph);
        graph.getSteps().addLog(log.toString());
        log.append("- Исходный граф транспонируется\n");
        log.append("\n3 ЭТАП. Формирование КСС обходом в глубину транспонированного графа.\n");

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

        for (int i = 0; i < transposed_graph.getSteps().getOperationSequence().size(); i++){
            graph.getSteps().getOperationSequence().add(transposed_graph.getSteps().getOperationSequence().get(i));
            graph.getSteps().addLog(transposed_graph.getSteps().getLogs().get(i));
        }

        for (int i = 0; i < result.size(); i++){
            for (Integer v : result.get(i)){
                graph.getVertexList().get(v - 1).setSCCNumber(i + 1);
                transposed_graph.getVertexList().get(v - 1).setSCCNumber(i + 1);
            }
        }

        ArrayList<ArrayList<Integer>> result_for_log = new ArrayList<>();
        for (int i = 0; i < result.size(); i++){
            ArrayList<Integer> comp = new ArrayList<>();
            for (Integer elem : result.get(i)){
                comp.add(Integer.parseInt(graph.getVertexList().get(elem - 1).getLabel()));
            }
            result_for_log.add(comp);
        }
        log.append("\nПолученные КСС: " + result_for_log + "\n");
        graph.getSteps().getLogs().remove(graph.getSteps().getLogs().size() - 1);
        graph.getSteps().getLogs().add(log.toString());
        graph.getSteps().modifyLogs();

        ArrayList<Vertex> vertexes_for_bypass = (ArrayList<Vertex>) graph.getVertexList().clone();
        vertexes_for_bypass.sort((v1, v2) -> {
            int cmp = Integer.compare(v1.getEntryTime(), v2.getEntryTime());
            return cmp;
        });
        for (int i = 0; i < vertexes_for_bypass.size(); i++){
            graph.getVertexList().get(vertexes_for_bypass.get(i).getId() - 1).setBypassNumber(i + 1);
        }
        return result_for_log;
    }

    private static ArrayList<Integer> count_exit_order(Graph graph, boolean[] visited, StringBuilder log){
        ArrayList<Integer> exit_order = new ArrayList<>();
        int time = 0;
        for (int i = 0; i < visited.length; i++){
            if (!visited[i]){
                log.append("- Прыжок в вершину " + graph.getVertexList().get(i).getLabel() + "\n");
                time++;
                time = dfs(graph, i + 1, visited, exit_order, time, log, 0);
            }
        }
        return exit_order;
    }

    private static int dfs(Graph graph, int vertex, boolean[] visited, ArrayList<Integer> stack, int time, StringBuilder log, int flag){
        visited[vertex - 1] = true;
        Vertex current_vertex = graph.getVertexList().get(vertex - 1);
        current_vertex.setEntryTime(time);
        current_vertex.setColor(CustomColor.LIGHTGRAY);

        log.append("- Посещена вершина " + current_vertex.getLabel() + "\n");
        graph.getSteps().enterVertex(current_vertex);
        graph.getSteps().addLog(log.toString());
        if (flag == 0){
            graph.getSteps().colorVertex(current_vertex, CustomColor.LIGHTGRAY);
            graph.getSteps().addLog(log.toString());
        } else {
            graph.getSteps().numberVertex(current_vertex);
            graph.getSteps().addLog(log.toString());
        }

        int edge_index = search_edge(graph, vertex, visited, 0, log, flag);
        while (edge_index != -1){
            time++;
            graph.getSteps().leaveVertex(current_vertex);
            graph.getSteps().addLog(log.toString());
            time = dfs(graph, graph.getEdgeList().get(edge_index).getTarget(), visited, stack, time, log, flag);
            graph.getSteps().enterVertex(current_vertex);
            graph.getSteps().addLog(log.toString());
            edge_index = search_edge(graph, vertex, visited, edge_index, log, flag);
        }
        time++;
        stack.add(vertex);
        current_vertex.setExitTime(time);
        current_vertex.setColor(CustomColor.GRAY);
        current_vertex.setStackNumber(graph.getVertexCount() - stack.size());

        graph.getSteps().leaveVertex(current_vertex);
        if (flag == 0) {
            log.append("- Вершина " + current_vertex.getLabel() + " добавлена в стек\n");
            graph.getSteps().addLog(log.toString());
            graph.getSteps().colorVertex(current_vertex, CustomColor.GRAY);
            graph.getSteps().addLog(log.toString());
        } else {
            log.append("- Вершина " + current_vertex.getLabel() + " принадлежит текущей КСС\n");
            graph.getSteps().addLog(log.toString());
        }
        return time;
    }

    private static int search_edge(Graph graph, int vertex, boolean[] visited, int last_stop, StringBuilder log, int flag){
        for (int i = last_stop; i < graph.getEdgeCount(); i++){
            Edge current_edge = graph.getEdgeList().get(i);
            if (current_edge.getSource() == vertex && !visited[current_edge.getTarget() - 1]){
                current_edge.setEdgeType(EdgeType.TREE);
                log.append("- Переход по ребру (" + graph.getVertexList().get(current_edge.getSource() - 1).getLabel() + ", "
                        + graph.getVertexList().get(current_edge.getTarget() - 1).getLabel() + ")\n");
                graph.getSteps().goEdge(current_edge);
                graph.getSteps().addLog(log.toString());
                return i;
            }
            Vertex source_vertex = graph.getVertexList().get(current_edge.getSource() - 1);
            Vertex target_vertex = graph.getVertexList().get(current_edge.getTarget() - 1);
            if (source_vertex.getId() == vertex && target_vertex.getColor() == CustomColor.LIGHTGRAY
                    && current_edge.getEdgeType() == EdgeType.NONE){
                current_edge.setEdgeType(EdgeType.BACK);
                if (flag == 0) {
                    log.append("- Ребро (" + source_vertex.getLabel() + ", " + target_vertex.getLabel() + ") обратное\n");
                    graph.getSteps().colorEdge(current_edge, CustomColor.CYAN);
                    graph.getSteps().addLog(log.toString());
                }

            } else if (source_vertex.getId() == vertex && target_vertex.getColor() == CustomColor.GRAY
                    && current_edge.getEdgeType() == EdgeType.NONE){
                if (source_vertex.getEntryTime() < target_vertex.getEntryTime()){
                    current_edge.setEdgeType(EdgeType.FORWARD);
                    if (flag == 0) {
                        log.append("- Ребро (" + source_vertex.getLabel() + ", " + target_vertex.getLabel() + ") направленное вперёд\n");
                        graph.getSteps().colorEdge(current_edge, CustomColor.BLUE);
                        graph.getSteps().addLog(log.toString());
                    }
                } else {
                    current_edge.setEdgeType(EdgeType.CROSS);
                    if (flag == 0) {
                        log.append("- Ребро (" + source_vertex.getLabel() + ", " + target_vertex.getLabel() + ") поперечное\n");
                        graph.getSteps().colorEdge(current_edge, CustomColor.PURPLE);
                        graph.getSteps().addLog(log.toString());
                    }
                }
            }
        }
        return -1;
    }

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