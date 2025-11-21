package Lab4;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

public class GraphicsDisplay extends JPanel {
    private final List<Double[][]> graphs = Arrays.asList(null, null);
    private final Color[] colors = {Color.RED, Color.BLUE};
    private final Stroke[] strokes = {
            new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10,
                    new float[]{12, 4, 4, 4, 4, 4}, 0),
            new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10,
                    new float[]{12, 4, 4, 4, 4, 4}, 0)
    };

    private boolean showAxis = true, showMarkers = true, showSecondGraph = true, rotate90 = false;
    private double minX, maxX, minY, maxY, scale;

    public GraphicsDisplay() {
        setBackground(Color.WHITE);
    }

    public void setData(Double[][] data, int graphIndex) {
        if (graphIndex >= 0 && graphIndex < 2) {
            graphs.set(graphIndex, data);
            repaint();
        }
    }

    public void clearAll() {
        graphs.set(0, null);
        graphs.set(1, null);
        repaint();
    }

    public boolean isLoaded(int graphIndex) {
        return graphIndex >= 0 && graphIndex < 2 && graphs.get(graphIndex) != null;
    }

    public void setShowAxis(boolean show) {
        this.showAxis = show;
        repaint();
    }

    public void setShowMarkers(boolean show) {
        this.showMarkers = show;
        repaint();
    }

    public void setShowSecondGraph(boolean show) {
        this.showSecondGraph = show;
        repaint();
    }

    public void setRotate90(boolean rotate) {
        this.rotate90 = rotate;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!hasData()) {
            drawPlaceholder((Graphics2D) g);
            return;
        }

        calculateBoundsAndScale();
        Graphics2D canvas = (Graphics2D) g;

        if (rotate90) {
            canvas.rotate(-Math.PI / 2);
            canvas.translate(-getHeight(), 0);
        }

        if (showAxis) drawAxes(canvas);
        drawGraphs(canvas);
        if (showMarkers) drawMarkers(canvas);
    }

    private boolean hasData() {
        return graphs.stream().anyMatch(g -> g != null && g.length > 0);
    }

    private void drawPlaceholder(Graphics2D canvas) {
        canvas.setColor(Color.GRAY);
        canvas.setFont(new Font("Serif", Font.ITALIC, 24));
        String text = "Загрузите данные через меню Файл";
        int x = (getWidth() - canvas.getFontMetrics().stringWidth(text)) / 2;
        canvas.drawString(text, x, getHeight() / 2);
    }

    private void calculateBoundsAndScale() {
        minX = Double.MAX_VALUE;
        maxX = -Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        for (Double[][] data : graphs) {
            if (data != null) {
                for (Double[] point : data) {
                    minX = Math.min(minX, point[0]);
                    maxX = Math.max(maxX, point[0]);
                    minY = Math.min(minY, point[1]);
                    maxY = Math.max(maxY, point[1]);
                }
            }
        }

        scale = Math.min(getWidth() / (maxX - minX), getHeight() / (maxY - minY));
        adjustBounds();
    }

    private void adjustBounds() {
        if (scale == getWidth() / (maxX - minX)) {
            double adjust = (getHeight() / scale - (maxY - minY)) / 2;
            maxY += adjust;
            minY -= adjust;
        } else {
            double adjust = (getWidth() / scale - (maxX - minX)) / 2;
            maxX += adjust;
            minX -= adjust;
        }
    }

    
}