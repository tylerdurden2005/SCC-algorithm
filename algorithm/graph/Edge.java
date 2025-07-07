package algorithm.graph;
import com.google.gson.annotations.Expose;

public class Edge extends Element {
    @Expose
    private int source;
    @Expose
    private int target;
    @Expose
    private CustomColor color;
    @Expose
    private EdgeType edge_type;

    public Edge(int source, int target, CustomColor color){
        this.source = source;
        this.target = target;
        this.color = color;
        this.edge_type = EdgeType.NONE;
    }

    public Edge(int source, int target){
        this.source = source;
        this.target = target;
        this.color = CustomColor.BLACK;
        this.edge_type = EdgeType.NONE;
    }

    public int getSource(){
        return source;
    }

    public int getTarget(){
        return target;
    }

    public CustomColor getColor(){
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

    public void setSource(int source) {
        this.source = source;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}
