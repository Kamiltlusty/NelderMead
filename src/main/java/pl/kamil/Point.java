package pl.kamil;

import pl.kamil.eval_func.EvalFunc;

import java.util.ArrayList;
import java.util.List;

public class Point {
    List<Double> coords;
    double eval;

    public Point(List<Double> coords) {
        this.coords = coords;
    }

    public void evaluate(EvalFunc ef) {
        eval = ef.evaluate(this);
    }

    public Point clone(){
        return new Point(new ArrayList<>(coords));
    }

    public double getEval() {
        return eval;
    }

    public List<Double> getCoords() {
        return coords;
    }

    public void setCoords(List<Double> coords) {
        this.coords = coords;
    }

    public void setEval(double eval) {
        this.eval = eval;
    }

    @Override
    public String toString() {
        return "Point{" +
                "eval=" + eval +
                ", coords=" + coords +
                '}';
    }
}
