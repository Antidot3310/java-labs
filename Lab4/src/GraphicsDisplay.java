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

    private void drawGraphs(Graphics2D canvas) {
        for (int i = 0; i < graphs.size(); i++) {
            if (i == 1 && !showSecondGraph) continue;

            Double[][] data = graphs.get(i);
            if (data == null || data.length == 0) continue;

            canvas.setStroke(strokes[i]);
            canvas.setColor(colors[i]);

            GeneralPath path = new GeneralPath();
            for (int j = 0; j < data.length; j++) {
                Point2D p = toScreen(data[j][0], data[j][1]);
                if (j == 0) path.moveTo(p.getX(), p.getY());
                else path.lineTo(p.getX(), p.getY());
            }
            canvas.draw(path);
        }
    }

    private void drawMarkers(Graphics2D canvas) {
        for (int i = 0; i < graphs.size(); i++) {
            if (i == 1 && !showSecondGraph) continue;

            Double[][] data = graphs.get(i);
            if (data == null) continue;

            for (Double[] point : data) {
                Point2D center = toScreen(point[0], point[1]);
                drawSight(canvas, center, shouldHighlight(point[1]) ? Color.GREEN : colors[i]);
            }
        }
    }

    private void drawSight(Graphics2D canvas, Point2D center, Color color) {
        double radius = 5.5;
        canvas.setColor(color);

        // Внешний и внутренний круги
        canvas.draw(new Ellipse2D.Double(center.getX()-radius, center.getY()-radius, 2*radius, 2*radius));
        canvas.draw(new Ellipse2D.Double(center.getX()-radius*0.6, center.getY()-radius*0.6, 2*radius*0.6, 2*radius*0.6));

        // Перекрестие
        double crossLen = radius * 1.2;
        canvas.draw(new Line2D.Double(center.getX()-crossLen, center.getY(), center.getX()+crossLen, center.getY()));
        canvas.draw(new Line2D.Double(center.getX(), center.getY()-crossLen, center.getX(), center.getY()+crossLen));

        // Черточки на концах
        double tickLen = radius * 0.4;
        canvas.draw(new Line2D.Double(center.getX()-tickLen/2, center.getY()-crossLen,
                center.getX()+tickLen/2, center.getY()-crossLen));
        canvas.draw(new Line2D.Double(center.getX()-tickLen/2, center.getY()+crossLen,
                center.getX()+tickLen/2, center.getY()+crossLen));
        canvas.draw(new Line2D.Double(center.getX()-crossLen, center.getY()-tickLen/2,
                center.getX()-crossLen, center.getY()+tickLen/2));
        canvas.draw(new Line2D.Double(center.getX()+crossLen, center.getY()-tickLen/2,
                center.getX()+crossLen, center.getY()+tickLen/2));
    }

    private void drawAxes(Graphics2D canvas) {
        canvas.setColor(Color.BLACK);
        canvas.setStroke(new BasicStroke(2));
        canvas.setFont(new Font("Serif", Font.BOLD, 36));

        // Ось Y
        if (minX <= 0 && maxX >= 0) {
            canvas.draw(new Line2D.Double(toScreen(0, maxY), toScreen(0, minY)));
            drawArrow(canvas, toScreen(0, maxY), 5, 20);
            canvas.drawString("y", (float)toScreen(0, maxY).getX() + 10,
                    (float)toScreen(0, maxY).getY() - 10);
        }

        // Ось X
        if (minY <= 0 && maxY >= 0) {
            canvas.draw(new Line2D.Double(toScreen(minX, 0), toScreen(maxX, 0)));
            drawArrow(canvas, toScreen(maxX, 0), -20, -5);
            canvas.drawString("x", (float)toScreen(maxX, 0).getX() - 30,
                    (float)toScreen(maxX, 0).getY() + 20);
        }
    }

    private void drawArrow(Graphics2D canvas, Point2D tip, double dx, double dy) {
        GeneralPath arrow = new GeneralPath();
        arrow.moveTo(tip.getX(), tip.getY());
        arrow.lineTo(tip.getX() + dx, tip.getY() + dy);
        arrow.lineTo(tip.getX() - dx/2, tip.getY() + dy/2);
        arrow.closePath();
        canvas.fill(arrow);
    }

    private Point2D.Double toScreen(double x, double y) {
        return new Point2D.Double((x - minX) * scale, (maxY - y) * scale);
    }

    private boolean shouldHighlight(Double y) {
        int value = Math.abs(y.intValue());
        int sum = 0;
        while (value > 0) {
            sum += value % 10;
            value /= 10;
        }
        return sum < 10;
    }
}