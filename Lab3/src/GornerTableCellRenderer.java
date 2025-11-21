import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class GornerTableCellRenderer implements TableCellRenderer {
    private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JLabel label = new JLabel();
    private final JCheckBox checkbox = new JCheckBox();
    private final DecimalFormat formatter;
    private String needle = null;
    private boolean highlightPalindromes = false;

    public GornerTableCellRenderer() {
        formatter = new DecimalFormat("#.#####");
        formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        panel.add(label);
        checkbox.setEnabled(false);
    }

    public void setNeedle(String needle) {
        this.needle = (needle != null && needle.trim().length() > 0) ? needle.trim() : null;
        if (this.needle != null) this.highlightPalindromes = false;
    }

    public void setHighlightPalindromes(boolean v) {
        this.highlightPalindromes = v;
    }

    private boolean isPalindromeString(String s) {
        if (s == null || s.isEmpty()) return false;

        // Убираем все нецифровые символы: минус, точку, запятую и лишние нули
        String cleaned = s.replace("-", "")
                .replace(".", "")
                .replace(",", "")
                .replaceFirst("^0+", ""); // убираем ведущие нули

        if (cleaned.isEmpty()) return false;

        // Проверяем на палиндром
        int i = 0, j = cleaned.length() - 1;
        while (i < j) {
            if (cleaned.charAt(i) != cleaned.charAt(j)) {
                return false;
            }
            i++;
            j--;
        }
        return true;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        // Форматируем число
        String text = "";
        if (value != null) {
            if (value instanceof Float) {
                text = formatter.format(((Float)value).doubleValue());
            } else if (value instanceof Number) {
                text = formatter.format(((Number)value).doubleValue());
            } else {
                text = value.toString();
            }
        }

        // Для поиска точного значения
        if (col == 1 && needle != null && needle.equals(text)) {
            checkbox.setSelected(true);
            checkbox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return checkbox;
        }

        label.setText(text);

        // Подсветка для палиндромов
        if (highlightPalindromes && isPalindromeString(text)) {
            panel.setBackground(Color.YELLOW);
            label.setToolTipText("Это палиндром: " + text);
        } else {
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            label.setToolTipText(null);
        }

        // Настройка цветов текста
        if (isSelected) {
            label.setForeground(table.getSelectionForeground());
        } else {
            label.setForeground(Color.BLACK);
        }

        return panel;
    }
}