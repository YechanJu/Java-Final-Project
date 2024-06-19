package Todolist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TodoListApp {
    private JFrame frame;
    private JPanel mainPanel, addPanel;
    private CardLayout cardLayout;
    private JTextField titleField, deadlineField, contentField;
    private DefaultListModel<Task> listModel;
    private JList<Task> todoList;

    public TodoListApp() {
        // Create the frame
        frame = new JFrame("To-Do List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(390, 700); // Adjusted height

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create main view
        JPanel mainView = createMainView();

        // Create add task view
        JPanel addView = createAddView();

        // Add views to the card layout panel
        mainPanel.add(mainView, "MainView");
        mainPanel.add(addView, "AddView");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createMainView() {
        // Create input fields and buttons
        JButton addButton = new JButton("Add");
        JButton showCompletedButton = new JButton("Completed");
        JButton deleteButton = new JButton("Delete");

        // Set preferred size for buttons to ensure they fit on the screen
        Dimension buttonSize = new Dimension(120, 30);
        addButton.setPreferredSize(buttonSize);
        showCompletedButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);

        // Create the list model and JList
        listModel = new DefaultListModel<>();
        todoList = new JList<>(listModel);
        todoList.setCellRenderer(new TaskRenderer());
        todoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        todoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = todoList.locationToIndex(e.getPoint());
                if (index != -1) {
                    Task task = listModel.getElementAt(index);
                    Rectangle checkboxBounds = todoList.getCellBounds(index, index);
                    JCheckBox checkbox = (JCheckBox) todoList.getCellRenderer().getListCellRendererComponent(todoList, task, index, false, false);
                    if (SwingUtilities.isRightMouseButton(e)) {
                        showTaskDetails(task);
                    } else if (e.getX() - checkboxBounds.x <= checkbox.getPreferredSize().width) {
                        task.setCompleted(!task.isCompleted());
                        todoList.repaint();
                    } else if (e.getClickCount() == 2) { // Double click to edit
                        editTask(task, index);
                    }
                }
            }
        });

        // Add action listeners to buttons
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "AddView");
            }
        });

        showCompletedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCompletedTasks();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedTask();
            }
        });

        // Layout the components
        JPanel mainView = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.add(addButton);
        buttonPanel.add(showCompletedButton);
        buttonPanel.add(deleteButton);

        mainView.add(buttonPanel, BorderLayout.NORTH);
        mainView.add(new JScrollPane(todoList), BorderLayout.CENTER);

        return mainView;
    }

    private JPanel createAddView() {
        // Create input fields and buttons
        titleField = new JTextField(10);
        deadlineField = new JTextField(10);
        contentField = new JTextField(15);
        JButton confirmButton = new JButton("Confirm");

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem();
                cardLayout.show(mainPanel, "MainView");
            }
        });

        // Layout the components using GridBagLayout for better control
        JPanel addView = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        addView.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        addView.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addView.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        addView.add(deadlineField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addView.add(new JLabel("Content:"), gbc);

        gbc.gridx = 1;
        addView.add(contentField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        addView.add(confirmButton, gbc);

        return addView;
    }

    private void addItem() {
        String title = titleField.getText();
        String deadline = deadlineField.getText();
        String content = contentField.getText();
        if (!title.isEmpty() && !deadline.isEmpty() && !content.isEmpty()) {
            listModel.addElement(new Task(title, deadline, content));
            sortTasks();
            clearFields();
        }
    }

    private void editTask(Task task, int index) {
        JTextField editTitleField = new JTextField(task.getTitle(), 10);
        JTextField editDeadlineField = new JTextField(task.getDeadline(), 10);
        JTextField editContentField = new JTextField(task.getContent(), 15);
        JButton saveButton = new JButton("Save");

        JPanel editPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        editPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        editPanel.add(editTitleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        editPanel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        editPanel.add(editDeadlineField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        editPanel.add(new JLabel("Content:"), gbc);

        gbc.gridx = 1;
        editPanel.add(editContentField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        editPanel.add(saveButton, gbc);

        JDialog editDialog = new JDialog(frame, "Edit Task", true);
        editDialog.getContentPane().add(editPanel);
        editDialog.pack();

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newTitle = editTitleField.getText();
                String newDeadline = editDeadlineField.getText();
                String newContent = editContentField.getText();
                if (!newTitle.isEmpty() && !newDeadline.isEmpty() && !newContent.isEmpty()) {
                    task.setTitle(newTitle);
                    task.setDeadline(newDeadline);
                    task.setContent(newContent);
                    listModel.set(index, task); // Update the task in the list model
                    sortTasks();
                    todoList.repaint();
                    editDialog.dispose();
                }
            }
        });

        editDialog.setVisible(true);
    }

    private void deleteSelectedTask() {
        int selectedIndex = todoList.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.remove(selectedIndex);
        }
    }

    private void showTaskDetails(Task task) {
        String message = String.format("Title: %s\nDeadline: %s\nContent: %s",
                task.getTitle(), task.getDeadline(), task.getContent());
        JOptionPane.showMessageDialog(frame, message, "Task Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showCompletedTasks() {
        DefaultListModel<Task> completedModel = new DefaultListModel<>();
        for (int i = 0; i < listModel.size(); i++) {
            Task task = listModel.getElementAt(i);
            if (task.isCompleted()) {
                completedModel.addElement(task);
            }
        }
        JList<Task> completedList = new JList<>(completedModel);
        completedList.setCellRenderer(new TaskRenderer());
        JOptionPane.showMessageDialog(frame, new JScrollPane(completedList), "Completed To-Dos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void sortTasks() {
        ArrayList<Task> tasks = Collections.list(listModel.elements());
        tasks.sort(Comparator.comparing(Task::getDeadline).thenComparing(Task::isCompleted));
        listModel.clear();
        for (Task task : tasks) {
            listModel.addElement(task);
        }
    }

    private void clearFields() {
        titleField.setText("");
        deadlineField.setText("");
        contentField.setText("");
    }

    public static void main(String[] args) {
        new TodoListApp();
    }
}
