package pl.kamil;

import java.util.List;

public class ExperimentResult {
    public String function;
    public List<FigureDTO> figures;

    public ExperimentResult(String function, List<FigureDTO> figures) {
        this.function = function;
        this.figures = figures;
    }
}