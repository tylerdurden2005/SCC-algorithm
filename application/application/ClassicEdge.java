package application;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

class ClassicEdge extends Group {
    private Line line;
    private Line leftWing;
    private Line rightWing;
    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public ClassicEdge(double startX, double startY, double endX, double endY){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(4);

        double angle = Math.atan2(endY - startY, endX - startX);
        double arrowLength = 10;
        double arrowAngle = Math.toRadians(30);

        double x1 = endX - arrowLength * Math.cos(angle - arrowAngle);
        double y1 = endY - arrowLength * Math.sin(angle - arrowAngle);

        double x2 = endX - arrowLength * Math.cos(angle + arrowAngle);
        double y2 = endY - arrowLength * Math.sin(angle + arrowAngle);

        leftWing = new Line(endX, endY, x1, y1);
        rightWing = new Line(endX, endY, x2, y2);

        leftWing.setStroke(Color.BLACK);
        rightWing.setStroke(Color.BLACK);

        leftWing.setStrokeWidth(4);
        rightWing.setStrokeWidth(4);
        getChildren().addAll(line, leftWing, rightWing);
    }
    public void setAnotherColorLines(Color color){
        line.setStroke(color);
        leftWing.setStroke(color);
        rightWing.setStroke(color);
    }
    public double getStartX(){
        return startX;
    }
    public double getStartY(){
        return startY;
    }

    public double getEndX(){
        return endX;
    }

    public double getEndY(){
        return endY;
    }
}