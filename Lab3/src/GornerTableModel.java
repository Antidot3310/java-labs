import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class GornerTableModel extends AbstractTableModel {
    private final Double[] coefficients;
    private final Double from;
    private final Double to;
    private final Double step;
    private int rows;
    private final DecimalFormat df = new DecimalFormat("#.#####");

    public GornerTableModel(Double from, Double to, Double step, Double[] coefficients) {
        this.from = from; this.to = to; this.step = step; this.coefficients = coefficients;
        this.rows = (int)Math.floor((to - from) / step) + 1;
        if (rows < 0) rows = 0;

        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
    }

    @Override public int getColumnCount() { return 4; }
    @Override public int getRowCount() { return rows; }

    @Override public String getColumnName(int col) {
        switch (col) {
            case 0: return "X";
            case 1: return "Значение (Horner, Double)";
            case 2: return "Значение (float)";
            default: return "Разница";
        }
    }

    @Override public Class<?> getColumnClass(int col) {
        switch (col) {
            case 0: case 1: case 3: return Double.class;
            case 2: return Float.class;
            default: return Object.class;
        }
    }

    @Override public Object getValueAt(int row, int col) {
        double x = from + step * row;
        if (col == 0) return x;

        double r = 0.0;
        for (double c : coefficients) r = r * x + c;
        if (col == 1) return r;

        float xf = (float)x;
        float rf = 0f;
        for (Double c : coefficients) {
            rf = rf * xf + c.floatValue();
        }
        if (col == 2) return rf;

        return r - rf;
    }

    public Double getFrom() { return from; }
    public Double getTo() { return to; }
    public Double getStep() { return step; }
}