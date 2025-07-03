package algorithm.graph;
import com.google.gson.annotations.Expose;

public class Edge {
    @Expose
    private int source;
    @Expose
    private int target;
    @Expose
    private Color color;
    @Expose
    private EdgeType edge_type;

    public Edge() {}

    public Edge(int source, int target, Color color){
        this.source = source;
        this.target = target;
        this.color = color;
        this.edge_type = EdgeType.NONE;
    }

    public Edge(int source, int target){
        this.source = source;
        this.target = target;
        this.color = Color.BLACK;
        this.edge_type = EdgeType.NONE;
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

    public EdgeType getEdgeType() {
        return edge_type;
    }

    public void setEdgeType(EdgeType edge_type){
        if (this.edge_type == EdgeType.NONE) {
            this.edge_type = edge_type;
        }
    }
}
