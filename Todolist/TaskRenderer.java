package Todolist;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

// Custom renderer to show tasks with different background colors based on their status
public class TaskRenderer extends JCheckBox implements ListCellRenderer<Task> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Task> list, Task value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());
        setSelected(value.isCompleted());

        if (value.isCompleted()) {
            setBackground(Color.LIGHT_GRAY);
        } else {
            LocalDate deadline = LocalDate.parse(value.getDeadline(), DateTimeFormatter.ISO_DATE);
            long daysUntilDeadline = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
            if (daysUntilDeadline < 0) {
                setBackground(Color.RED);
            } else if (daysUntilDeadline <= 3) {
                setBackground(Color.YELLOW);
            } else {
                setBackground(Color.WHITE);
            }
        }

        if (isSelected) {
            setBackground(getBackground().darker());
        }

        return this;
    }
}
