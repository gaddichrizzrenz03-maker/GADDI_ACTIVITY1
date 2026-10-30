import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LibrarySystem1 {
    private static final Color NAVY = new Color(24, 37, 65);
    private static final Color BLUE = new Color(63, 111, 220);
    private static final Color BG = new Color(245, 247, 251);
    private static final Color TEXT = new Color(43, 52, 69);
    private static final int COVER_WIDTH = 60;
    private static final int COVER_HEIGHT = 76;

    private final List<Book> books = new ArrayList<>();
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private DefaultTableModel tableModel;
    private JTable bookTable;
    private JLabel totalValue, availableValue, borrowedValue;
    private JTextField searchField;
    private Image backgroundImage;
    private File currentLibraryFile;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySystem1().showLoadingScreen());
    }

    private void showLoadingScreen() {
        JFrame loading = new JFrame("LibrarySystem1");
        loading.setUndecorated(true);
        loading.setSize(470, 250);
        loading.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(NAVY);
        panel.setBorder(new EmptyBorder(28, 32, 26, 32));

        JLabel title = new JLabel("LibrarySystem1", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 29));

        JLabel subtitle = new JLabel("Your library, organized.", SwingConstants.CENTER);
        subtitle.setForeground(new Color(205, 216, 239));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);
        progress.setForeground(new Color(104, 158, 255));
        progress.setBackground(new Color(47, 63, 94));
        progress.setBorderPainted(false);

        JLabel status = new JLabel("Starting library system...", SwingConstants.CENTER);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 7));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(subtitle);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(progress, BorderLayout.CENTER);
        panel.add(status, BorderLayout.SOUTH);
        loading.setContentPane(panel);
        loading.setVisible(true);

        String[] messages = {
            "Preparing dashboard...", "Loading book catalog...",
            "Setting up library tools...", "Almost ready..."
        };
        Timer timer = new Timer(35, null);
        timer.addActionListener(e -> {
            int value = progress.getValue() + 2;
            progress.setValue(value);
            status.setText(messages[Math.min(value / 26, messages.length - 1)]);
            if (value >= 100) {
                timer.stop();
                loading.dispose();
                seedBooks();
                createMainWindow();
            }
        });
        timer.start();
    }

    private void seedBooks() {
        books.add(new Book("B001", "Noli Me Tangere", "Jose Rizal", false, null));
        books.add(new Book("B002", "El Filibusterismo", "Jose Rizal", true, null));
        books.add(new Book("B003", "Java: A Beginner's Guide", "Herbert Schildt", false, null));
        books.add(new Book("B004", "The Little Prince", "Antoine de Saint-Exupery", false, null));
        books.add(new Book("B005", "Introduction to Algorithms", "Thomas H. Cormen", true, null));
        books.add(new Book("B006", "Chess Openings", "Bobby Ficher", true, null));
        books.add(new Book("B007", "The Art of Computer Programming", "Donald E. Knuth", true, null));
        books.add(new Book("B008", "Clean Code", "Robert C. Martin", false, null));
        books.add(new Book("B009", "Design Patterns", "Gang of Four", true, null));
        books.add(new Book("B010", "Effective Java", "Joshua Bloch", false, null));
        books.add(new Book("B011", "The Pragmatic Programmer", "Andrew Hunt", false, null));
        books.add(new Book("B012", "Head First Design Patterns", "Eric Freeman", true, null));
        books.add(new Book("B013", "Refactoring", "Martin Fowler", false, null));
        books.add(new Book("B014", "The Mythical Man-Month", "Frederick P. Brooks Jr.", false, null));
        books.add(new Book("B015", "Code Complete", "Steve McConnell", true, null));
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = new ImageIcon("library_bg.jpg").getImage();
        } catch (Exception e) {
            backgroundImage = null;
            System.out.println("Background image not found: " + e.getMessage());
        }
    }

    private void createMainWindow() {
        frame = new JFrame("LibrarySystem1 | Library Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1050, 680);
        frame.setMinimumSize(new Dimension(850, 560));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        loadBackgroundImage();

        JPanel sidebar = new JPanel();
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(215, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(22, 14, 18, 14));

        JLabel brand = new JLabel("  LibrarySystem1");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 19));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(8));

        JLabel sub = new JLabel("  LIBRARY MANAGEMENT");
        sub.setForeground(new Color(164, 181, 216));
        sub.setFont(new Font("SansSerif", Font.BOLD, 10));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sub);
        sidebar.add(Box.createVerticalStrut(32));

        addNavButton(sidebar, "Dashboard", "dashboard");
        addNavButton(sidebar, "Books Catalog", "books");
        addNavButton(sidebar, "Borrow / Return", "books");
        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("<html><font color='#A4B5D8'>LibrarySystem1</font><br>"
                + "<font color='#7185AD'>Java Swing Edition</font></html>");
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(footer);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    g2.dispose();
                }
            }
        };
        contentPanel.setOpaque(false);

        contentPanel.add(createDashboard(), "dashboard");
        contentPanel.add(createBooksPage(), "books");

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(contentPanel, BorderLayout.CENTER);
        refreshData();
        frame.setVisible(true);
    }

    private void addNavButton(JPanel sidebar, String label, String card) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        button.setPreferredSize(new Dimension(185, 43));
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(card.equals("dashboard") && label.equals("Dashboard")
                ? BLUE : NAVY);
        button.setBorder(new EmptyBorder(8, 14, 8, 8));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, card);
            if (card.equals("books")) refreshData();
        });
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(7));
    }

    private JPanel createDashboard() {
        JPanel page = new JPanel(new BorderLayout(18, 18));
        page.setBackground(new Color(245, 247, 251, 180));
        page.setBorder(new EmptyBorder(28, 28, 28, 28));

        JLabel heading = new JLabel("Dashboard");
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));
        heading.setForeground(TEXT);

        JLabel description = new JLabel("Welcome back! Here's your library overview.");
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setForeground(new Color(112, 122, 141));

        JPanel top = new JPanel(new GridLayout(2, 1, 0, 5));
        top.setOpaque(false);
        top.add(heading);
        top.add(description);

        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setOpaque(false);

        totalValue = new JLabel("0");
        availableValue = new JLabel("0");
        borrowedValue = new JLabel("0");

        stats.add(statCard("TOTAL BOOKS", totalValue, BLUE));
        stats.add(statCard("AVAILABLE", availableValue, new Color(35, 157, 111)));
        stats.add(statCard("BORROWED", borrowedValue, new Color(220, 145, 45)));

        JPanel quick = new JPanel(new BorderLayout(10, 10));
        quick.setBackground(new Color(255, 255, 255, 220));
        quick.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 233, 241)),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel quickTitle = new JLabel("Quick actions");
        quickTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel quickText = new JLabel("<html>Manage your collection, add new titles, or record a book loan.</html>");
        quickText.setForeground(new Color(112, 122, 141));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actions.setOpaque(false);

        JButton catalog = primaryButton("Open Books Catalog");
        catalog.addActionListener(e -> cardLayout.show(contentPanel, "books"));

        JButton add = secondaryButton("+ Add a Book");
        add.addActionListener(e -> {
            cardLayout.show(contentPanel, "books");
            showAddBookDialog();
        });

        actions.add(catalog);
        actions.add(add);

        JPanel quickTop = new JPanel(new GridLayout(2, 1, 0, 8));
        quickTop.setOpaque(false);
        quickTop.add(quickTitle);
        quickTop.add(quickText);

        quick.add(quickTop, BorderLayout.NORTH);
        quick.add(actions, BorderLayout.CENTER);

        JPanel middle = new JPanel(new BorderLayout(0, 18));
        middle.setOpaque(false);
        middle.add(stats, BorderLayout.NORTH);
        middle.add(quick, BorderLayout.CENTER);

        page.add(top, BorderLayout.NORTH);
        page.add(middle, BorderLayout.CENTER);
        return page;
    }

    private JPanel statCard(String title, JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(new Color(255, 255, 255, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(229, 233, 241)),
                        new EmptyBorder(17, 16, 17, 16))));

        JLabel label = new JLabel(title);
        label.setForeground(new Color(112, 122, 141));
        label.setFont(new Font("SansSerif", Font.BOLD, 11));

        value.setForeground(TEXT);
        value.setFont(new Font("SansSerif", Font.BOLD, 30));

        card.add(label, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JPanel createBooksPage() {
        JPanel page = new JPanel(new BorderLayout(14, 14));
        page.setBackground(new Color(245, 247, 251, 180));
        page.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel heading = new JLabel("Books Catalog");
        heading.setFont(new Font("SansSerif", Font.BOLD, 26));
        heading.setForeground(TEXT);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setToolTipText("Search by ID, title, or author");

        JButton search = primaryButton("Search");
        search.addActionListener(e -> filterTable());

        JButton reset = secondaryButton("Show All");
        reset.addActionListener(e -> {
            searchField.setText("");
            refreshData();
        });

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setOpaque(false);
        searchBar.add(searchField);
        searchBar.add(search);
        searchBar.add(reset);

        JPanel header = new JPanel(new BorderLayout(10, 12));
        header.setOpaque(false);
        header.add(heading, BorderLayout.NORTH);
        header.add(searchBar, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new Object[]{"Cover", "Book ID", "Title", "Author", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(64);
        bookTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bookTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        bookTable.getTableHeader().setBackground(new Color(235, 239, 247));
        bookTable.setSelectionBackground(new Color(218, 229, 255));
        bookTable.setGridColor(new Color(232, 236, 243));
        bookTable.setBackground(new Color(255, 255, 255, 220));
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        bookTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        bookTable.getColumnModel().getColumn(0).setMinWidth(70);
        bookTable.getColumnModel().getColumn(0).setMaxWidth(70);

        JScrollPane scroll = new JScrollPane(bookTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(225, 230, 239)));

        JButton add = primaryButton("+ Add Book");
        add.addActionListener(e -> showAddBookDialog());

        JButton openFile = secondaryButton("Open File");
        openFile.addActionListener(e -> openLibraryFile());

        JButton saveAs = secondaryButton("Save As");
        saveAs.addActionListener(e -> saveLibraryFileAs());

        JButton borrow = secondaryButton("Borrow Selected");
        borrow.addActionListener(e -> changeBookStatus(true));

        JButton returnBook = secondaryButton("Return Selected");
        returnBook.addActionListener(e -> changeBookStatus(false));

        JButton remove = secondaryButton("Remove Selected");
        remove.addActionListener(e -> removeSelectedBook());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(add);
        actions.add(openFile);
        actions.add(saveAs);
        actions.add(borrow);
        actions.add(returnBook);
        actions.add(remove);

        page.add(header, BorderLayout.NORTH);
        page.add(scroll, BorderLayout.CENTER);
        page.add(actions, BorderLayout.SOUTH);
        return page;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(9, 14, 9, 14));
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 223, 235)),
                new EmptyBorder(8, 12, 8, 12)));
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return button;
    }

    private void refreshData() {
        if (tableModel != null) {
            tableModel.setRowCount(0);
            for (Book book : books) addBookRow(book);
        }
        if (totalValue != null) {
            int borrowed = 0;
            for (Book book : books) if (book.borrowed) borrowed++;
            totalValue.setText(String.valueOf(books.size()));
            borrowedValue.setText(String.valueOf(borrowed));
            availableValue.setText(String.valueOf(books.size() - borrowed));
        }
    }

    private void addBookRow(Book book) {
        tableModel.addRow(new Object[]{
                getBookCoverIcon(book),
                book.id,
                book.title,
                book.author,
                book.borrowed ? "Borrowed" : "Available"
        });
    }

    private ImageIcon getBookCoverIcon(Book book) {
        if (book == null) return createPlaceholderCover();
        if (book.coverIcon != null) return book.coverIcon;
        return createPlaceholderCover();
    }

    private ImageIcon createPlaceholderCover() {
        BufferedImage image = new BufferedImage(COVER_WIDTH, COVER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(new Color(233, 239, 251));
        g2.fillRoundRect(0, 0, COVER_WIDTH, COVER_HEIGHT, 16, 16);
        g2.setColor(BLUE);
        g2.drawRoundRect(0, 0, COVER_WIDTH - 1, COVER_HEIGHT - 1, 16, 16);
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2.setColor(BLUE);
        g2.drawString("BOOK", 9, COVER_HEIGHT / 2 + 4);
        g2.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon loadScaledIcon(File imageFile) {
        if (imageFile == null || !imageFile.exists()) return createPlaceholderCover();

        try {
            BufferedImage original = ImageIO.read(imageFile);
            if (original == null) return createPlaceholderCover();

            BufferedImage scaled = new BufferedImage(COVER_WIDTH, COVER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaled.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(original, 0, 0, COVER_WIDTH, COVER_HEIGHT, null);
            g2.dispose();
            return new ImageIcon(scaled);
        } catch (IOException e) {
            return createPlaceholderCover();
        }
    }

    private void filterTable() {
        String query = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (Book book : books) {
            if (book.id.toLowerCase().contains(query)
                    || book.title.toLowerCase().contains(query)
                    || book.author.toLowerCase().contains(query)) {
                addBookRow(book);
            }
        }
    }

    private void showAddBookDialog() {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JLabel coverPreview = new JLabel();
        coverPreview.setPreferredSize(new Dimension(90, 110));
        coverPreview.setHorizontalAlignment(SwingConstants.CENTER);
        coverPreview.setBorder(BorderFactory.createLineBorder(new Color(216, 223, 235), 1));
        coverPreview.setIcon(createPlaceholderCover());

        final String[] selectedImagePath = {null};

        JButton chooseCover = secondaryButton("Choose Cover Image");
        chooseCover.addActionListener(e -> {
            File imageFile = chooseImageFile();
            if (imageFile == null) return;
            selectedImagePath[0] = imageFile.getAbsolutePath();
            coverPreview.setIcon(loadScaledIcon(imageFile));
        });

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Book ID:"));
        form.add(idField);
        form.add(new JLabel("Book title:"));
        form.add(titleField);
        form.add(new JLabel("Author:"));
        form.add(authorField);

        JPanel coverPanel = new JPanel(new BorderLayout(8, 0));
        coverPanel.add(new JLabel("Cover photo:"), BorderLayout.NORTH);
        coverPanel.add(coverPreview, BorderLayout.CENTER);
        coverPanel.add(chooseCover, BorderLayout.SOUTH);

        form.add(coverPanel);

        int result = JOptionPane.showConfirmDialog(frame, form, "Add a Book",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String id = idField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.",
                    "Missing information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (Book book : books) {
            if (book.id.equalsIgnoreCase(id)) {
                JOptionPane.showMessageDialog(frame, "That Book ID already exists.",
                        "Duplicate ID", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        books.add(new Book(id, title, author, false, selectedImagePath[0]));
        saveBooks();
        refreshData();
        JOptionPane.showMessageDialog(frame, "Book added successfully.");
    }

    private File chooseImageFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose Book Cover");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                "Image Files (*.jpg, *.jpeg, *.png, *.gif, *.bmp)", "jpg", "jpeg", "png", "gif", "bmp");
        chooser.setFileFilter(imageFilter);

        if (currentLibraryFile != null) {
            chooser.setCurrentDirectory(currentLibraryFile.getParentFile());
        } else {
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }

        int result = chooser.showOpenDialog(frame);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    private Book getSelectedBook() {
        int row = bookTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(frame, "Please select a book from the table.",
                    "No book selected", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        String id = String.valueOf(tableModel.getValueAt(row, 1));
        for (Book book : books) if (book.id.equals(id)) return book;
        return null;
    }

    private void changeBookStatus(boolean borrow) {
        Book book = getSelectedBook();
        if (book == null) return;

        if (borrow && book.borrowed) {
            JOptionPane.showMessageDialog(frame, "This book is already borrowed.");
            return;
        }

        if (!borrow && !book.borrowed) {
            JOptionPane.showMessageDialog(frame, "This book is already marked available.");
            return;
        }

        book.borrowed = borrow;
        saveBooks();
        refreshData();

        JOptionPane.showMessageDialog(frame, borrow
                ? "Book marked as borrowed." : "Book returned successfully.");
    }

    private void removeSelectedBook() {
        Book book = getSelectedBook();
        if (book == null) return;

        int answer = JOptionPane.showConfirmDialog(frame,
                "Remove \"" + book.title + "\" from the catalog?",
                "Confirm removal", JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            books.remove(book);
            saveBooks();
            refreshData();
        }
    }

    private void openLibraryFile() {
        File file = chooseFile("Open Library File", false);
        if (file == null) return;

        try {
            loadBooksFromFile(file);
            currentLibraryFile = file;
            JOptionPane.showMessageDialog(frame, "Library file opened successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,
                    "Unable to open file:\n" + e.getMessage(),
                    "Open Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveLibraryFileAs() {
        File file = chooseFile("Save Library File", true);
        if (file == null) return;

        try {
            saveBooksToFile(file);
            currentLibraryFile = file;
            JOptionPane.showMessageDialog(frame, "Library saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,
                    "Unable to save file:\n" + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private File chooseFile(String title, boolean saveMode) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(true);

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Library Files (*.txt)", "txt");
        chooser.setFileFilter(filter);

        if (currentLibraryFile != null) {
            chooser.setCurrentDirectory(currentLibraryFile.getParentFile());
            chooser.setSelectedFile(currentLibraryFile);
        } else {
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }

        int result = saveMode
                ? chooser.showSaveDialog(frame)
                : chooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();

            if (saveMode && !selected.getName().toLowerCase().endsWith(".txt")) {
                selected = new File(selected.getAbsolutePath() + ".txt");
            }

            return selected;
        }

        return null;
    }

    private void loadBooksFromFile(File file) throws IOException {
        books.clear();

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                String[] parts = trimmed.split("\\|", -1);
                if (parts.length >= 4) {
                    String id = parts[0];
                    String title = parts[1];
                    String author = parts[2];
                    boolean borrowed = Boolean.parseBoolean(parts[3]);
                    String imagePath = (parts.length >= 5) ? parts[4].trim() : "";

                    File resolvedImage = resolveImagePath(file, imagePath);
                    books.add(new Book(
                            id,
                            title,
                            author,
                            borrowed,
                            resolvedImage != null ? resolvedImage.getAbsolutePath() : null
                    ));
                }
            }
        }

        refreshData();
    }

    private File resolveImagePath(File libraryFile, String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) return null;

        File path = new File(imagePath.trim());
        if (path.isAbsolute()) {
            return path;
        }

        if (libraryFile != null && libraryFile.getParentFile() != null) {
            File relative = new File(libraryFile.getParentFile(), imagePath.trim());
            return relative.exists() ? relative : null;
        }

        return null;
    }

    private void saveBooksToFile(File file) throws IOException {
        List<String> lines = new ArrayList<>();

        for (Book book : books) {
            lines.add(
                    book.id + "|" +
                            book.title + "|" +
                            book.author + "|" +
                            book.borrowed + "|" +
                            (book.imagePath == null ? "" : book.imagePath)
            );
        }

        Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
    }

    private void saveBooks() {
        if (currentLibraryFile != null) {
            try {
                saveBooksToFile(currentLibraryFile);
                return;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Unable to save books to selected file:\n" + e.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        File file = chooseFile("Save Library File", true);
        if (file == null) return;

        try {
            saveBooksToFile(file);
            currentLibraryFile = file;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Unable to save books:\n" + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static class Book {
        final String id;
        final String title;
        final String author;
        boolean borrowed;
        String imagePath;
        ImageIcon coverIcon;

        Book(String id, String title, String author, boolean borrowed, String imagePath) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.borrowed = borrowed;
            this.imagePath = imagePath;
            this.coverIcon = resolveCoverIcon(imagePath);
        }

        private static ImageIcon resolveCoverIcon(String imagePath) {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                return null;
            }

            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                return null;
            }

            try {
                BufferedImage original = ImageIO.read(imageFile);
                if (original == null) {
                    return null;
                }

                BufferedImage scaled = new BufferedImage(COVER_WIDTH, COVER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = scaled.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(original, 0, 0, COVER_WIDTH, COVER_HEIGHT, null);
                g2.dispose();
                return new ImageIcon(scaled);
            } catch (IOException e) {
                return null;
            }
        }
    }
}