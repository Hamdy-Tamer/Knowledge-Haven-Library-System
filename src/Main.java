import java.util.*;
import javax.swing.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.awt.event.*;

// Book class representing a book in the library
class Books {
    // Initializers
    private int id;
    private String name;
    private String category;
    private boolean borrowed;
    private Date borrowingDate;
    private int borrowingPeriod;
    private Date returnDate;

    // Constructor
    public Books(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.borrowed = false;
    }

    //SETTERS AND GETTERS
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public boolean getBorrowed() {
        return borrowed;
    }

    public void setBorrowingDate(Date borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public Date getBorrowingDate() {
        return borrowingDate;
    }

    public void setBorrowingPeriod(int borrowingPeriod) {
        this.borrowingPeriod = borrowingPeriod;
    }

    public int getBorrowingPeriod() {
        return borrowingPeriod;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }
}

class Library {
    // Initializers
    private int capacity;
    public List<Books> books;
    private Map<String, Integer> categoryCounts;
    private boolean changesOccurred; // Flag to indicate changes

    //CONSTRUCTORS
    public Library(int capacity) {
        this.capacity = capacity;
        this.books = new ArrayList<>();
        this.categoryCounts = new HashMap<>();
        changesOccurred = false;
    }

    //GETER
    public int getCapacity() {
        return capacity;
    }

    //ADD BOOK
    public boolean addBook(Books book) {
        int newId;
        //IF THERE ARE NO BOOKS SET ID TO 1
        if (books.isEmpty()) {
            newId = 1;
        }
        //ELSE SET ID = PREVIOUS ID +1
        else {
            newId = books.get(books.size() - 1).getId() + 1;
        }
        book.setId(newId);
        books.add(book);

        // ADD BOOK CATEGORY
        String category = book.getCategory();
        //ADD NUMBER OF BOOKS IN CATEGORY
        categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
        changesOccurred = true;
        return true;
    }

    //REMOVE BOOK
    public boolean removeBook(int id) {
        for (Books book : books) {
            if (book.getId() == id) {//IF BOOK ID EXISTS
                if (!book.getBorrowed()) {//IF BOOK ISN'T BORROWED
                    books.remove(book);
                    String category = book.getCategory();//FIND CATEGORY OF BOOK
                    categoryCounts.put(category, categoryCounts.get(category) - 1);//REMOVE BOOK FROM CATEGORY
                    changesOccurred = true;
                    return true;
                } else {
                    return false;// Can't remove borrowed book
                }
            }
        }
        return false;// Book ID not found
    }

    //BORROW BOOK
    public boolean borrowBook(int id, int borrowingPeriod) {
        for (Books book : books) {
            if (book.getId() == id) {//IF BOOK ID EXISTS
                if (!book.getBorrowed()) {//IF BOOK ISN'T BORROWED
                    book.setBorrowed(true);

                    Calendar calendar = Calendar.getInstance();
                    Date borrowingDate = new Date(); // Set borrowing date to current date
                    calendar.setTime(borrowingDate);
                    calendar.add(Calendar.DAY_OF_YEAR, borrowingPeriod);
                    Date returnDate = calendar.getTime();

                    book.setBorrowingDate(borrowingDate); // Set borrowing date
                    book.setBorrowingPeriod(borrowingPeriod);
                    book.setReturnDate(returnDate);
                    changesOccurred = true;
                    return true;
                } else {
                    return false;// Book already borrowed
                }
            }
        }
        return false;// Book ID not found
    }



    public boolean returnBook(int id) {//RETURN BOOK
        for (Books book : books) {
            if (book.getId() == id) {//IF BOOK ID EXISTS
                if (book.getBorrowed()) {//IF BOOK IS BORROWED
                    book.setBorrowed(false);
                    changesOccurred = true;
                    return true;
                } else {
                    return false;// Book not borrowed
                }
            }
        }
        return false;// Book ID not found
    }


