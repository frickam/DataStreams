import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class SwingFileSearchApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingFileSearchApp().createAndShowGUI());
    }

    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private Path loadedFilePath;

    private void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("File Search App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Create text areas with scroll panes
        originalTextArea = new JTextArea();
        filteredTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);

        JScrollPane originalScrollPane = new JScrollPane(originalTextArea);
        JScrollPane filteredScrollPane = new JScrollPane(filteredTextArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, originalScrollPane, filteredScrollPane);
        splitPane.setResizeWeight(0.5);

        frame.add(splitPane, BorderLayout.CENTER);

        // Create the search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());

        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton, BorderLayout.EAST);

        frame.add(searchPanel, BorderLayout.NORTH);

        // Create the button panel
        JPanel buttonPanel = new JPanel();
        JButton loadButton = new JButton("Load File");
        JButton quitButton = new JButton("Quit");

        buttonPanel.add(loadButton);
        buttonPanel.add(quitButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Add button action listeners
        loadButton.addActionListener(new LoadFileAction());
        searchButton.addActionListener(new SearchFileAction());
        quitButton.addActionListener(e -> System.exit(0));

        // Display the frame
        frame.setVisible(true);
    }

    private class LoadFileAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                loadedFilePath = fileChooser.getSelectedFile().toPath();
                loadFileContent();
            }
        }

        private void loadFileContent() {
            originalTextArea.setText("");
            filteredTextArea.setText("");
            try (Stream<String> lines = Files.lines(loadedFilePath)) {
                lines.forEach(line -> originalTextArea.append(line + "\n"));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error loading file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SearchFileAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (loadedFilePath == null) {
                JOptionPane.showMessageDialog(null, "Please load a file first.",
                        "No File Loaded", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String searchString = searchField.getText().trim();
            if (searchString.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a search string.",
                        "Empty Search String", JOptionPane.WARNING_MESSAGE);
                return;
            }

            filteredTextArea.setText(""); // Clear the filtered text area
            try (Stream<String> lines = Files.lines(loadedFilePath)) {
                long count = lines.filter(line -> line.contains(searchString))
                        .peek(line -> filteredTextArea.append(line + "\n"))
                        .count();

                if (count == 0) {
                    JOptionPane.showMessageDialog(null, "No matching lines found.",
                            "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error searching file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}