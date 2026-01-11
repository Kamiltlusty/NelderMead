package pl.kamil;

import pl.kamil.eval_func.EvalFunc;
import pl.kamil.eval_func.Sphere;

import java.util.*;

public class Main {
    public static void run() {
        var scanner = new Scanner(System.in);
        var nm = new NelderMead();
        EvalFunc ef = new Sphere();
        double alfa = 1.0;
        double beta = 0.5;
        double gamma = 2;
        double delta = 0.5;
        int maxIter = 500;
        double eps = 1e-6;


        System.out.println("Podaj parametry wejsciowe w nastepujacy sposob: [x1,x2,...,xn]");
        var input = scanner.nextLine();
        String s = input.substring(1, input.length() - 1);
        List<Double> params = new ArrayList<>(Arrays
                .stream(s.split(","))
                .map(Double::valueOf)
                .toList());
        double step = 0.1;

        Map<Integer, List<Point>> integerListMap = nm.runExperiment(new Point(params), step, ef, alfa, beta, gamma, delta, maxIter, eps);
        System.out.println(integerListMap);

    }

    public static void main() {
        run();
    }
}

