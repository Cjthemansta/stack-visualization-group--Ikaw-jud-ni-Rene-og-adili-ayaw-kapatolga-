import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Swing GUI for viewing and operating on StackADT.
 * All stack changes are made through StackADT's public methods.
 */
public class StackVisualizerGUI extends JFrame {
    private final StackADT stack = new StackADT();
    private final JTextField valueField = new JTextField(10);
    private final JPanel stackPanel = new JPanel();
    private final JLabel countLabel = new JLabel("Size: 0 / " + StackADT.MAX_CAPACITY);
    private final JLabel statusLabel = new JLabel("Ready. Enter a number and click PUSH.");
    private final Random random = new Random();

    public StackVisualizerGUI() {
        super("Stack Visualization Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        add(createInputPanel(), BorderLayout.NORTH);
        add(createVisualizationPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        refreshStackView();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 12));
        panel.setBorder(BorderFactory.createTitledBorder("Enter a value"));
        panel.add(new JLabel("Value:"));
        panel.add(valueField);

        JButton pushButton = new JButton("PUSH");
        pushButton.addActionListener(event -> pushValue());
        valueField.addActionListener(event -> pushValue());
        panel.add(pushButton);

        JButton randomButton = new JButton("RANDOM");
        randomButton.setToolTipText("Generate a random number from 1 to 100 and push it");
        randomButton.addActionListener(event -> pushRandomValue());
        panel.add(randomButton);
        return panel;
    }

    private JPanel createVisualizationPanel() {
        JPanel outer = new JPanel(new BorderLayout(8, 8));
        outer.setBorder(BorderFactory.createTitledBorder("Stack (newest item at the top)"));

        stackPanel.setLayout(new BoxLayout(stackPanel, BoxLayout.Y_AXIS));
        stackPanel.setBackground(Color.WHITE);
        stackPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(stackPanel);
        scrollPane.setPreferredSize(new Dimension(500, 330));
        outer.add(scrollPane, BorderLayout.CENTER);
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        outer.add(countLabel, BorderLayout.SOUTH);
        return outer;
    }

    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        JPanel buttons = new JPanel(new GridLayout(1, 4, 8, 8));
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        JButton popButton = new JButton("POP");
        popButton.addActionListener(event -> popValue());
        JButton peekButton = new JButton("PEEK");
        peekButton.addActionListener(event -> peekValue());
        JButton clearButton = new JButton("CLEAR");
        clearButton.addActionListener(event -> clearStack());
        JButton checkButton = new JButton("CHECK STACK");
        checkButton.addActionListener(event -> checkStack());

        buttons.add(popButton);
        buttons.add(peekButton);
        buttons.add(clearButton);
        buttons.add(checkButton);

        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        bottom.add(buttons, BorderLayout.CENTER);
        bottom.add(statusPanel, BorderLayout.SOUTH);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 4));
        return bottom;
    }

    private void pushValue() {
        String input = valueField.getText().trim();
        if (input.isEmpty()) {
            setStatus("Please enter an integer before clicking PUSH.");
            valueField.requestFocusInWindow();
            return;
        }

        try {
            int value = Integer.parseInt(input);
            stack.push(value);
            valueField.setText("");
            refreshStackView();
            setStatus("Pushed " + value + " onto the stack.");
            valueField.requestFocusInWindow();
        } catch (NumberFormatException exception) {
            setStatus("Invalid input. Please enter a whole number, such as 25.");
        } catch (IllegalStateException exception) {
            setStatus(exception.getMessage());
        }
    }

    private void pushRandomValue() {
        int value = random.nextInt(100) + 1;
        try {
            stack.push(value);
            refreshStackView();
            setStatus("Generated and pushed random value " + value + ".");
        } catch (IllegalStateException exception) {
            setStatus(exception.getMessage());
        }
    }

    private void popValue() {
        try {
            int removed = stack.pop();
            refreshStackView();
            setStatus("Popped and removed " + removed + " from the stack.");
        } catch (IllegalStateException exception) {
            setStatus(exception.getMessage());
        }
    }

    private void peekValue() {
        try {
            int top = stack.peek();
            setStatus("Peek: the top value is " + top + ". It was not removed.");
        } catch (IllegalStateException exception) {
            setStatus(exception.getMessage());
        }
    }

    private void clearStack() {
        stack.clear();
        refreshStackView();
        setStatus("Stack cleared.");
    }

    private void checkStack() {
        if (stack.isEmpty()) {
            setStatus("Check Stack: the stack is empty. Size = 0.");
        } else {
            setStatus("Check Stack: size = " + stack.size() + "; top = " + stack.peek() + ".");
        }
    }

    /** Rebuilds the display using a copy supplied by StackADT. */
    private void refreshStackView() {
        stackPanel.removeAll();
        List<Integer> values = stack.getElementsTopToBottom();

        if (values.isEmpty()) {
            JLabel emptyLabel = new JLabel("[ Stack is empty ]", SwingConstants.CENTER);
            emptyLabel.setAlignmentX(CENTER_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(18, 5, 18, 5));
            stackPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < values.size(); i++) {
                int value = values.get(i);
                String text = (i == 0 ? "TOP  ->  " : "        ") + value;
                JLabel item = new JLabel(text, SwingConstants.CENTER);
                item.setOpaque(true);
                item.setBackground(i == 0 ? new Color(210, 235, 255) : new Color(238, 242, 247));
                item.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(120, 135, 150)),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)));
                item.setFont(new Font(Font.MONOSPACED, i == 0 ? Font.BOLD : Font.PLAIN, 15));
                item.setAlignmentX(CENTER_ALIGNMENT);
                item.setMaximumSize(new Dimension(280, 42));
                item.setPreferredSize(new Dimension(280, 42));
                stackPanel.add(item);
                stackPanel.add(Box.createVerticalStrut(6));
            }
        }

        countLabel.setText("Size: " + stack.size() + " / " + StackADT.MAX_CAPACITY);
        stackPanel.revalidate();
        stackPanel.repaint();
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StackVisualizerGUI gui = new StackVisualizerGUI();
            gui.setVisible(true);
        });
    }
}
