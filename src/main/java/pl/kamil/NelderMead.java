package pl.kamil;

import pl.kamil.eval_func.EvalFunc;

import java.util.*;

public class NelderMead {
    public Map<Integer, List<Point>> runExperiment(Point x0, double step, EvalFunc ef,
                                                   double alpha, double beta, double gamma, double delta, int maxIter, double eps) {
        x0.evaluate(ef);
        int counter = 0;

        // generate vertices
        List<Point> simplex = new ArrayList<>();
        simplex.add(x0);
        genVertices(x0, simplex, step, ef);
        Map<Integer, List<Point>> save = new TreeMap<>();
        save.put(counter, new ArrayList<>(simplex));
        while (counter < maxIter) {
            // algorithm
            // sort
            simplex.sort(Comparator.comparing(Point::getEval));
            // calculate centroid
            Point centroid = calculateCentroid(simplex.subList(0, simplex.size() - 1));
            // transform
            // take best, second worst and the worst
            Point best = simplex.get(0);
            Point secondWorst = simplex.get(simplex.size() - 2);
            Point worst = simplex.getLast();

            // and it's evaluations
            double bestEval = best.getEval();
            double secondWorstEval = secondWorst.getEval();
            double worstEval = worst.getEval();

            Point reflected = reflect(worst, centroid, alpha);
            reflected.evaluate(ef);
            double reflectedEval = reflected.getEval();

            if (bestEval <= reflectedEval && reflectedEval < secondWorstEval) {
                // accept and terminate
                simplex.set(simplex.size() - 1, reflected);
            } else if (reflectedEval < bestEval) {
                // compute expansion point
                Point expanded = expand(reflected, centroid, gamma);
                expanded.evaluate(ef);
                // accept and terminate
                if (expanded.getEval() < reflectedEval) {
                    simplex.set(simplex.size() - 1, expanded);
                } else {
                    simplex.set(simplex.size() - 1, reflected);
                }
            } else if (reflectedEval >= secondWorstEval) {
                if (secondWorstEval <= reflectedEval && reflectedEval < worstEval) {
                    Point contracted = outside(reflected, centroid, beta);
                    contracted.evaluate(ef);
                    if (contracted.getEval() <= reflectedEval) {
                        simplex.set(simplex.size() - 1, contracted);
                    } else {
                        simplex = shrink(best, simplex.subList(1, simplex.size()), delta);
                        for (var p : simplex) {
                            p.evaluate(ef);
                        }
                    }
                } else if (reflectedEval >= worstEval) {
                    Point contracted = inside(worst, centroid, beta);
                    contracted.evaluate(ef);
                    if (contracted.getEval() <= worstEval) {
                        simplex.set(simplex.size() - 1, contracted);
                    } else {
                        simplex = shrink(best, simplex.subList(1, simplex.size()), delta);
                        for (var p : simplex) {
                            p.evaluate(ef);
                        }
                    }
                }
            }
            // warunek terminacji
            counter++;
            save.put(counter, new ArrayList<>(simplex));
            double fMax = simplex.get(simplex.size() - 1).getEval();
            double fMin = simplex.get(0).getEval();
            if (Math.abs(fMax - fMin) < eps) {
                break;
            }
        }
        return save;
    }

    private List<Point> shrink(Point best, List<Point> subVertices, double delta) {
        int n = subVertices.size();
        int dimension = best.getCoords().size();

        List<Point> v = new ArrayList<>();
        v.add(best);
        for (int i = 0; i < n; i++) {
            List<Double> coords = new ArrayList<>();
            for (int j = 0; j < dimension; j++) {
                Double coordJBest = best.getCoords().get(j);
                Double coordJ = subVertices.get(i).getCoords().get(j);
                Double resCoord = coordJBest + delta * (coordJ - coordJBest);
                coords.add(resCoord);
            }
            v.add(new Point(coords));
        }
        return v;
    }

    private Point inside(Point worst, Point centroid, double beta) {
        int dimension = worst.getCoords().size();
        List<Double> coords = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            Double coordIWorst = worst.getCoords().get(i);
            Double coordIC = centroid.getCoords().get(i);
            Double resCoord = coordIC + beta * (coordIWorst - coordIC);
            coords.add(resCoord);
        }
        return new Point(coords);
    }

    private Point outside(Point reflected, Point centroid, double beta) {
        int dimension = reflected.getCoords().size();
        List<Double> coords = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            Double coordIReflected = reflected.getCoords().get(i);
            Double coordIC = centroid.getCoords().get(i);
            Double resCoord = coordIC + beta * (coordIReflected - coordIC);
            coords.add(resCoord);
        }
        return new Point(coords);
    }

    private Point expand(Point reflected, Point centroid, double gamma) {
        int dimension = reflected.getCoords().size();
        List<Double> coords = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            Double coordIReflected = reflected.getCoords().get(i);
            Double coordIC = centroid.getCoords().get(i);
            Double resCoord = coordIC + gamma * (coordIReflected - coordIC);
            coords.add(resCoord);
        }
        return new Point(coords);
    }

    private Point reflect(Point worst, Point centroid, double alpha) {
        int dimension = worst.getCoords().size();
        List<Double> coords = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            Double coordIWorst = worst.getCoords().get(i);
            Double coordIC = centroid.getCoords().get(i);
            Double resCoord = coordIC + alpha * (coordIC - coordIWorst);
            coords.add(resCoord);
        }
        return new Point(coords);
    }

    private Point calculateCentroid(List<Point> data) {
        int n = data.size();
        int dimension = data.get(0).getCoords().size();
        List<Double> coords = new ArrayList<>();

        for (int i = 0; i < dimension; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += data.get(j).getCoords().get(i);
            }
            coords.add(1.0 / data.size() * sum);
        }
        return new Point(coords);
    }

    private void genVertices(Point x0, List<Point> subVertices, double step, EvalFunc ef) {
        for (int i = 0; i < x0.coords.size(); i++) {
            subVertices.add(genVertex(x0, i, step, ef));
        }
    }

    private Point genVertex(Point x0, int position, double step, EvalFunc ef) {
        Point tmp = x0.clone();
        tmp.getCoords().set(position, x0.getCoords().get(position) + step);
        tmp.evaluate(ef);
        return tmp;
    }
}
