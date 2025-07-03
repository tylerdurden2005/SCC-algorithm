package algorithm.graph;
import com.google.gson.annotations.Expose;

public class Vertex {
    @Expose
    private int id;
    @Expose
    private String label;
    @Expose
    private double x;
    @Expose
    private double y;
    @Expose
    private Color color;
    private int entry_time;
    private int exit_time;
    private int stack_number;
    private int SCC_number;

    public Vertex() {}

    public Vertex(int id, String label, double x, double y){
        this.id = id;
        this.label = label;
        this.x = x;
        this.y = y;
        this.entry_time = 0;
        this.exit_time = 0;
        this.stack_number = 0;
        this.color = Color.WHITE;
        this.SCC_number = 0;
    }

    public Vertex(int id, String label){
        this.id = id;
        this.label = label;
        this.x = 0.0;
        this.y = 0.0;
        this.entry_time = 0;
        this.exit_time = 0;
        this.stack_number = 0;
        this.color = Color.WHITE;
        this.SCC_number = 0;
    }

    public Vertex(int id){
        this.id = id;
        this.label = "" + id;
        this.x = 0.0;
        this.y = 0.0;
        this.entry_time = 0;
        this.exit_time = 0;
        this.stack_number = 0;
        this.color = Color.WHITE;
        this.SCC_number = 0;
    }

    public int getId(){
        return id;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double[] getCoordinate(){
        return new double[]{x, y};
    }

    public void setId(int id){
        this.id = id;
    }

    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }

    public int getEntryTime(){
        return entry_time;
    }

    public int getExitTime(){
        return exit_time;
    }

    public Color getColor(){
        return color;
    }

    public void setEntryTime(int entry_time) {
        this.entry_time = entry_time;
    }

    public void setExitTime(int exit_time) {
        this.exit_time = exit_time;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getStackNumber(){
        return stack_number;
    }

    public void setStackNumber(int stack_number){
        this.stack_number = stack_number;
    }

    public int getSCCNumber() {
        return SCC_number;
    }

    public void setSCCNumber(int SCC_number){
        this.SCC_number = SCC_number;
    }

    public String getLabel(){
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
