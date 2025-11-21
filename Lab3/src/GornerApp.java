import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Locale;
import java.text.DecimalFormatSymbols;

public class GornerApp {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Укажите коэффициенты многочлена (старшая степень первой).");
            System.exit(-1);
        }
        Double[] coeffs = new Double[args.length];
        try {
            for (int i = 0; i < args.length; i++) coeffs[i] = Double.parseDouble(args[i]);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка преобразования аргумента в Double.");
            System.exit(-2);
        }
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(coeffs);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
