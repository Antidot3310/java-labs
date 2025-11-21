import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class GraphEditor extends JPanel implements MouseListener, MouseMotionListener {
    private List<Point2D.Double> points;
    private List<Point2D.Double> originalPoints;
    private Stack<Rectangle2D.Double> zoomHistory;
    private Rectangle2D.Double currentView;
    private Rectangle2D.Double selectionRect;
    private Point2D.Double hoverPoint;
    private Point2D.Double draggedPoint;
    private Point dragStart;
    private boolean isDraggingPoint = false;

    // Цвета и настройки
    private static final Color POINT_COLOR = Color.BLUE;
    private static final Color LINE_COLOR = Color.RED;
    private static final Color HOVER_COLOR = Color.GREEN;
    private static final Color SELECTION_COLOR = Color.BLACK;
    private static final int POINT_SIZE = 6;
    private static final int HOVER_RADIUS = 10;

    public GraphEditor(List<Point2D.Double> initialPoints) {
        this.points = new ArrayList<>(initialPoints);
        this.originalPoints = new ArrayList<>(initialPoints);
        this.zoomHistory = new Stack<>();
        this.currentView = calculateDataBounds();

        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private Rectangle2D.Double calculateDataBounds() {
        if (points.isEmpty()) return new Rectangle2D.Double(0, 0, 1, 1);

        double minX = points.get(0).x;
        double maxX = points.get(0).x;
        double minY = points.get(0).y;
        double maxY = points.get(0).y;

        for (Point2D.Double p : points) {
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }

        // Добавляем отступы
        double paddingX = (maxX - minX) * 0.1;
        double paddingY = (maxY - minY) * 0.1;

        return new Rectangle2D.Double(
                minX - paddingX, minY - paddingY,
                (maxX - minX) + 2 * paddingX,
                (maxY - minY) + 2 * paddingY
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Рисуем оси
        drawAxes(g2d);
        // Рисуем линии графика
        drawGraphLines(g2d);
        // Рисуем точки
        drawPoints(g2d);
        // Рисуем выделенную область
        drawSelectionRect(g2d);
        // Рисуем информацию о точке при наведении
        drawHoverInfo(g2d);
    }

   
    }
}