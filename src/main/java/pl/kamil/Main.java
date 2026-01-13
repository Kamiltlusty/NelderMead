package pl.kamil;

import pl.kamil.eval_func.EvalFunc;
import pl.kamil.eval_func.Sphere;
import tools.jackson.databind.ObjectMapper;

import java.io.*;
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

        List<FigureDTO> lf = new ArrayList<>();
        for (var figures : integerListMap.values()) {
            List<PointDTO> lp = new ArrayList<>();
            for (var vertices : figures) {
                PointDTO p = new PointDTO(vertices.getCoords());
                lp.add(p);
            }
            lf.add(new FigureDTO(lp));
        }

        ObjectMapper om = new ObjectMapper();
        om.writerWithDefaultPrettyPrinter()
                .writeValue(new File("figures.json"), lf);
    }

    public static void createVisualization() throws IOException, InterruptedException {
        String pythonCmd;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            pythonCmd = "py"; // Windows
        } else {
            pythonCmd = "python3"; // Linux / macOS
        }

        File pythonScript = extractPythonScript("hello.py");
        ProcessBuilder pb = new ProcessBuilder(
                pythonCmd,
                pythonScript.getAbsolutePath()
        );
        pb.redirectErrorStream(true);

        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[PYTHON] " + line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python script failed with code: " + exitCode);
        }

        System.out.println("Python script completed successfully.");
    }

    private static File extractPythonScript(String resourcePath) throws IOException {
        InputStream in = Main.class
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        if (in == null) {
            throw new RuntimeException("Resource not found: " + resourcePath);
        }

        File tempFile = File.createTempFile("hello", ".py");
        tempFile.deleteOnExit();

        try (in; FileOutputStream out = new FileOutputStream(tempFile)) {
            in.transferTo(out);
        }

        return tempFile;
    }

    public static void main() throws IOException, InterruptedException {
        run();
        createVisualization();
    }
}

