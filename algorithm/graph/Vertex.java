package algorithm.graph;
import com.google.gson.annotations.Expose;

public class Vertex extends Element {
    @Expose
    private int id;
    @Expose
    private String label;
    @Expose
    private double x;
    @Expose
    private double y;
    @Expose
    private CustomColor color;
    private int entry_time;
    private int exit_time;
    private int stack_number;
    private int SCC_number;
    private int bypass_number;

    public Vertex(int id, String label, double x, double y){
        this.id = id;
        this.label = label;
        this.x = x;
        this.y = y;
        this.entry_time = 0;
        this.exit_time = 0;
        this.stack_number = 0;
        this.color = CustomColor.WHITE;
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
        this.color = CustomColor.WHITE;
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

    public CustomColor getColor(){
        return color;
    }

    public void setEntryTime(int entry_time) {
        this.entry_time = entry_time;
    }

    public void setExitTime(int exit_time) {
        this.exit_time = exit_time;
    }

    public void setColor(CustomColor color) {
        this.color = color;
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

    public int getBypassNumber(){
        return bypass_number;
    }

    public void setBypassNumber(int bypass_number) {
        this.bypass_number = bypass_number;
    }
}
