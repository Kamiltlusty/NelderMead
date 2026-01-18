package pl.kamil.eval_func;

import pl.kamil.Point;
import java.util.List;

public class Rosenbrock implements EvalFunc {

    @Override
    public double evaluate(Point point) {
        List<Double> x = point.getCoords();
        double sum = 0.0;

        for (int i = 0; i < x.size() - 1; i++) {
            double xi = x.get(i);
            double xnext = x.get(i + 1);

            double term1 = 100.0 * Math.pow(xnext - xi * xi, 2);
            double term2 = Math.pow(1.0 - xi, 2);

            sum += term1 + term2;
        }

        return sum;
    }
}
