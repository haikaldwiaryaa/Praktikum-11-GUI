import javax.swing.*;
import java.awt.*;

public class ComponentDemo extends JFrame {
    public ComponentDemo() {
        setTitle("Swing Component Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField nameField = new JTextField(15);

        JComboBox<String> deptCombo = new JComboBox<>(
                new String[] {"Informatika", "Elektronika", "Mesin"}
        );

        JCheckBox activeCheck = new JCheckBox("Mahasiswa aktif", true);

        JRadioButton regulerRadio = new JRadioButton("Reguler", true);
        JRadioButton ekstensiRadio = new JRadioButton("Ekstensi");
        ButtonGroup group = new ButtonGroup();
        group.add(regulerRadio);
        group.add(ekstensiRadio);

        JTextArea outputArea = new JTextArea(6, 25);
        outputArea.setEditable(false);

        JButton showButton = new JButton("Tampilkan");

        JPanel kelasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        kelasPanel.add(regulerRadio);
        kelasPanel.add(ekstensiRadio);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Nama:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Jurusan:"));
        formPanel.add(deptCombo);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(activeCheck);
        formPanel.add(new JLabel("Kelas:"));
        formPanel.add(kelasPanel);

        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.add(showButton);

        setLayout(new BorderLayout(5, 5));
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        showButton.addActionListener(e -> {
            if (nameField.getText().isBlank()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Nama wajib diisi.",
                        "Input tidak valid",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String nama = nameField.getText();
            String jurusan = (String) deptCombo.getSelectedItem();
            boolean aktif = activeCheck.isSelected();
            String kelas = regulerRadio.isSelected()
                    ? "Reguler"
                    : "Ekstensi";

            outputArea.setText(
                    "Nama: " + nama +
                    "\nJurusan: " + jurusan +
                    "\nAktif: " + aktif +
                    "\nKelas: " + kelas
            );
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ComponentDemo::new);
    }
    
}