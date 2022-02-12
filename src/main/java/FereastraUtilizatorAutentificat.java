import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa "FereastraUtilizatorAutentificat" din care putem trece în următorele {@link FereastraCosCumparaturi} și {@link FereastraIstoricComenzi}
 *
 * @author Mercescu Ionut
 */
public class FereastraUtilizatorAutentificat extends JFrame implements ActionListener, MouseListener {
    private final FereastraCosCumparaturi fereastraCosCumparaturi;
    private final ButtonGroup grupButoaneRadio;
    private final List<JButton> listaButoane;
    private final List<JRadioButton> listaButoaneRadio;
    private JButton butonAdauga, butonDeconectare, butonIesire, butonAdaugaInCos;
    private JRadioButton butonRadio1000, butonRadio2500, butonRadio5000;
    private JPanel panouPrincipal;
    private JLabel labelBunVenit;
    private JLabel labelSuma;
    private JLabel labelImagineCos;
    private JLabel labelAvertizare;
    private JLabel labelNrProduseInCos;
    private JLabel labelIstoricComenzi;
    private JList<Produs> listaProduse;
    private String valoareDeAdaugat; // folosită pentru a adăuga bani în sold-ul utilizatorului
    private Utilizator utilizatorAutentificat;
    private boolean saModificatSoldulUtilizatorului = false, sauAdaugatProduse = false;
    /**
     * Constructorul clasei
     *
     * @param titluFereastra titlul ferestrei
     * @param dateUtilizator datele despre utilizator primite de la {@link FereastraPrincipala} în urma autentificării
     */
    public FereastraUtilizatorAutentificat(String titluFereastra, String dateUtilizator) {
        super(titluFereastra);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1024, 512));
        this.setLocationRelativeTo(null);

        utilizatorAutentificat = new Utilizator(dateUtilizator);
        fereastraCosCumparaturi = creareFereastraCosDeCumparaturi(); // crează fereastra coșului de cumpărături

        labelImagineCos.setIcon(creareImagine()); // adaugă imaginea pentru coșul de cumpărături
        labelImagineCos.addMouseListener(this);

        adaugaProduseInLista();
        listaProduse.setCursor(new Cursor(Cursor.HAND_CURSOR)); // schimbă cursorul când dăm hover peste lista de produse
        labelIstoricComenzi.setCursor(new Cursor(Cursor.HAND_CURSOR)); // schimbă cursorul când dăm hover peste "istoric cumpărături"
        labelIstoricComenzi.addMouseListener(this);
        labelBunVenit.setText(utilizatorAutentificat.getUsernameUtilizator() + " !");
        labelSuma.setText(utilizatorAutentificat.getSoldUtilizator());

        alinierePeMijlocInLista(); // aliniează produsele din listă pe mijloc

        valoareDeAdaugat = "";
        grupButoaneRadio = new ButtonGroup();
        listaButoaneRadio = creareListaButoaneRadio();


        listaButoane = creareListaButoane();
        modificareButoane(listaButoane, listaButoaneRadio);

        this.add(panouPrincipal);
        this.setVisible(true);
    }

    public JLabel getLabelImagineCos() {
        return labelImagineCos;
    }

    public JLabel getLabelAvertizare() {
        return labelAvertizare;
    }

    public boolean getSaModificatSoldul() {
        return saModificatSoldulUtilizatorului;
    }

    public void setSaModificatSoldul(boolean valoareDeAdevar) {
        this.saModificatSoldulUtilizatorului = valoareDeAdevar;
    }

    public JLabel getLabelNrProduseInCos() {
        return labelNrProduseInCos;
    }

    /**
     * Populează lista {@link FereastraUtilizatorAutentificat#listaProduse} cu produse citite din baza de date
     */
    private void adaugaProduseInLista() {
        try {
            DefaultListModel<Produs> defaultListModel = new DefaultListModel<>();
            ConexiuneDB conexiuneDB = new ConexiuneDB();
            Statement statement = conexiuneDB.getSQLConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT [ID_PRODUS], [NUME_PRODUS], [PRET_PRODUS], [CANTITATE_PRODUS] FROM [dbo].[STOC_PRODUSE_P3]");
            while (resultSet.next()) defaultListModel.addElement(new Produs(creareStringProdus(resultSet)));
            listaProduse.setModel(defaultListModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crează un string pentru fiecare produs în parte
     *
     * @param resultSet resultSet-ul în care avem stocată informația
     * @return String-ul cu produsul tocmai creat
     * @throws SQLException aruncă excepție în caz de apare o eroare în timpul selectării din baza de date
     */
    private @NotNull String creareStringProdus(@NotNull ResultSet resultSet) throws SQLException {
        return resultSet.getString("ID_PRODUS") + ";" + resultSet.getString("NUME_PRODUS") +
                ";" + resultSet.getString("PRET_PRODUS") + ";" + resultSet.getString("CANTITATE_PRODUS");
    }

    public Utilizator getUtilizatorAutentificat() {
        return utilizatorAutentificat;
    }

    public void setUtilizatorAutentificat(Utilizator utilizatorAutentificat) {
        this.utilizatorAutentificat = utilizatorAutentificat;
    }

    /**
     * Crează fereastra de tip {@link FereastraCosCumparaturi} odată cu crearea acestei ferestre
     *
     * @return obiectul coșului de cumpărături
     */
    @NotNull
    private FereastraCosCumparaturi creareFereastraCosDeCumparaturi() {
        final FereastraCosCumparaturi fereastraCosCumparaturi;
        fereastraCosCumparaturi = new FereastraCosCumparaturi("Aplicatie Gestionare Stocuri", this);
        labelAvertizare.setVisible(false);
        fereastraCosCumparaturi.setVisible(false);
        return fereastraCosCumparaturi;
    }

    /**
     * Metodă ce asigură alinierea pe mijloc în {@link FereastraUtilizatorAutentificat#listaProduse}
     */
    private void alinierePeMijlocInLista() {
        DefaultListCellRenderer cellRenderer = (DefaultListCellRenderer) listaProduse.getCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        listaProduse.addMouseListener(this);
    }

    /**
     * Metodă ce crează un ImageIcon dintr-o imagine aflată în interiorul proiectului
     *
     * @return un ImageIcon cu imaginea respectivă
     */
    private @NotNull ImageIcon creareImagine() {
        var imageIcon = new ImageIcon("iconite/cosDeCumparaturi.png");
        Image imagineTemporara = imageIcon.getImage();
        Image nouaImagine = imagineTemporara.getScaledInstance(30, 25, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(nouaImagine);
    }

    /**
     * Aduce modificari butoanelor din interiorul listelor date ca si parametrii
     *
     * @param listaButoane      lista cu butoane din interiorul ferestrei
     * @param listaButoaneRadio lista cu butoane radio din interiorul ferestrei
     */
    private void modificareButoane(@NotNull List<JButton> listaButoane, @NotNull List<JRadioButton> listaButoaneRadio) {
        listaButoane.forEach(butonCurent -> {
            butonCurent.addActionListener(this);
            butonCurent.setBorderPainted(false);
            butonCurent.setFocusable(false);
            butonCurent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        });
        listaButoaneRadio.forEach(butonRadioCurent -> {
            butonRadioCurent.setFocusable(false);
            butonRadioCurent.addActionListener(this);
            grupButoaneRadio.add(butonRadioCurent);
        });
    }

    /**
     * Creaza o lista cu toate butoanele din interiorul ferestrei
     *
     * @return lista cu butoane
     */
    @Contract(" -> new")
    private @NotNull List<JButton> creareListaButoane() {
        return new ArrayList<>(List.of(butonAdauga, butonDeconectare, butonIesire, butonAdaugaInCos));
    }

    /**
     * Creaza o lista cu toate butoanele radio din interiorul ferestrei
     *
     * @return lista cu butoane
     */
    @Contract(" -> new")
    private @NotNull List<JRadioButton> creareListaButoaneRadio() {
        return new ArrayList<>(List.of(butonRadio1000, butonRadio2500, butonRadio5000));
    }

    /**
     * In functie de butoanele pe care le selectam se intampla urmatoarele modificari:
     * daca selectam "butonRadio1000" valoarea care va urma sa fie adaugata la soldul curent este de 50
     * daca selectam "butonRadio2500" valoarea care va urma sa fie adaugata la soldul curent este de 100
     * daca selectam "butonRadio5000" valoarea care va urma sa fie adaugata la soldul curent este de 150
     * daca selectam "butonAdauga" preluam valoarea din "valoareDeAdaugat" si o adunam cu valoarea curenta din soldul nostru
     * daca selectam "butonDeconectare" preluam soldul curent al utilizatorului si-l updatam in fisier ( pentru a tine minte la o viitoare conectare cu cat sold a ramas )
     * daca selectam "butonIesire" ne iasa din aplicatie fara a aduce modificari aditionale
     *
     * @param e eventul pe care-l comparam
     */
    @Override
    public void actionPerformed(@NotNull ActionEvent e) {
        if (e.getSource().equals(butonRadio1000)) valoareDeAdaugat = butonRadio1000.getText();
        if (e.getSource().equals(butonRadio2500)) valoareDeAdaugat = butonRadio2500.getText();
        if (e.getSource().equals(butonRadio5000)) valoareDeAdaugat = butonRadio5000.getText();
        if (e.getSource().equals(butonAdauga)) {
            boolean celPutinUnButonSelectat = listaButoaneRadio.stream().anyMatch(AbstractButton::isSelected);
            if (celPutinUnButonSelectat) actualizareLabelSold();
            else
                afisareMesajAvertizare("Trebuie să selectezi suma pe care vrei să o adaugi la sold înainte de a apăsa pe buton!");
        }

        // când alegem "butonDeconectare" se vor salva toate informațiile ( soldul, colul de cumpărături, etc )
        if (e.getSource().equals(butonDeconectare)) {
            ConexiuneDB conexiuneDB;
            // actualizăm soldul utilizatoruui DOAR ÎN CAZUL ÎN CARE S-AU ADUS MODIFICĂRI ASUPRA SA, atfel n-are rost
            if (saModificatSoldulUtilizatorului) {
                try {
                    conexiuneDB = new ConexiuneDB();
                    Statement statement = conexiuneDB.getSQLConnection().createStatement();
                    statement.executeUpdate("UPDATE [dbo].[UTILIZATORI_P3] SET [SOLD_UTILIZATOR] = " + labelSuma.getText() + " WHERE [ID_UTILIZATOR] = "
                            + utilizatorAutentificat.getIdUtilizator() + ";");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            // dacă s-au adăugat produse noi în coșul de cumpărături
            if (sauAdaugatProduse) {
                try {
                    conexiuneDB = new ConexiuneDB();
                    Statement sqlStatement1 = conexiuneDB.getSQLConnection().createStatement();
                    Statement sqlStatement2 = conexiuneDB.getSQLConnection().createStatement();
                    ResultSet resultSet = sqlStatement2.executeQuery("SELECT [ID_PRODUS] FROM [dbo].[COS_CUMPARATURI_P3] WHERE [ID_UTILIZATOR] = " +
                            utilizatorAutentificat.getIdUtilizator());
                    for (Produs produsCurent : fereastraCosCumparaturi.getListaProduseComandate())
                        if (!resultSet.next()) // parcurgem tabelul până când ajungem la ultima poziție unde adăugăm produsul nou adăugat
                            sqlStatement1.executeUpdate("INSERT INTO [dbo].[COS_CUMPARATURI_P3] ([ID_UTILIZATOR] ,[ID_PRODUS]) VALUES ("
                                    + utilizatorAutentificat.getIdUtilizator() + ", " + produsCurent.getIdProdus() + ")");
                } catch (SQLException ex) {
                    afisareMesajAvertizare("Produsul selectat se afla deja in cosul de cumparaturi!");
                }
            }
            fereastraCosCumparaturi.dispose();
            this.dispose();
            FereastraPrincipala fereastraPrincipala = new FereastraPrincipala("Aplicatie Gestionare Stocuri");
        }
        if (e.getSource().equals(butonIesire)) {
            fereastraCosCumparaturi.dispose();
            this.dispose();
        }
        if (e.getSource().equals(butonAdaugaInCos)) {
            if (listaProduse.getSelectedValuesList().size() == 0)
                afisareMesajAvertizare("Selecteaza produse inainte de a le adauga in cosul de cumparaturi!");
            else {
                adaugaProduseInCos(); // adaugă DOAR PRODUSE NOI ÎN COȘ
                int dimensiuneCos = fereastraCosCumparaturi.getListaProduseComandate().size(); // preluăm dimensiunea noului coș
                labelNrProduseInCos.setText(String.valueOf(dimensiuneCos)); // actualizăm dimensiunea
                fereastraCosCumparaturi.actualizeazaProduseInCos(); // funcție ce adaugă efectiv produsele în lista din interiorul coșului de cumpărături
            }
        }
    }

    private void adaugaProduseInCos() {
        boolean nuSeAflaInLista = true;
        for (Produs produsPentruAdaugat : listaProduse.getSelectedValuesList()) {
            nuSeAflaInLista = true; // începem cu premiza că produsul pe care vrem să-l adăugăm se află-n listă
            for (Produs produsCurent : fereastraCosCumparaturi.getListaProduseComandate()) // parcurgem lista de produse aflate-n coș
                // dacă găsim un produs cu același ID ca al produsului pe care vrem să-l inserăm atunci nu adăugăm efectiv
                if (produsPentruAdaugat.getIdProdus().equals(produsCurent.getIdProdus()))
                    nuSeAflaInLista = false;
            // dacă nu s-a găsit niciun alt produs cu același ID adăugăm produsul curent în coșul de cumpărături
            if (nuSeAflaInLista) {
                fereastraCosCumparaturi.getListaProduseComandate().add(produsPentruAdaugat);
                sauAdaugatProduse = true;
            }
        }
        if (!nuSeAflaInLista) afisareMesajAvertizare("Produsul se afla deja in cosul de cumparaturi!");
        if (nuSeAflaInLista && labelAvertizare.isVisible()) labelAvertizare.setVisible(false);
    }

    /**
     * Actualizează soldul în urma adăugării sumei de bani
     */
    private void actualizareLabelSold() {
        labelSuma.setText(String.valueOf(Integer.parseInt(labelSuma.getText()) + Integer.parseInt(valoareDeAdaugat)));
        utilizatorAutentificat.setSoldUtilizator(labelSuma.getText());
        saModificatSoldulUtilizatorului = true;
        if (labelAvertizare.isVisible()) labelAvertizare.setVisible(false);
    }

    /**
     * Afișează un mesaj de avertizare
     *
     * @param continutMesaj conținutul mesajului de avertizare
     */
    private void afisareMesajAvertizare(String continutMesaj) {
        labelAvertizare.setText(continutMesaj);
        if (!labelAvertizare.isVisible()) labelAvertizare.setVisible(true);
    }

    private boolean verificaButoaneSelectate() {
        return listaButoaneRadio.stream().anyMatch(AbstractButton::isSelected);
    }

    public List<JButton> getListaButoane() {
        return listaButoane;
    }

    public List<JRadioButton> getListaButoaneRadio() {
        return listaButoaneRadio;
    }

    public JButton getButonAdauga() {
        return butonAdauga;
    }

    public JButton getButonDeconectare() {
        return butonDeconectare;
    }

    public JButton getButonIesire() {
        return butonIesire;
    }

    public JRadioButton getbutonRadio1000() {
        return butonRadio1000;
    }

    public JRadioButton getButonRadio2500() {
        return butonRadio2500;
    }

    public JRadioButton getButonRadio5000() {
        return butonRadio5000;
    }

    public JLabel getLabelSuma() {
        return labelSuma;
    }

    /**
     * Evenimente de mouse ce va seta fereastra coșului de cumpărături ca fiind vizibilă atunci când apăsăm pe iconița coșului de cumărături sau fereastra istoricului dacă apăsăm pe istoric comenzi
     *
     * @param e
     */
    @Override
    public void mouseClicked(@NotNull MouseEvent e) {
        if (e.getSource().equals(labelImagineCos)) {
            fereastraCosCumparaturi.setVisible(true);
        }
        if (e.getSource().equals(labelIstoricComenzi)) {
            this.setVisible(false);
            FereastraIstoricComenzi fereastraIstoricComenzi = new FereastraIstoricComenzi("Aplicatie Gestionare Stocuri", utilizatorAutentificat.getUsernameUtilizator(),
                    utilizatorAutentificat.getIdUtilizator(), this);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(@NotNull MouseEvent e) {
        if (e.getSource().equals(labelImagineCos))
            labelImagineCos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
