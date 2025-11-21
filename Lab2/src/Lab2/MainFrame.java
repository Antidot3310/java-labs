package Lab2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;
    private static final int IMAGE_WIDTH = 300;
    private static final int IMAGE_HEIGHT = 150;

    private final JTextField[] inputFields = new JTextField[3];
    private final JTextField resultField = new JTextField("0", 15);
    private final JRadioButton[] formulaButtons = new JRadioButton[2];
    private final JRadioButton[] memoryButtons = new JRadioButton[3];
    private final JLabel[] memoryLabels = new JLabel[3];
    private final ImageIcon[] formulaImages = new ImageIcon[2];

    private final double[] memory = {0.0, 0.0, 0.0};
    private int activeMemory = 0;
    private int selectedFormula = 0;
    private JLabel formulaImageLabel;

    public MainFrame() {
        super("Вычисление формулы (Вариант 3)");
        initializeComponents();
        setupLayout();
        setupListeners();
        setSize(WIDTH, HEIGHT);
        centerWindow();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initializeComponents() {
        // Инициализация полей ввода
        for (int i = 0; i < inputFields.length; i++) {
            inputFields[i] = new JTextField("0", 10);
        }
        resultField.setEditable(false);

        // Инициализация кнопок формул
        formulaButtons[0] = new JRadioButton("Формула 1", true);
        formulaButtons[1] = new JRadioButton("Формула 2");

        // Инициализация кнопок памяти
        String[] memoryNames = {"Переменная 1", "Переменная 2", "Переменная 3"};
        for (int i = 0; i < memoryButtons.length; i++) {
            memoryButtons[i] = new JRadioButton(memoryNames[i], i == 0);
            memoryLabels[i] = new JLabel(String.format("mem%d: 0.0", i + 1));
        }

        // Загрузка изображений формул
        loadFormulaImages();
    }

    private void loadFormulaImages() {
        try {
            String[] imageFiles = {"1.png", "2.png"};
            for (int i = 0; i < formulaImages.length; i++) {
                formulaImages[i] = loadAndScaleImage(imageFiles[i]);
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки изображений: " + e.getMessage());
        }

        formulaImageLabel = createFormulaImageLabel();
        updateFormulaImage();
    }

    private ImageIcon loadAndScaleImage(String filename) {
        ImageIcon icon = new ImageIcon(filename);
        if (icon.getImage() != null) {
            Image scaled = icon.getImage().getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return null;
    }

    private JLabel createFormulaImageLabel() {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        return label;
    }

    private void setupLayout() {
        Box mainBox = Box.createVerticalBox();

        // Панель формул
        Box formulaBox = Box.createHorizontalBox();
        formulaBox.add(createRadioGroup("Выбор формулы:", formulaButtons, BoxLayout.Y_AXIS));
        formulaBox.add(Box.createHorizontalStrut(20));
        formulaBox.add(formulaImageLabel);
        formulaBox.add(Box.createHorizontalGlue());

        // Сборка интерфейса
        mainBox.add(Box.createVerticalStrut(10));
        mainBox.add(formulaBox);
        mainBox.add(Box.createVerticalStrut(20));
        mainBox.add(createInputPanel());
        mainBox.add(Box.createVerticalStrut(10));
        mainBox.add(createResultPanel());
        mainBox.add(Box.createVerticalStrut(10));
        mainBox.add(createControlPanel());
        mainBox.add(Box.createVerticalStrut(10));
        mainBox.add(createMemorySelectionPanel());
        mainBox.add(Box.createVerticalStrut(10));
        mainBox.add(createMemoryDisplayPanel());

        getContentPane().add(mainBox);
    }

    private Box createInputPanel() {
        Box box = Box.createHorizontalBox();
        String[] labels = {"X:", "Y:", "Z:"};
        for (int i = 0; i < labels.length; i++) {
            box.add(new JLabel(labels[i]));
            box.add(Box.createHorizontalStrut(5));
            box.add(inputFields[i]);
            if (i < labels.length - 1) box.add(Box.createHorizontalStrut(20));
        }
        return box;
    }

    private Box createResultPanel() {
        Box box = Box.createHorizontalBox();
        box.add(new JLabel("Результат:"));
        box.add(Box.createHorizontalStrut(10));
        box.add(resultField);
        return box;
    }

    private Box createControlPanel() {
        Box box = Box.createHorizontalBox();
        JButton[] buttons = {
                createButton("Вычислить", this::calculate),
                createButton("Очистить поля", this::clearFields),
                createButton("MC", this::clearMemory),
                createButton("M+", this::addToMemory)
        };

        for (int i = 0; i < buttons.length; i++) {
            if (i > 0) box.add(Box.createHorizontalStrut(10));
            box.add(buttons[i]);
        }
        return box;
    }

    private Box createMemorySelectionPanel() {
        Box box = Box.createHorizontalBox();
        box.add(new JLabel("Активная переменная:"));
        box.add(Box.createHorizontalStrut(10));
        box.add(createRadioGroup("", memoryButtons, BoxLayout.X_AXIS));
        return box;
    }

    private Box createMemoryDisplayPanel() {
        Box box = Box.createHorizontalBox();
        for (int i = 0; i < memoryLabels.length; i++) {
            if (i > 0) box.add(Box.createHorizontalStrut(20));
            box.add(memoryLabels[i]);
        }
        return box;
    }

    private Box createRadioGroup(String title, JRadioButton[] buttons, int axis) {
        Box box = axis == BoxLayout.Y_AXIS ? Box.createVerticalBox() : Box.createHorizontalBox();
        if (!title.isEmpty()) box.add(new JLabel(title));

        ButtonGroup group = new ButtonGroup();
        for (JRadioButton button : buttons) {
            group.add(button);
            box.add(button);
        }
        return box;
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    private void setupListeners() {
        // Обработчики формул
        for (int i = 0; i < formulaButtons.length; i++) {
            final int index = i;
            formulaButtons[i].addActionListener(e -> {
                selectedFormula = index;
                updateFormulaImage();
            });
        }

        // Обработчики памяти
        for (int i = 0; i < memoryButtons.length; i++) {
            final int index = i;
            memoryButtons[i].addActionListener(e -> activeMemory = index);
        }
    }

    private void updateFormulaImage() {
        if (formulaImages[selectedFormula] != null) {
            formulaImageLabel.setIcon(formulaImages[selectedFormula]);
            formulaImageLabel.setText("");
        } else {
            formulaImageLabel.setIcon(null);
            formulaImageLabel.setText("Формула " + (selectedFormula + 1));
        }
    }

    private void calculate(ActionEvent e) {
        try {
            double x = Double.parseDouble(inputFields[0].getText());
            double y = Double.parseDouble(inputFields[1].getText());
            double z = Double.parseDouble(inputFields[2].getText());

            double result = selectedFormula == 0 ? calculate1(x, y, z) : calculate2(x, y, z);
            resultField.setText(String.format("%.6f", result));

        } catch (NumberFormatException ex) {
            showError("Ошибка в формате числа");
        } catch (Exception ex) {
            showError("Ошибка вычисления: " + ex.getMessage());
        }
    }

    private double calculate1(double x, double y, double z) {
        double numerator = Math.sin(Math.PI * y * y) + Math.log(y * y);
        double denominator = Math.sin(Math.PI * z * z) + Math.sin(x) +
                Math.log(z * z) + x * x + Math.exp(Math.cos(x * z));
        return numerator / denominator;
    }

    private double calculate2(double x, double y, double z) {
        double numerator = Math.pow(Math.cos(Math.exp(y)) + Math.exp(y * y) +
                Math.pow(x, -0.5), 0.25);
        double denominator = Math.log(Math.pow(1 + z, 2)) + Math.cos(Math.PI * Math.pow(z, 3));
        return numerator / denominator;
    }

    private void clearFields(ActionEvent e) {
        for (JTextField field : inputFields) field.setText("0");
        resultField.setText("0");
    }

    private void clearMemory(ActionEvent e) {
        memory[activeMemory] = 0.0;
        updateMemoryDisplay();
    }

    private void addToMemory(ActionEvent e) {
        try {
            double result = Double.parseDouble(resultField.getText());
            memory[activeMemory] += result;
            updateMemoryDisplay();
        } catch (NumberFormatException ex) {
            showError("Нет результата для добавления в память");
        }
    }

    private void updateMemoryDisplay() {
        for (int i = 0; i < memoryLabels.length; i++) {
            memoryLabels[i].setText(String.format("mem%d: %.6f", i + 1, memory[i]));
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private void centerWindow() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - WIDTH) / 2, (screen.height - HEIGHT) / 2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}