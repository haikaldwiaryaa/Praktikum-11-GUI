import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

public class POSFrame extends JFrame {
    private final List<Product> products = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();

    private JTable productTable;
    private JTable cartTable;
    private JTextField qtyField;
    private JLabel selectedLabel;
    private JLabel totalLabel;
    private JLabel pointsLabel;
    private JTextArea receiptArea;
    private JButton addButton;
    private JButton checkoutButton;
    private JButton printButton;

    private DefaultTableModel productModel;
    private DefaultTableModel cartModel;

    public POSFrame() {
        setTitle("POIN Off-Sales - Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initProductData();
        initComponents();
        bindEvents();

        setSize(1000, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initProductData() {
        products.add(new Product("P001", "Air Mineral 600ml", 3000));
        products.add(new Product("P002", "Kopi Sachet", 5000));
        products.add(new Product("P003", "Roti isi", 8000));
        products.add(new Product("P004", "Snack Keripik", 6000));
        products.add(new Product("P005", "Minuman Botol", 12000));
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        exitItem.addActionListener(e -> dispose());
        aboutItem.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "POIN Off-Sales - Praktikum Java Swing"
                )
        );

        fileMenu.add(exitItem);
        helpMenu.add(aboutItem);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        productModel = new DefaultTableModel(
                new Object[] {"ID", "Nama Produk", "Harga (Rp)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Product p : products) {
            productModel.addRow(new Object[] {
                    p.getId(), p.getName(), p.getPrice()
            });
        }

        productTable = new JTable(productModel);
        selectedLabel = new JLabel("Pilih produk dari tabel di kiri");
        qtyField = new JTextField("1", 5);
        addButton = new JButton("Add to Cart");

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        qtyPanel.add(new JLabel("Qty:"));
        qtyPanel.add(qtyField);
        qtyPanel.add(addButton);

        JPanel productBottomPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        productBottomPanel.add(selectedLabel);
        productBottomPanel.add(qtyPanel);

        JPanel productPanel = new JPanel(new BorderLayout(5, 5));
        productPanel.setBorder(BorderFactory.createTitledBorder("Produk"));
        productPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        productPanel.add(productBottomPanel, BorderLayout.SOUTH);

        cartModel = new DefaultTableModel(
                new Object[] {"ID", "Nama Produk", "Qty", "Harga", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartModel);
        totalLabel = new JLabel("Total: Rp0");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        pointsLabel = new JLabel("Points: 0");
        checkoutButton = new JButton("Checkout");
        printButton = new JButton("Cetak");

        JPanel checkoutRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkoutRow.add(checkoutButton);

        JPanel printRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        printRow.add(printButton);

        JPanel summaryPanel = new JPanel(new GridLayout(4, 1, 0, 4));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        summaryPanel.add(totalLabel);
        summaryPanel.add(pointsLabel);
        summaryPanel.add(checkoutRow);
        summaryPanel.add(printRow);

        receiptArea = new JTextArea(8, 30);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JPanel receiptPanel = new JPanel(new BorderLayout(4, 4));
        receiptPanel.add(new JLabel("Struk:"), BorderLayout.NORTH);
        receiptPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);

        JPanel cartBottomPanel = new JPanel(new BorderLayout(5, 5));
        cartBottomPanel.add(summaryPanel, BorderLayout.NORTH);
        cartBottomPanel.add(receiptPanel, BorderLayout.CENTER);

        JPanel cartPanel = new JPanel(new BorderLayout(5, 5));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Keranjang"));
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        cartPanel.add(cartBottomPanel, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        mainPanel.add(productPanel);
        mainPanel.add(cartPanel);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private void bindEvents() {
        productTable.getSelectionModel().addListSelectionListener(e -> {
            int row = productTable.getSelectedRow();

            if (row >= 0) {
                Product selected = products.get(row);
                selectedLabel.setText(
                        "Dipilih: " + selected.getId() +
                        " - " + selected.getName() +
                        " (Rp" + selected.getPrice() + ")"
                );
            }
        });
        addButton.addActionListener(
            e -> addSelectedProductToCart()
        );
        checkoutButton.addActionListener(
                e -> checkout()
        );
        printButton.addActionListener(
                e -> printReceipt()
        );
    }
        private void addSelectedProductToCart() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih produk terlebih dahulu.",
                    "Input tidak valid",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String qtyText = qtyField.getText();
        if (qtyText.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Qty wajib diisi.",
                    "Input tidak valid",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            int qty = Integer.parseInt(qtyText.trim());
            Product selected = products.get(row);
            CartItem item = new CartItem(selected, qty);
            cart.add(item);
            refreshCartTable();
            updateSummary();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Qty harus berupa angka bulat.",
                    "Input tidak valid",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Input tidak valid",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void refreshCartTable() {
        cartModel.setRowCount(0);
        for (CartItem item : cart) {
            Product p = item.getProduct();
            cartModel.addRow(new Object[] {
                    p.getId(),
                    p.getName(),
                    item.getQty(),
                    p.getPrice(),
                    item.getSubtotal()
            });
        }
    }

    private void updateSummary() {
        int total = 0;
        for (CartItem item : cart) {
            total += item.getSubtotal();
        }
        int points = total / 1000;
        totalLabel.setText("Total: Rp" + total);
        pointsLabel.setText("Points: " + points);
    }
    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Keranjang masih kosong.",
                    "Checkout gagal",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int total = 0;
        StringBuilder receipt = new StringBuilder();

        receipt.append("===== POIN OFF-SALES =====\n");
        receipt.append("Toko: Demo Toko\n");
        receipt.append("====================================\n");
        receipt.append(String.format("%-4s %-15s %3s %10s\n", "ID", "Nama", "Qty", "Subtotal"));
        receipt.append("------------------------------------\n");

        for (CartItem item : cart) {
            Product p = item.getProduct();
            int subtotal = item.getSubtotal();
            total += subtotal;
            receipt.append(String.format(
                    "%-4s %-15s %3d %10s\n",
                    p.getId(),
                    p.getName(),
                    item.getQty(),
                    "Rp" + subtotal
            ));
        }

        int points = total / 1000;
        receipt.append("------------------------------------\n");
        receipt.append(String.format("%-20s %14s\n", "TOTAL:", "Rp" + total));
        receipt.append("POINTS DIDAPAT: " + points + " (1 point per Rp 1000)\n");
        receipt.append("====================================\n");
        receipt.append("Terima kasih! Silakan kunjungi kembali.\n");

        receiptArea.setText(receipt.toString());

        cart.clear();
        refreshCartTable();
        updateSummary();
    }

    private void printReceipt() {
        if (receiptArea.getText().isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Belum ada struk yang dapat dicetak."
            );
            return;
        }

        try {
            receiptArea.print();
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal mencetak: " + ex.getMessage()
            );
        }
    }   
}