    public String viewLibraryStatus() {//VIEW LIBRARY
        StringBuilder status = new StringBuilder("\nLibrary Status:\n");
        status.append("Total Capacity: ").append(capacity).append("\n");//total capacity of library
        status.append("Number of Books: ").append(books.size()).append("\n");//number of books in library

        for (String category : new String[]{"Biology", "Maths", "History", "Chemistry", "Politics"}) {// Append category counts
            int count = categoryCounts.getOrDefault(category, 0);
            status.append(category).append(" Books: ").append(count).append("\n");
        }

        int borrowedCount = 0;//NUMBER OF BORROWED BOOKS
        for (Books book : books) {
            if (book.getBorrowed()) {
                borrowedCount++;
                status.append("Borrowed Book - ID: ").append(book.getId()).append(", Name: ").append(book.getName()).append(", Category: ")
                        .append(book.getCategory()).append(", Borrowing Period: ").append(book.getBorrowingPeriod()).append(" days, Borrowing Date: ")
                        .append(book.getBorrowingDate()).append(", Return Date: ").append(book.getReturnDate()).append("\n");
            } else {
                status.append("Available Book - ID: ").append(book.getId()).append(", Name: ").append(book.getName()).append(", Category: ")
                        .append(book.getCategory()).append("\n");
            }
        }
        status.append("Total Borrowed Books: ").append(borrowedCount).append("\n");
        return status.toString();
    }

    // Getter for category counts
    public Map<String, Integer> getCategoryCounts() {
        return categoryCounts;
    }
    // Method to check if changes occurred in the library
    public boolean changesOccurred() {
        return changesOccurred;
    }
    // Method to clear the changes flag
    public void clearChanges() {
        changesOccurred = false;
    }
}

class LibrarySystemGUI extends JFrame {
    //Initializers
    private Library library;
    private JTextField bookNameField;
    private JComboBox<String> categoryComboBox;
    private JTextArea statusTextArea;
    private boolean statusRequested;
    private JPanel addBookPanel;
    private JButton addBookButton;

