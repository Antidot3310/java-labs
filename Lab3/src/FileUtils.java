import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class FileUtils {
    public static void saveToCSV(GornerTableModel data, File file, JFrame parent) {
        try (PrintWriter out = new PrintWriter(file, StandardCharsets.UTF_8)) {
            out.println("X,Значение(Horner),Значение(float),Разница");
            for (int i = 0; i < data.getRowCount(); i++) {
                Object x = data.getValueAt(i, 0);
                Object v1 = data.getValueAt(i, 1);
                Object v2 = data.getValueAt(i, 2);
                Object d  = data.getValueAt(i, 3);
                out.printf(Locale.ROOT, "%s,%s,%s,%s%n", x, v1, v2, d);
            }
            JOptionPane.showMessageDialog(parent, "CSV сохранён.", "Готово", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "Ошибка записи файла.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}