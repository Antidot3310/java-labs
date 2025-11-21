import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final int WIDTH = 800, HEIGHT = 500;
    private final Double[] coefficients;
    private GornerTableModel data;
    private final GornerTableCellRenderer renderer = new GornerTableCellRenderer();

    private final JTextField textFrom = new JTextField("0.0", 7);
    private final JTextField textTo   = new JTextField("1.0", 7);
    private final JTextField textStep = new JTextField("0.1", 7);

    private JFileChooser fileChooser = null;
    private Box hBoxResult;

    public MainFrame(Double[] coefficients) {
        super("Табулирование (Вариант C, подвариант 3)");
        this.coefficients = coefficients;
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        buildMenu();
        buildUI();
    }

    private void buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        JMenuItem saveCsv = new JMenuItem("Сохранить CSV");
        saveCsv.addActionListener(e -> {
            if (data == null) return;
            if (fileChooser == null) fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                FileUtils.saveToCSV(data, fileChooser.getSelectedFile(), this);
            }
        });
        fileMenu.add(saveCsv);

        JMenu tableMenu = new JMenu("Таблица");
        menuBar.add(tableMenu);

        JMenuItem searchValue = new JMenuItem("Найти значение");
        searchValue.addActionListener(e -> {
            String value = JOptionPane.showInputDialog(this, "Введите значение для поиска (в формате вывода):");
            renderer.setNeedle(value);
            renderer.setHighlightPalindromes(false);
            repaint();
        });
        tableMenu.add(searchValue);

        JMenuItem findPalindromes = new JMenuItem("Найти палиндромы");
        findPalindromes.addActionListener(e -> {
            renderer.setNeedle(null);
            renderer.setHighlightPalindromes(true);
            repaint();
        });
        tableMenu.add(findPalindromes);

        JMenu help = new JMenu("Справка");
        menuBar.add(help);
        JMenuItem about = new JMenuItem("О программе");
        about.addActionListener(e -> showAboutDialog());
        help.add(about);
    }

    private void buildUI() {
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        Box topBox = Box.createHorizontalBox();
        topBox.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        topBox.add(new JLabel("От:")); topBox.add(Box.createHorizontalStrut(5)); topBox.add(textFrom);
        topBox.add(Box.createHorizontalStrut(10));
        topBox.add(new JLabel("До:")); topBox.add(Box.createHorizontalStrut(5)); topBox.add(textTo);
        topBox.add(Box.createHorizontalStrut(10));
        topBox.add(new JLabel("Шаг:")); topBox.add(Box.createHorizontalStrut(5)); topBox.add(textStep);
        cp.add(topBox, BorderLayout.NORTH);

        hBoxResult = Box.createHorizontalBox();
        hBoxResult.add(new JPanel());
        cp.add(hBoxResult, BorderLayout.CENTER);

        Box hboxButtons = Box.createHorizontalBox();
        hboxButtons.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        JButton buttonCalc = new JButton("Вычислить");
        JButton buttonReset = new JButton("Очистить поля");
        buttonCalc.addActionListener(e -> onCalculate());
        buttonReset.addActionListener(e -> onReset());
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.add(buttonCalc);
        hboxButtons.add(Box.createHorizontalStrut(20));
        hboxButtons.add(buttonReset);
        hboxButtons.add(Box.createHorizontalGlue());
        cp.add(hboxButtons, BorderLayout.SOUTH);
    }

    private void onCalculate() {
        try {
            Double from = Double.parseDouble(textFrom.getText());
            Double to   = Double.parseDouble(textTo.getText());
            Double step = Double.parseDouble(textStep.getText());
            data = new GornerTableModel(from, to, step, coefficients);
            JTable table = new JTable(data);
            table.setRowHeight(28);
            table.setDefaultRenderer(Double.class, renderer);
            table.setDefaultRenderer(Float.class, renderer);
            table.setDefaultRenderer(Object.class, renderer);

            hBoxResult.removeAll();
            hBoxResult.add(new JScrollPane(table));
            getContentPane().validate();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка в формате числа", "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onReset() {
        textFrom.setText("0.0");
        textTo.setText("1.0");
        textStep.setText("0.1");
        hBoxResult.removeAll();
        hBoxResult.add(new JPanel());
        renderer.setNeedle(null);
        renderer.setHighlightPalindromes(false);
        getContentPane().validate();
    }

    private void showAboutDialog() {
        // Создаем панель с информацией и фото
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Текстовая информация
        JLabel infoLabel = new JLabel(
                "<html><div style='text-align: center;'>" +
                        "<h2>О программе</h2>" +
                        "<b>Автор:</b> Воронович Р.Ю.<br>" +
                        "<b>Группа:</b> 7<br>" +
                        "</div></html>",
                JLabel.CENTER
        );

        // Попытка загрузить фото
        JLabel photoLabel = null;
        try {
            // Пытаемся загрузить фото из файла (поместите photo.jpg в корень проекта)
            ImageIcon originalIcon = new ImageIcon("photo.jpg");
            Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            photoLabel = new JLabel(scaledIcon);
            photoLabel.setHorizontalAlignment(JLabel.CENTER);
        } catch (Exception e) {
            // Если фото не найдено, создаем заглушку
            photoLabel = new JLabel(
                    "<html><div style='text-align: center; color: gray;'>" +
                            "Фото<br>не найдено" +
                            "</div></html>",
                    JLabel.CENTER
            );
            photoLabel.setPreferredSize(new Dimension(150, 150));
            photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }

        // Размещаем компоненты
        panel.add(photoLabel, BorderLayout.WEST);
        panel.add(infoLabel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "О программе", JOptionPane.INFORMATION_MESSAGE);
    }
}