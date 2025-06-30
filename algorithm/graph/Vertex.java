package graph;

public class Vertex {
    private int id;
    private int x;
    private int y;

    public Vertex(int id, int x, int y){
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public Vertex(int id){
        this.id = id;
        this.x = 0;
        this.y = 0;
    }

    public int getId(){
        return id;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

}
