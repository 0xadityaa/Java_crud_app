import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ProductManagementApp extends JFrame implements ActionListener {
    private RandomAccessFile dataFile;
    private JTextField txtProductId, txtProductName, txtQuantity, txtUnitPrice;
    private JTextArea txtDisplay;
    private JButton btnFirst, btnPrevious, btnNext, btnLast, btnAdd, btnUpdate, btnDelete;
    private JTable table;

    public ProductManagementApp() {
        try {
            dataFile = new RandomAccessFile("products.dat", "rw");
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Product Management App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.NORTH);

        JLabel lblProductId = new JLabel("Product ID:");
        panel.add(lblProductId);

        txtProductId = new JTextField();
        panel.add(txtProductId);
        txtProductId.setColumns(5);

        JLabel lblProductName = new JLabel("Product Name:");
        panel.add(lblProductName);

        txtProductName = new JTextField();
        panel.add(txtProductName);
        txtProductName.setColumns(10);

        JLabel lblQuantity = new JLabel("Quantity:");
        panel.add(lblQuantity);

        txtQuantity = new JTextField();
        panel.add(txtQuantity);
        txtQuantity.setColumns(5);

        JLabel lblUnitPrice = new JLabel("Unit Price:");
        panel.add(lblUnitPrice);

        txtUnitPrice = new JTextField();
        panel.add(txtUnitPrice);
        txtUnitPrice.setColumns(5);

        btnFirst = new JButton("First");
        panel.add(btnFirst);
        btnFirst.addActionListener(this);

        btnPrevious = new JButton("Previous");
        panel.add(btnPrevious);
        btnPrevious.addActionListener(this);

        btnNext = new JButton("Next");
        panel.add(btnNext);
        btnNext.addActionListener(this);

        btnLast = new JButton("Last");
        panel.add(btnLast);
        btnLast.addActionListener(this);

        btnAdd = new JButton("Add");
        panel.add(btnAdd);
        btnAdd.addActionListener(this);

        btnUpdate = new JButton("Update");
        panel.add(btnUpdate);
        btnUpdate.addActionListener(this);

        btnDelete = new JButton("Delete");
        panel.add(btnDelete);
        btnDelete.addActionListener(this);

        JPanel displayPanel = new JPanel();
        getContentPane().add(displayPanel, BorderLayout.CENTER);

        txtDisplay = new JTextArea();
        displayPanel.add(txtDisplay);
        txtDisplay.setColumns(40);
        txtDisplay.setRows(10);

        JScrollPane scrollPane = new JScrollPane();
        displayPanel.add(scrollPane);

        table = new JTable();
        scrollPane.setViewportView(table);

        // Set table model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Product ID");
        model.addColumn("Product Name");
        model.addColumn("Quantity");
        model.addColumn("Unit Price");
        table.setModel(model);

        displayAllProducts();
    }

    private void displayAllProducts() {
        try {
            txtDisplay.setText("");
            dataFile.seek(0);

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // Reset the table model

            while (dataFile.getFilePointer() < dataFile.length()) {
                int productId = dataFile.readInt();
                String productName = readString(dataFile, 30);
                int quantity = dataFile.readInt();
                double unitPrice = dataFile.readDouble();

                // Add the product to the table model
                model.addRow(new Object[]{productId, productName, quantity, unitPrice});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readAndDisplayProduct() {
        try {
            int productId = dataFile.readInt();
            String productName = readString(dataFile, 30);
            int quantity = dataFile.readInt();
            double unitPrice = dataFile.readDouble();

            txtProductId.setText(String.valueOf(productId));
            txtProductName.setText(productName);
            txtQuantity.setText(String.valueOf(quantity));
            txtUnitPrice.setText(String.valueOf(unitPrice));

            txtDisplay.setText("Product ID: " + productId + "\n" +
                    "Product Name: " + productName + "\n" +
                    "Quantity: " + quantity + "\n" +
                    "Unit Price: " + unitPrice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addOrUpdateProduct(int productId, String productName, int quantity, double unitPrice) {
        try {
            long position = dataFile.length();
            dataFile.seek(position);

            dataFile.writeInt(productId);
            writeString(dataFile, productName, 30);
            dataFile.writeInt(quantity);
            dataFile.writeDouble(unitPrice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeString(RandomAccessFile file, String value, int length) throws IOException {
        StringBuilder sb = new StringBuilder(length);
        sb.append(value);
        sb.setLength(length);
        file.writeChars(sb.toString());
    }

    private void goToFirstProduct() {
        try {
            if (dataFile.length() >= Product.SIZE) {
                dataFile.seek(0);
                readAndDisplayProduct();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToPreviousProduct() {
        try {
            long currentPosition = dataFile.getFilePointer();
            if (currentPosition >= 2 * Product.SIZE) {
                dataFile.seek(currentPosition - 2 * Product.SIZE);
                readAndDisplayProduct();
            } else if (currentPosition >= Product.SIZE) {
                dataFile.seek(0);
                readAndDisplayProduct();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToNextProduct() {
        try {
            long currentPosition = dataFile.getFilePointer();
            if (currentPosition < dataFile.length() - Product.SIZE) {
                readAndDisplayProduct();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToLastProduct() {
        try {
            long lastPosition = dataFile.length();
            if (lastPosition >= Product.SIZE) {
                dataFile.seek(lastPosition - Product.SIZE);
                readAndDisplayProduct();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteProduct() {
        try {
            long currentPosition = dataFile.getFilePointer();
            long lastPosition = dataFile.length();

            if (currentPosition < lastPosition) {
                // Move the remaining products back in the file
                long remainingBytes = lastPosition - currentPosition - Product.SIZE;
                byte[] buffer = new byte[(int) remainingBytes];
                dataFile.read(buffer);

                dataFile.seek(currentPosition - Product.SIZE);
                dataFile.write(buffer);

                // Truncate the file
                dataFile.setLength(lastPosition - Product.SIZE);

                // Go to the next product and display it
                goToNextProduct();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnFirst) {
            goToFirstProduct();
        } else if (e.getSource() == btnPrevious) {
            goToPreviousProduct();
        } else if (e.getSource() == btnNext) {
            goToNextProduct();
        } else if (e.getSource() == btnLast) {
            goToLastProduct();
        } else if (e.getSource() == btnAdd) {
            int productId = Integer.parseInt(txtProductId.getText());
            String productName = txtProductName.getText();
            int quantity = Integer.parseInt(txtQuantity.getText());
            double unitPrice = Double.parseDouble(txtUnitPrice.getText());

            addOrUpdateProduct(productId, productName, quantity, unitPrice);
            displayAllProducts();
        } else if (e.getSource() == btnUpdate) {
            int productId = Integer.parseInt(txtProductId.getText());
            String productName = txtProductName.getText();
            int quantity = Integer.parseInt(txtQuantity.getText());
            double unitPrice = Double.parseDouble(txtUnitPrice.getText());

            addOrUpdateProduct(productId, productName, quantity, unitPrice);
            displayAllProducts();
        } else if (e.getSource() == btnDelete) {
            deleteProduct();
            displayAllProducts();
        }
    }

    private String readString(RandomAccessFile file, int length) throws IOException {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(file.readChar());
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ProductManagementApp frame = new ProductManagementApp();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