    // Constructor
    public LibrarySystemGUI(int capacity) {
        super("Knowledge Haven Library System");//TITLE
        library = new Library(capacity);
        statusRequested = false;

        setSize(600, 400);//SIZE OF WINDOW
        setLocationRelativeTo(null);//CENTER WINDOW

        // Set the image icon
        ImageIcon icon = new ImageIcon("Library.jpeg");
        setIconImage(icon.getImage());

        initComponents();

        addWindowListener(new WindowAdapter() {//CLOSE WINDOW
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    // Initialize
    private void initComponents() {
        JPanel mainPanel = new JPanel();// Main panel layout
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));//MAKE PANEL LAYOUT IN X-AXIS

        addBookPanel = createAddBookPanel();//PANEL TO ADD BOOK
        mainPanel.add(addBookPanel);

        statusTextArea = new JTextArea(150, 150);//TEXT AREA SIZE
        statusTextArea.setEditable(false);//MAKE IT UNEDITABLE
        JScrollPane scrollPane = new JScrollPane(statusTextArea);//ADDING SCROLLPANE TO SHOW TEXT
        mainPanel.add(scrollPane);

        setContentPane(mainPanel);

        handleMenu();// Handle menu operations
    }

    // Create the panel for adding a book
    private JPanel createAddBookPanel() {//ADD BOOK PANNEL
        addBookPanel = new JPanel();

        JLabel nameLabel = new JLabel("Book Name:");//ADD BOOK PANEL LABEL
        bookNameField = new JTextField(30);//SETTING TEXT FIELD LIMIT

        JLabel categoryLabel = new JLabel("Category:");//ADDING CATEGORY LABEL
        String[] categories = {"Biology", "Maths", "History", "Chemistry", "Politics"};
        categoryComboBox = new JComboBox<>(categories);//ADDING A COMBO BOX

        addBookButton = new JButton("Add Book");//ADD BOOK BUTTON
        addBookButton.addActionListener(e -> addBook());

        addBookPanel.add(nameLabel);
        addBookPanel.add(bookNameField);
        addBookPanel.add(categoryLabel);
        addBookPanel.add(categoryComboBox);
        addBookPanel.add(addBookButton);

        return addBookPanel;
    }


    private void addBook() {
        String name = bookNameField.getText();
        String category = (String) categoryComboBox.getSelectedItem();
        if (!name.isEmpty()) {
            Books newBook = new Books(library.books.size() + 1, name, category);
            library.addBook(newBook);
            bookNameField.setText("");

            // Update status display directly
            updateStatus();

            // Show message dialog
            JOptionPane.showMessageDialog(this, "Book is added successfully.\nBook ID: " + newBook.getId(), "Success", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("success.jpg"));

            // Check if the library is now at full capacity
            if (library.books.size() >= library.getCapacity()) {
                setAddingComponentsEnabled(false);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter the name of the book.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
        }
    }


    // Handle menu operations
    private void handleMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Operations");
        menuBar.add(menu);

        JMenuItem viewStatusItem = new JMenuItem("View Library Status");
        viewStatusItem.addActionListener(e -> {
            statusRequested = true;
            updateStatus();
        });
        menu.add(viewStatusItem); //VIEW CHOISE

        JMenuItem removeBookItem = new JMenuItem("Remove Book");
        removeBookItem.addActionListener(e -> {
            clearStatus();
            removeBook();
        });
        menu.add(removeBookItem);

        JMenuItem borrowBookItem = new JMenuItem("Borrow Book");
        borrowBookItem.addActionListener(e -> {
            clearStatus();
            borrowBook();
        });
        menu.add(borrowBookItem);

        JMenuItem returnBookItem = new JMenuItem("Return Book");
        returnBookItem.addActionListener(e -> {
            clearStatus();
            returnBook();
        });
        menu.add(returnBookItem);

        setJMenuBar(menuBar); //SHOW THE MENU BUTTON IN GUI
    }



    private void removeBook() {
        while (true) {
            String input = JOptionPane.showInputDialog(this, "Enter Book ID to Remove:");
            if (input != null) {
                try {
                    int id = Integer.parseInt(input);
                    if (id <= 0) {
                        // Show message dialog if ID is zero or negative
                        JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid ID.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                        continue; // Continue to next iteration of the loop
                    }
                    Books bookToRemove = null;
                    for (Books book : library.books) {
                        if (book.getId() == id) {
                            bookToRemove = book;
                            break;
                        }
                    }
                    if (bookToRemove != null) {
                        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the book with ID " + id + " ?", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon("delete.jpg"));
                        if (confirm == JOptionPane.YES_OPTION) {
                            if (!bookToRemove.getBorrowed()) {
                                boolean removed = library.removeBook(id);
                                if (removed) {
                                    updateStatus();

                                    // Check if a book was removed and enable adding components if the library is not at full capacity, so the input opens again
                                    if (library.books.size() < library.getCapacity()) {
                                        setAddingComponentsEnabled(true);
                                    }

                                    // Show success message dialog
                                    JOptionPane.showMessageDialog(this, "Book has been removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("success.jpg"));
                                }
                            } else {
                                // Show message dialog if the book is borrowed
                                JOptionPane.showMessageDialog(this, "Sorry! This book is currently borrowed.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                            }
                        }
                    } else {
                        // Show message dialog if book with entered ID doesn't exist
                        JOptionPane.showMessageDialog(this, "Book with ID " + id + " doesn't exist.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                    }
                    return; // Exit the loop
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid Book ID.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                }
            } else {
                return; // Exit the loop if user cancels the input dialog
            }
        }
    }



    private void setAddingComponentsEnabled(boolean enabled) {
        bookNameField.setEnabled(enabled);
        categoryComboBox.setEnabled(enabled);
        addBookButton.setEnabled(enabled);
    }


    private void borrowBook() {
        while (true) {
            String input = JOptionPane.showInputDialog(this, "Enter Book ID to Borrow:");
            if (input != null) {
                try {
                    int id = Integer.parseInt(input);
                    if (id <= 0) {
                        // Show message dialog if ID is zero or negative
                        JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid ID.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                        continue; // Continue to next iteration of the loop
                    }
                    Books bookToBorrow = null;
                    for (Books book : library.books) {
                        if (book.getId() == id) {
                            bookToBorrow = book;
                            break;
                        }
                    }
                    if (bookToBorrow == null) {
                        // Show message dialog if book with entered ID doesn't exist
                        JOptionPane.showMessageDialog(this, "Book with ID " + id + " doesn't exist.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                        return; // Exit the method
                    }
                    if (bookToBorrow.getBorrowed()) {
                        // Show message dialog if the book is currently borrowed
                        JOptionPane.showMessageDialog(this, "Sorry! This book is currently borrowed.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                        return; // Exit the method
                    }
                    String periodInput = JOptionPane.showInputDialog(this, "Enter Borrowing Period (in days):");
                    if (periodInput != null) {
                        try {
                            int period = Integer.parseInt(periodInput);
                            if (library.borrowBook(id, period)) {
                                updateStatus();
                                // Show success message dialog
                                JOptionPane.showMessageDialog(this, "Now you can borrow the book. Return Date: " + bookToBorrow.getReturnDate(), "Success", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("success.jpg"));
                                return; // Exit the method if book is successfully borrowed
                            }
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid borrowing period.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                        }
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid Book ID.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                }
            } else {
                return; // Exit the method if user cancels the input dialog
            }
        }
    }


    private void returnBook() {
        while (true) {
            String input = JOptionPane.showInputDialog(this, "Enter Book ID to Return:");
            if (input != null) {
                try {
                    int id = Integer.parseInt(input);
                    if (id <= 0) {
                        // Show message dialog if ID is zero or negative
                        JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid ID.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                        continue; // Continue to next iteration of the loop
                    }
                    boolean bookFound = false;
                    for (Books book : library.books) {
                        if (book.getId() == id) {
                            bookFound = true;
                            if (!book.getBorrowed()) {
                                // Show message dialog if the book is not currently borrowed
                                JOptionPane.showMessageDialog(this, "This book is not currently borrowed.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                            } else {
                                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to return the book with ID: " + id, "Confirm Return", JOptionPane.YES_NO_OPTION);
                                if (confirm == JOptionPane.YES_OPTION) {
                                    if (library.returnBook(id)) {
                                        updateStatus();
                                        // Show success message dialog
                                        JOptionPane.showMessageDialog(this, "Done, the book has been returned to the library.", "Success", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("success.jpg"));
                                    }
                                }
                            }
                            break;
                        }
                    }
                    if (!bookFound) {
                        // Show message dialog if book with entered ID doesn't exist
                        JOptionPane.showMessageDialog(this, "Book with ID " + id + " doesn't exist.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                    }
                    return; // Exit the loop
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid input! Please enter a valid ID.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                }
            } else {
                return; // Exit the loop if user cancels the input dialog
            }
        }
    }



    private void updateStatus() {
        if (statusRequested) {
            statusTextArea.setText(library.viewLibraryStatus());//put in textarea
            statusRequested = false;
        }
    }

    private void clearStatus() {
        statusTextArea.setText("");
    }


    private void handleWindowClosing() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon("exit-icon.jpg"));
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Close the window
            System.exit(0); // Exit the application
        }
    }


    public static void main(String[] args) {
        int capacity = 0;
        boolean validInput = false;

        while (!validInput) {
            String input = JOptionPane.showInputDialog("Enter the capacity of the library:");
            try {
                capacity = Integer.parseInt(input);
                if (capacity <= 0) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a positive integer number for capacity.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
                } else {
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter an integer number for capacity.", "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon("error.jpg"));
            }
        }

        LibrarySystemGUI librarySystemGUI = new LibrarySystemGUI(capacity);
        librarySystemGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Prevent closing directly
        librarySystemGUI.setVisible(true); // visible the second window

        // Make the window fullscreen
        librarySystemGUI.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

}