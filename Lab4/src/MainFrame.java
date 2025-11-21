package Lab4;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private GraphicsDisplay display = new GraphicsDisplay();
    private JFileChooser fileChooser = new JFileChooser(new File("."));

    private JCheckBoxMenuItem showAxis, showMarkers, showSecondGraph, rotate90;
    private JLabel statusLabel = new JLabel("Загрузите данные через меню Файл");

    public MainFrame() {
        super("Графики функций - Вариант C3");
        setupWindow();
        createMenu();
        setupLayout();
    }

    private void setupWindow() {
        setSize(800, 600);
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Меню Файл
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.add(createMenuItem("Открыть первый график", e -> openFile(0)));
        fileMenu.add(createMenuItem("Открыть второй график", e -> openFile(1)));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Очистить все", e -> display.clearAll()));

        // Меню График
        JMenu graphMenu = new JMenu("График");
        showAxis = createCheckItem("Показывать оси", display::setShowAxis, true);
        showMarkers = createCheckItem("Показывать маркеры", display::setShowMarkers, true);
        showSecondGraph = createCheckItem("Показывать второй график", display::setShowSecondGraph, true);
        rotate90 = createCheckItem("Поворот на 90°", display::setRotate90, false);

        graphMenu.add(showAxis);
        graphMenu.add(showMarkers);
        graphMenu.add(showSecondGraph);
        graphMenu.add(rotate90);
        graphMenu.addMenuListener(new GraphMenuListener());

        menuBar.add(fileMenu);
        menuBar.add(graphMenu);
        setJMenuBar(menuBar);
    }

    private JMenuItem createMenuItem(String text, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(action);
        return item;
    }

    private JCheckBoxMenuItem createCheckItem(String text, CheckBoxAction setter, boolean selected) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(text);
        item.setSelected(selected);
        item.addActionListener(e -> setter.apply(item.isSelected()));
        return item;
    }

    private void setupLayout() {
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(display, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void openFile(int graphIndex) {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadData(fileChooser.getSelectedFile(), graphIndex);
        }
    }

    private void loadData(File file, int graphIndex) {
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            List<Double[]> points = new ArrayList<>();
            while (in.available() > 0) {
                points.add(new Double[]{in.readDouble(), in.readDouble()});
            }
            display.setData(points.toArray(new Double[0][]), graphIndex);
            updateStatus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus() {
        String status = String.format("График 1: %s | График 2: %s",
                display.isLoaded(0) ? "загружен" : "не загружен",
                display.isLoaded(1) ? "загружен" : "не загружен");
        statusLabel.setText(status);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

    private class GraphMenuListener implements MenuListener {
        @Override
        public void menuSelected(MenuEvent e) {
            boolean hasFirst = display.isLoaded(0);
            showAxis.setEnabled(hasFirst);
            showMarkers.setEnabled(hasFirst);
            showSecondGraph.setEnabled(display.isLoaded(1));
            rotate90.setEnabled(hasFirst);
        }

        @Override
        public void menuDeselected(MenuEvent e) {}

        @Override
        public void menuCanceled(MenuEvent e) {}
    }

    @FunctionalInterface
    interface CheckBoxAction {
        void apply(boolean selected);
    }
}