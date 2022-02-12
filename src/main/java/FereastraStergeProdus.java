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
 * Clasa responsabilă cu asigurarea funcționalității ștergerii unui produs din interiorul baze de date de către Administrator
 *
 * @author Mercescu Ionut
 */
public class FereastraStergeProdus extends JFrame implements ActionListener {

    private final FereastraAdministrator obiectFereastra;
    private final List<JButton> listaButoane;
    private final List<Produs> listaProduse;
    private JPanel panouPrincipal;
    private JList<Produs> listaCuProduse;
    private JButton butonStergeProdus, butonInapoi;
    private JLabel labelAvertisment;

    /**
     * Constructorul clasei
     *
     * @param titluFereastra  titlul ferestrei
     * @param obiectFereastra obiectul ferestrei precedente
     */
    public FereastraStergeProdus(String titluFereastra, FereastraAdministrator obiectFereastra) {
        super(titluFereastra);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1000, 500));
        this.setLocationRelativeTo(null);

        this.obiectFereastra = obiectFereastra;
        listaButoane = creareListaSiAdaugareFunctionalitate();
        labelAvertisment.setVisible(false);
        listaProduse = new ArrayList<>();
        alinierePeMijlocInLista();
        adaugaProduseInJList();

        this.add(panouPrincipal);
        this.setVisible(true);
    }

    public List<JButton> getListaButoane() {
        return listaButoane;
    }

    /**
     * Metodă ce asigură alinierea pe mijloc a produselor în {@link FereastraStergeProdus#listaCuProduse}
     */
    private void alinierePeMijlocInLista() {
        DefaultListCellRenderer cellRenderer = (DefaultListCellRenderer) listaCuProduse.getCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Metodă ce populează {@link FereastraStergeProdus#listaCuProduse} cu produse citite din baza de date
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
     * Crează o listă cu butoane și adaugă funcționalitate acestora și elemente de design
     *
     * @return lista cu butoanele tocmai creată
     */
    private @NotNull List<JButton> creareListaSiAdaugareFunctionalitate() {
        List<JButton> listaButoane = new ArrayList<>(List.of(butonStergeProdus, butonInapoi));
        listaButoane.forEach(butonCurent -> {
            butonCurent.setFocusable(false);
            butonCurent.setCursor(new Cursor(Cursor.HAND_CURSOR));
            butonCurent.addActionListener(this);
        });
        return listaButoane;
    }

    /**
     * Dacă selectăm {@link FereastraStergeProdus#butonStergeProdus} și nicio verificare n-a eșuat produsul va fi șters din stocul de produse
     * Dacă selectăm {@link FereastraStergeProdus#butonInapoi} ne vom întoarce la fereastra precedentă
     *
     * @param e
     */
    @Override
    public void actionPerformed(@NotNull ActionEvent e) {
        if (e.getSource().equals(butonStergeProdus)) {
            if (listaCuProduse.getSelectedValuesList().size() == 0)
                afisareMesaj("Selectează un produs înainte de a șterge!");
            else {
                try {
                    ConexiuneDB conexiuneDB = new ConexiuneDB();
                    Statement sqlStatement = conexiuneDB.getSQLConnection().createStatement();
                    sqlStatement.executeUpdate("DELETE FROM [dbo].[STOC_PRODUSE_P3] WHERE [ID_PRODUS] = " + listaCuProduse.getSelectedValue().getIdProdus());
                    listaProduse.clear();
                    adaugaProduseInJList();
                    afisareMesaj("Produsul a fost sters cu succes din stocul magazinului!");
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

    public JList<Produs> getListaCuProduse() {
        return listaCuProduse;
    }

    public JButton getButonStergeProdus() {
        return butonStergeProdus;
    }

    public JButton getButonInapoi() {
        return butonInapoi;
    }

    public JLabel getLabelAvertisment() {
        return labelAvertisment;
    }

    /**
     * Afișeză un mesaj
     *
     * @param continutMesaj conținutul mesajului
     */
    private void afisareMesaj(String continutMesaj) {
        labelAvertisment.setText(continutMesaj);
        if (!labelAvertisment.isVisible()) labelAvertisment.setVisible(true);
    }


}
