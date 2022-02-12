import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa responsabilă de asigurarea funcționalității ce-i permite Administratorului să modifice produse deja existente în stoc
 *
 * @author Mercescu Ionuț
 */
public class FereastraModificaProdus extends JFrame implements ActionListener {
    private final FereastraAdministrator obiectFereastra;
    List<JButton> listaButoane;
    List<JTextField> listaFielduri;
    List<Produs> listaProduse;
    private JList<Produs> listaCuProduse;
    private JTextField fieldPretProdus, fieldCantitateProdus, fieldNumeProdus;
    private JButton butonModificaProdus, butonInapoi;
    private JLabel labelAvertisment;
    private JPanel panouPrincipal;

    /**
     * Constructorul clasei
     *
     * @param titluFereastra  titlul ferestrei
     * @param obiectFereastra obiectul ferestrei precedente
     */
    public FereastraModificaProdus(String titluFereastra, FereastraAdministrator obiectFereastra) {
        super(titluFereastra);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1000, 500));
        this.setLocationRelativeTo(null);

        this.obiectFereastra = obiectFereastra;
        listaButoane = creareListaSiAdaugareFunctionalitate();
        listaFielduri = creareListaFielduriSiAdaugareDesign();
        labelAvertisment.setVisible(false);
        listaProduse = new ArrayList<>();
        alinierePeMijlocInLista();
        adaugaProduseInJList();

        this.add(panouPrincipal);
        this.setVisible(true);
    }

    public JTextField getFieldPretProdus() {
        return fieldPretProdus;
    }

    public JTextField getFieldCantitateProdus() {
        return fieldCantitateProdus;
    }

    public JTextField getFieldNumeProdus() {
        return fieldNumeProdus;
    }

    public JButton getButonModificaProdus() {
        return butonModificaProdus;
    }

    public JList<Produs> getListaCuProduse() {
        return listaCuProduse;
    }

    public JButton getButonInapoi() {
        return butonInapoi;
    }

    public JLabel getLabelAvertisment() {
        return labelAvertisment;
    }

    /**
     * Metodă ce se ocupă cu alinierea pe orizontală a elementelor din interioul {@link FereastraModificaProdus#listaCuProduse}
     */
    private void alinierePeMijlocInLista() {
        DefaultListCellRenderer cellRenderer = (DefaultListCellRenderer) listaCuProduse.getCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Metodă ce se ocupă de a popula lista cu produse citite din baza de date
     */
    private void adaugaProduseInJList() {
        try {
            ConexiuneDB conexiuneDB = new ConexiuneDB();
            Statement sqlStatement = conexiuneDB.getSQLConnection().createStatement();
            ResultSet resultSet = sqlStatement.executeQuery("SELECT * FROM [dbo].[STOC_PRODUSE_P3]");
            while (resultSet.next())
                listaProduse.add(new Produs(resultSet.getString("ID_PRODUS"), resultSet.getString("NUME_PRODUS"),
                        resultSet.getString("PRET_PRODUS"), resultSet.getString("CANTITATE_PRODUS")));
            DefaultListModel<Produs> defaultListModel = new DefaultListModel<>();
            listaProduse.forEach(defaultListModel::addElement);
            listaCuProduse.setModel(defaultListModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crează o listă cu field-urile prezente în clasă și le adaugă elemente de design
     *
     * @return lista cu field-urile tocmai creată
     */
    private @NotNull List<JTextField> creareListaFielduriSiAdaugareDesign() {
        List<JTextField> listaFielduri = new ArrayList<>(List.of(fieldNumeProdus, fieldPretProdus, fieldCantitateProdus));
        listaFielduri.forEach(fieldCurent -> fieldCurent.setBorder(BorderFactory.createEmptyBorder()));
        return listaFielduri;
    }

    /**
     * Crează o listă cu butoane și fiecărui buton îi adaugă funcționalitate și elemente de design
     *
     * @return lista cu butoane tocmai creată
     */
    private @NotNull List<JButton> creareListaSiAdaugareFunctionalitate() {
        List<JButton> listaButoane = new ArrayList<>(List.of(butonModificaProdus, butonInapoi));
        listaButoane.forEach(butonCurent -> {
            butonCurent.setFocusable(false);
            butonCurent.setCursor(new Cursor(Cursor.HAND_CURSOR));
            butonCurent.addActionListener(this);
        });
        return listaButoane;
    }

    /**
     * În funcție de butonul pe care-l alegem se pot întâmpla următoarele:
     * <pre>
     *     1. Dacă selectăm {@link FereastraModificaProdus#butonModificaProdus} putem modifica numele, cantitatea și prețul unitar al produsului selectat dacă nicio verificare n-a eșuat
     *     2. Dacă selectăm {@link FereastraModificaProdus#butonInapoi} ne întoarcem la fereastra precedentă
     * </pre>
     *
     * @param e
     */
    @Override
    public void actionPerformed(@NotNull ActionEvent e) {
        if (e.getSource().equals(butonModificaProdus)) {
            if (listaCuProduse.getSelectedValuesList().size() == 0)
                afisareMesajAvertisment("Selectează măcar un produs înainte de a încerca să modifici !");
            else if (!verificaMacarUnFieldCompletat())
                afisareMesajAvertisment("Cel puțin un field trebuie să fie selectat!");
            else {
                try {
                    ConexiuneDB conexiuneDB = new ConexiuneDB();
                    Statement sqlStatement = conexiuneDB.getSQLConnection().createStatement();
                    aplicareModificariInDB(sqlStatement);
                    listaProduse.clear();
                    adaugaProduseInJList(); // actualizează produsele din JList
                    golireFielduri();
                    afisareMesaj("Produsul a fost modificat cu succes!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (e.getSource().equals(butonInapoi)) {
            this.dispose();
            obiectFereastra.setVisible(true);
        }
    }

    /**
     * Golește toate field-urile în urma modificării unui produs
     */
    private void golireFielduri() {
        if (!fieldNumeProdus.getText().isEmpty()) fieldNumeProdus.setText(null);
        if (!fieldPretProdus.getText().isEmpty()) fieldPretProdus.setText(null);
        if (!fieldCantitateProdus.getText().isEmpty()) fieldCantitateProdus.setText(null);
    }

    /**
     * Aplică modificări produsului selectat în funcție de ce field-uri sunt completate
     *
     * @param sqlStatement propoziția SQL responsabilă pentru a executa comanda SQL
     * @throws SQLException aruncă excepție în caz că intervine vreo eroare în urma modificării produsului din interiorul bazei de date
     */
    private void aplicareModificariInDB(Statement sqlStatement) throws SQLException {
        if (!fieldNumeProdus.getText().isEmpty())
            sqlStatement.executeUpdate("UPDATE [dbo].[STOC_PRODUSE_P3] SET [NUME_PRODUS] = '" + fieldNumeProdus.getText() + "' WHERE [ID_PRODUS] = " +
                    listaCuProduse.getSelectedValue().getIdProdus());
        if (!fieldPretProdus.getText().isEmpty())
            sqlStatement.executeUpdate("UPDATE [dbo].[STOC_PRODUSE_P3] SET [PRET_PRODUS] = " + fieldPretProdus.getText() + " WHERE [ID_PRODUS] = " +
                    listaCuProduse.getSelectedValue().getIdProdus());
        if (!fieldCantitateProdus.getText().isEmpty())
            sqlStatement.executeUpdate("UPDATE [dbo].[STOC_PRODUSE_P3] SET [CANTITATE_PRODUS] = " + fieldCantitateProdus.getText() + " WHERE [ID_PRODUS] = " +
                    listaCuProduse.getSelectedValue().getIdProdus());
    }

    /**
     * Afișează un mesaj
     *
     * @param continutMesaj conținutul mesajului
     */
    private void afisareMesaj(String continutMesaj) {
        labelAvertisment.setText(continutMesaj);
        if (!labelAvertisment.isVisible()) labelAvertisment.setVisible(true);
    }

    /**
     * Verifică dacă măcar un field este completat
     *
     * @return true dacă măcar un field este completat, false dacă niciunul nu este completat
     */
    private boolean verificaMacarUnFieldCompletat() {
        return listaFielduri.stream().anyMatch(fieldCurent -> !fieldCurent.getText().isEmpty());
    }

    /**
     * Afișează un mesaj de avertisment
     *
     * @param continutMesaj conținutul mesajului de avertisment
     */
    private void afisareMesajAvertisment(String continutMesaj) {
        labelAvertisment.setText(continutMesaj);
        if (!labelAvertisment.isVisible()) labelAvertisment.setVisible(true);
    }
}
