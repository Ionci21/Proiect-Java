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
 * Fereastra "FereastraAdaugaProdus" în care asigurăm funcționalitatea adăugării unor noi produse de către Administrator în baza de date
 *
 * @author Mercescu Ionuț
 */
public class FereastraAdaugaProdus extends JFrame implements ActionListener {

    private final FereastraAdministrator obiectFereastra;
    private final List<JButton> listaButoane;
    private final List<JTextField> listaFielduri;
    private JPanel panouPrincipal;
    private JButton butonAdaugaProdus, butonInapoi;
    private JTextField fieldNumeProdus, fieldCantitate, fieldPretUnitar;
    private JLabel labelAvertisment;
    private JList<Produs> listaCuProduse;
    private List<Produs> listaProduse;

    /**
     * Constructor-ul clasei "FereastraAdaugaProdus"
     *
     * @param titluFereastra  titlul ferestrei
     * @param obiectFereastra un obiect de tipul clasei {@link FereastraAdministrator} folosit pentru a ne întoarce înapoi la fereastra precedentă
     */
    public FereastraAdaugaProdus(String titluFereastra, FereastraAdministrator obiectFereastra) {
        super(titluFereastra);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1000, 500));
        this.setLocationRelativeTo(null);

        this.obiectFereastra = obiectFereastra;
        listaButoane = creareListaSiAdaugareFunctionalitate();
        listaFielduri = creareListaFielduriSiAdaugareFunctionalitate();
        labelAvertisment.setVisible(false);
        listaProduse = new ArrayList<>();
        alinierePeMijlocInLista();
        adaugaProduseInJList();

        this.add(panouPrincipal);
        this.setVisible(true);
    }

    /**
     * Funcție ce se ocupă de a seta aliniamentul orizontal al {@link FereastraAdaugaProdus#listaCuProduse}
     */
    private void alinierePeMijlocInLista() {
        DefaultListCellRenderer cellRenderer = (DefaultListCellRenderer) listaCuProduse.getCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Această funcție se ocupă de a popula JListul ( {@link FereastraAdaugaProdus#listaCuProduse} ) cu produse preluate din baza de date
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
     * Funcție ce adaugă design field-urilor prezente în această clasă, returnează o listă cu field-urile
     *
     * @return listă cu fielduri
     */
    private @NotNull List<JTextField> creareListaFielduriSiAdaugareFunctionalitate() {
        List<JTextField> listaFielduri = new ArrayList<>(List.of(fieldNumeProdus, fieldPretUnitar, fieldCantitate));
        listaFielduri.forEach(fieldCurent -> fieldCurent.setBorder(BorderFactory.createEmptyBorder()));
        return listaFielduri;
    }

    /**
     * Crează o listă cu butoanele prezente în clasă și adaugă funcționalitate și design acestora
     *
     * @return o listă cu butoane
     */
    private @NotNull List<JButton> creareListaSiAdaugareFunctionalitate() {
        List<JButton> listaButoane = new ArrayList<>(List.of(butonAdaugaProdus, butonInapoi));
        listaButoane.forEach(butonCurent -> {
            butonCurent.setFocusable(false);
            butonCurent.setCursor(new Cursor(Cursor.HAND_CURSOR));
            butonCurent.addActionListener(this);
        });
        return listaButoane;
    }

    /**
     * Metoda supraîncărcată "actionPerformed(...)" ce se ocupă cu funcționalitatea clasei, în funcție de butoanele selectate se pot întâmpla următoarele:
     * <pre>
     *     1. Dacă selectăm butonul {@link FereastraAdaugaProdus#butonAdaugaProdus} vom adăuga în baza de date un nou produs după ce toate verificările sunt trecute cu succes
     *     2. Dacă selectăm butonul {@link FereastraAdaugaProdus#butonInapoi} ne vom întoarce la fereastra precedentă
     * </pre>
     *
     * @param e
     */
    @Override
    public void actionPerformed(@NotNull ActionEvent e) {
        if (e.getSource().equals(butonAdaugaProdus)) {
            if (verificaFielduri()) {
                try {
                    ConexiuneDB conexiuneDB = new ConexiuneDB();
                    Statement sqlStatement = conexiuneDB.getSQLConnection().createStatement();
                    sqlStatement.executeUpdate("INSERT INTO [dbo].[STOC_PRODUSE_P3] ([NUME_PRODUS], [PRET_PRODUS], [CANTITATE_PRODUS]) " + "VALUES ('"
                            + fieldNumeProdus.getText() + "', " + fieldPretUnitar.getText() + ", " + fieldCantitate.getText() + ")");
                    afisareMesaj("Produsul \"" + fieldNumeProdus.getText() + "\" a fost adaugat cu succes in stocul de produse!");
                    listaProduse.clear();
                    adaugaProduseInJList();
                    listaFielduri.forEach(fieldCurent -> fieldCurent.setText(null));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else afisareMesajAvertizare();
        }
        if (e.getSource().equals(butonInapoi)) {
            this.dispose();
            obiectFereastra.setVisible(true);
        }
    }

    /**
     * Metoda folosită pentru a afișa în {@link FereastraAdaugaProdus#labelAvertisment} un mesaj
     *
     * @param continutMesaj conținutul mesajului
     */
    private void afisareMesaj(String continutMesaj) {
        labelAvertisment.setText(continutMesaj);
        if (!labelAvertisment.isVisible()) labelAvertisment.setVisible(true);
    }

    /**
     * Metoda folosită pentru a afișa în {@link FereastraAdaugaProdus#labelAvertisment} un mesaj de avertizare
     */
    private void afisareMesajAvertizare() {
        labelAvertisment.setText("Completează toate câmpurile înainte de a încerca să adaugi un produs!");
        if (!labelAvertisment.isVisible()) labelAvertisment.setVisible(true);
    }

    /**
     * Verifică dacă niciun field nu este gol
     *
     * @return true sau false
     */
    private boolean verificaFielduri() {
        return listaFielduri.stream().noneMatch(fieldCurent -> fieldCurent.getText().isEmpty());
    }

    /**
     * Getter-ul listei {@link FereastraAdaugaProdus#listaButoane}
     *
     * @return returnează lista de butoane
     */
    public List<JButton> getListaButoane() {
        return listaButoane;
    }

    public JButton getButonAdaugaProdus() {
        return butonAdaugaProdus;
    }

    public JButton getButonInapoi() {
        return butonInapoi;
    }

    public JTextField getFieldNumeProdus() {
        return fieldNumeProdus;
    }

    public JTextField getFieldCantitate() {
        return fieldCantitate;
    }

    public JTextField getFieldPretUnitar() {
        return fieldPretUnitar;
    }

    public JLabel getLabelAvertisment() {
        return labelAvertisment;
    }
}

