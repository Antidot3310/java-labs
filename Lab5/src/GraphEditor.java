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

    private void drawAxes(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);

        Point2D origin = dataToScreen(new Point2D.Double(0, 0));
        g2d.drawLine(0, (int) origin.getY(), getWidth(), (int) origin.getY());
        g2d.drawLine((int) origin.getX(), 0, (int) origin.getX(), getHeight());
    }

    private void drawGraphLines(Graphics2D g2d) {
        g2d.setColor(LINE_COLOR);
        g2d.setStroke(new BasicStroke(2));

        for (int i = 0; i < points.size() - 1; i++) {
            Point2D p1 = dataToScreen(points.get(i));
            Point2D p2 = dataToScreen(points.get(i + 1));
            g2d.drawLine((int) p1.getX(), (int) p1.getY(),
                    (int) p2.getX(), (int) p2.getY());
        }
    }

    private void drawPoints(Graphics2D g2d) {
        for (Point2D.Double point : points) {
            Point2D screenPoint = dataToScreen(point);
            int x = (int) screenPoint.getX() - POINT_SIZE / 2;
            int y = (int) screenPoint.getY() - POINT_SIZE / 2;

            if (point == hoverPoint) {
                g2d.setColor(HOVER_COLOR);
            } else {
                g2d.setColor(POINT_COLOR);
            }

            g2d.fillOval(x, y, POINT_SIZE, POINT_SIZE);
        }
    }

    private void drawSelectionRect(Graphics2D g2d) {
        if (selectionRect != null) {
            g2d.setColor(SELECTION_COLOR);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                    0, new float[]{5}, 0));

            Point2D start = dataToScreen(new Point2D.Double(selectionRect.x, selectionRect.y));
            Point2D end = dataToScreen(new Point2D.Double(
                    selectionRect.x + selectionRect.width,
                    selectionRect.y + selectionRect.height
            ));

            int x = (int) Math.min(start.getX(), end.getX());
            int y = (int) Math.min(start.getY(), end.getY());
            int width = (int) Math.abs(end.getX() - start.getX());
            int height = (int) Math.abs(end.getY() - start.getY());

            g2d.drawRect(x, y, width, height);
        }
    }

    private void drawHoverInfo(Graphics2D g2d) {
        if (hoverPoint != null) {
            Point2D screenPoint = dataToScreen(hoverPoint);
            String info = String.format("(%.2f, %.2f)", hoverPoint.x, hoverPoint.y);

            g2d.setColor(Color.BLACK);
            g2d.drawString(info, (int) screenPoint.getX() + 10, (int) screenPoint.getY() - 10);
        }
    }

    private Point2D dataToScreen(Point2D.Double dataPoint) {
        double x = (dataPoint.x - currentView.x) / currentView.width * getWidth();
        double y = getHeight() - (dataPoint.y - currentView.y) / currentView.height * getHeight();
        return new Point2D.Double(x, y);
    }

    private Point2D.Double screenToData(Point screenPoint) {
        double x = currentView.x + (screenPoint.x * currentView.width / getWidth());
        double y = currentView.y + ((getHeight() - screenPoint.y) * currentView.height / getHeight());
        return new Point2D.Double(x, y);
    }

    private Point2D.Double findNearestPoint(Point2D.Double dataPoint, double radius) {
        Point2D screenPoint = dataToScreen(dataPoint);

        for (Point2D.Double point : points) {
            Point2D pointScreen = dataToScreen(point);
            double distance = screenPoint.distance(pointScreen);
            if (distance <= radius) {
                return point;
            }
        }
        return null;
    }

    // MouseListener methods
    @Override
    public void mousePressed(MouseEvent e) {
        Point2D.Double dataPoint = screenToData(e.getPoint());

        if (SwingUtilities.isLeftMouseButton(e)) {
            draggedPoint = findNearestPoint(dataPoint, HOVER_RADIUS);
            if (draggedPoint != null) {
                isDraggingPoint = true;
            } else {
                // Начало выделения области
                dragStart = e.getPoint();
                selectionRect = new Rectangle2D.Double(
                        dataPoint.x, dataPoint.y, 0, 0
                );
            }
        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (selectionRect != null && Math.abs(selectionRect.width) > 5 &&
                    Math.abs(selectionRect.height) > 5) {

                zoomHistory.push(new Rectangle2D.Double(
                        currentView.x, currentView.y, currentView.width, currentView.height
                ));

                // Нормализуем прямоугольник выделения
                double x1 = selectionRect.x;
                double y1 = selectionRect.y;
                double x2 = selectionRect.x + selectionRect.width;
                double y2 = selectionRect.y + selectionRect.height;

                currentView = new Rectangle2D.Double(
                        Math.min(x1, x2), Math.min(y1, y2),
                        Math.abs(x2 - x1), Math.abs(y2 - y1)
                );
            }

            selectionRect = null;
            isDraggingPoint = false;
            draggedPoint = null;
        }
        else if (SwingUtilities.isRightMouseButton(e)) {
            // Восстановление масштаба на шаг назад
            if (!zoomHistory.isEmpty()) {
                currentView = zoomHistory.pop();
            }
        }

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point2D.Double dataPoint = screenToData(e.getPoint());
        hoverPoint = findNearestPoint(dataPoint, HOVER_RADIUS);
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point2D.Double dataPoint = screenToData(e.getPoint());

        if (isDraggingPoint && draggedPoint != null) {
            // Изменение Y координаты точки
            draggedPoint.y = dataPoint.y;
        }
        else if (dragStart != null) {
            // Обновление прямоугольника выделения
            Point2D.Double startData = screenToData(dragStart);
            selectionRect = new Rectangle2D.Double(
                    startData.x, startData.y,
                    dataPoint.x - startData.x,
                    dataPoint.y - startData.y
            );
        }

        repaint();
    }

    // Остальные методы MouseListener и MouseMotionListener
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public void saveToFile(File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Point2D.Double point : points) {
                writer.printf("%.6f,%.6f%n", point.x, point.y);
            }
        }
    }

    public void resetToOriginal() {
        points.clear();
        points.addAll(originalPoints);
        currentView = calculateDataBounds();
        zoomHistory.clear();
        repaint();
    }

    // создания тестовых данных
    public static List<Point2D.Double> createSampleData() {
        List<Point2D.Double> data = new ArrayList<>();
        for (double x = -10; x <= 10; x += 0.5) {
            double y = x * x; // y = x²
            data.add(new Point2D.Double(x, y));
        }
        return data;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Graph Editor - Вариант C");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            GraphEditor graphEditor = new GraphEditor(createSampleData());

            // Панель управления
            JPanel controlPanel = new JPanel();
            JButton saveButton = new JButton("Сохранить в файл");
            JButton resetButton = new JButton("Сбросить");

            saveButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    try {
                        graphEditor.saveToFile(fileChooser.getSelectedFile());
                        JOptionPane.showMessageDialog(frame, "Данные успешно сохранены!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Ошибка сохранения: " + ex.getMessage());
                    }
                }
            });

            resetButton.addActionListener(e -> graphEditor.resetToOriginal());

            controlPanel.add(saveButton);
            controlPanel.add(resetButton);

            frame.add(graphEditor, BorderLayout.CENTER);
            frame.add(controlPanel, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}