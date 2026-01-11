package pl.kamil.eval_func;
import pl.kamil.Point;

public class Sphere implements EvalFunc {
    public double evaluate(Point point) {
        int a = 0;
        double sum = 0.0;
        for (int i = 0; i < point.getCoords().size(); i++) {
            sum += Math.pow(point.getCoords().get(i) - a, 2);
        }
        return sum;
    }
}
