package graph;

public class Edge {
    private final int source;
    private final int target;
    private final Color color;

    public Edge(int source, int target, Color color){
        this.source = source;
        this.target = target;
        this.color = color;
    }

    public Edge(int source, int target){
        this.source = source;
        this.target = target;
        this.color = Color.BLACK;
    }

    public int getSource(){
        return source;
    }

    public int getTarget(){
        return target;
    }

    public Color getColor(){
        return color;
    }
}
