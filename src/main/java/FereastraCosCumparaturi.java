import org.jetbrains.annotations.Contract;
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

public class FereastraCosCumparaturi extends JFrame implements ActionListener {
    private final List<JButton> listaButoane;
    private final List<Produs> listaProduseComandate;
    private final FereastraUtilizatorAutentificat obiectFereastra;
    private JButton butonPlaseazaComanda;
    private JButton butonAnulare;
    private JButton butonStergeProdus;
    private JLabel labelSumaProduse;
    private JLabel labelInformativ;
    private JLabel labelPrimaParte;
    private JLabel labelADouaParte;
    private JLabel labelAvertizare;
    private JPanel panouPrincipal;
    private JList<Produs> JListProduseComandate;

    public FereastraCosCumparaturi(String titluFereastra, FereastraUtilizatorAutentificat obiectFereastra) {
        super(titluFereastra);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1400, 580));
        this.setLocationRelativeTo(null);

        listaProduseComandate = new ArrayList<>();
        modificariEsteticeJList();
        listaButoane = creareListaSiAdaugareFunctionalitate(); // crează lista de butoane și aduce fiecărui buton unele modificări și funcționalitate
        this.obiectFereastra = obiectFereastra;
        adaugaProduseInListaSiAfiseaza(); // preia produsele din baza de date și le adaugă în List și JList
        labelAvertizare.setVisible(false);

        this.add(panouPrincipal);
        this.setVisible(true);
    }

    public JLabel getLabelAvertizare() {
        return labelAvertizare;
    }

    public JButton getButonPlaseazaComanda() {
        return butonPlaseazaComanda;
    }

    public JButton getButonAnulare() {
        return butonAnulare;
    }

    public JButton getButonStergeProdus() {
        return butonStergeProdus;
    }

    private void modificariEsteticeJList() {
        JListProduseComandate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        var defaultListCellRenderer = (DefaultListCellRenderer) JListProduseComandate.getCellRenderer();
        defaultListCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void adaugaProduseInListaSiAfiseaza() {
        try {
            ConexiuneDB conexiuneDB = new ConexiuneDB();
            Statement statement = conexiuneDB.getSQLConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(creareStringSelect());
            if (!resultSet.next()) {
                afisareCosCumparaturiGol(); // dacă nu avem produse în coșul de afișăm corespunzător situației
                labelADouaParte.setVisible(false);
            }
            else {
                listaProduseComandate.add(creareProdus(resultSet)); // creăm produsul și-l adăugăm în listă
                while (resultSet.next())
                    listaProduseComandate.add(creareProdus(resultSet)); // și tot adăugăm până când nu mai avem produse de adăugat
                actualizeazaProduseInCos(); // mai apoi actualizăm produsele în listă ( și le și afișăm )
                actualizareSoldProduse(); // și actualizăm și soldul
                this.obiectFereastra.getLabelNrProduseInCos().setText(String.valueOf(listaProduseComandate.size()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Contract("_ -> new")
    @NotNull
    private Produs creareProdus(@NotNull ResultSet resultSet) throws SQLException {
        return new Produs(resultSet.getString("ID_PRODUS"), resultSet.getString("NUME_PRODUS"),
                resultSet.getString("PRET_PRODUS"), resultSet.getString("CANTITATE_PRODUS"));
    }

    @Contract(pure = true)
    private @NotNull String creareStringSelect() {
        // ne returneaza selectul folosit pentru a "uni" fiecare Utilizator cu Produsele sale din Coșul de Cumpărături
        return "SELECT [T1].[ID_UTILIZATOR], [T2].[ID_PRODUS], [T2].[NUME_PRODUS], [T2].[PRET_PRODUS], [T2].[CANTITATE_PRODUS] " +
                "FROM [dbo].[COS_CUMPARATURI_P3] T1 JOIN [dbo].[STOC_PRODUSE_P3] T2 ON [T1].[ID_PRODUS] = [T2].[ID_PRODUS] " +
                "WHERE [T1].[ID_UTILIZATOR] = " + obiectFereastra.getUtilizatorAutentificat().getIdUtilizator();
    }

    // dacă coșul este gol vom afișa astfel
    private void afisareCosCumparaturiGol() {
        labelInformativ.setText("Momentan nu ai niciun produs în coșul de cumpărături!");
        afisareLabele(labelInformativ, labelPrimaParte, labelSumaProduse, false);
    }

    // după fiecare modificare adusă vom apela funcția aceasta pentru a actualiza produsele din JList
    void actualizeazaProduseInCos() {
        DefaultListModel<Produs> defaultListModel = new DefaultListModel<>();
        listaProduseComandate.forEach(defaultListModel::addElement);
        JListProduseComandate.setModel(defaultListModel);
        labelInformativ.setText("Produsele din stoc sunt :");
        afisareLabele(labelPrimaParte, labelSumaProduse, labelADouaParte, true);
        actualizareSoldProduse();
    }

    private void afisareLabele(@NotNull JLabel labelPrimaParte, @NotNull JLabel labelSumaProduse, @NotNull JLabel labelADouaParte, boolean aFlag) {
        labelPrimaParte.setVisible(true);
        labelSumaProduse.setVisible(aFlag);
        labelADouaParte.setVisible(aFlag);
    }

    void actualizareSoldProduse() {
        if (listaProduseComandate.size() > 0) {
            int sumaProduseDinCos = listaProduseComandate.stream()
                    .mapToInt(produsCurent -> Integer.parseInt(produsCurent.getPretProdus()))
                    .sum();
            labelSumaProduse.setText(String.valueOf(sumaProduseDinCos));
        }
    }

    public List<Produs> getListaProduseComandate() {
        return listaProduseComandate;
    }

    private @NotNull List<JButton> creareListaSiAdaugareFunctionalitate() {
        List<JButton> listaButoane = new ArrayList<>(List.of(butonPlaseazaComanda, butonAnulare, butonStergeProdus));
        listaButoane.forEach(butonCurent -> {
            butonCurent.addActionListener(this);
            butonCurent.setFocusable(false);
            butonCurent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        });
        return listaButoane;
    }

    @Override
    public void actionPerformed(@NotNull ActionEvent e) {
        if (listaProduseComandate.size() > 0) {
            if (e.getSource().equals(butonPlaseazaComanda)) {
                if (!(Integer.parseInt(obiectFereastra.getUtilizatorAutentificat().getSoldUtilizator()) >= Integer.parseInt(labelSumaProduse.getText())))
                    afisareMesajAvertizare("Nu poți plasa comanda deoarece momentan nu ai bani suficienți, soldul tău : " + obiectFereastra.getUtilizatorAutentificat().getSoldUtilizator());
                else {
                    StringBuilder stringBuilder = creareStringComanda();
                    try {
                        ConexiuneDB conexiuneDB = new ConexiuneDB();
                        Statement sqlStatement = conexiuneDB.getSQLConnection().createStatement();
                        sqlStatement.executeUpdate("INSERT INTO [dbo].[ISTORIC_COMENZI_P3] ([ID_UTILIZATOR], [MESAJ_COMANDA]) VALUES ("
                                + obiectFereastra.getUtilizatorAutentificat().getIdUtilizator() + ", '" + stringBuilder + "')");
                        listaProduseComandate.forEach(produsCurent -> {
                            try {
                                if (Integer.parseInt(produsCurent.getCantitateProdus()) - 1 >= 0)
                                    sqlStatement.executeUpdate("UPDATE [STOC_PRODUSE_P3] SET [CANTITATE_PRODUS] = (SELECT [CANTITATE_PRODUS] FROM [dbo].[STOC_PRODUSE_P3] WHERE [ID_PRODUS] = "
                                            + produsCurent.getIdProdus() + ") - 1 WHERE [ID_PRODUS] = " + produsCurent.getIdProdus());
                                sqlStatement.executeUpdate("DELETE FROM [dbo].[COS_CUMPARATURI_P3] WHERE [ID_PRODUS] = " + produsCurent.getIdProdus());
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        });
                        listaProduseComandate.clear();
                        obiectFereastra.getLabelNrProduseInCos().setText("0");
                        obiectFereastra.getLabelSuma().setText(String.valueOf(Integer.parseInt(obiectFereastra.getLabelSuma().getText()) - Integer.parseInt(labelSumaProduse.getText())));
                        obiectFereastra.setSaModificatSoldul(true);
                        actualizeazaProduseInCos();
                        afisareCosCumparaturiGol();
                        labelADouaParte.setVisible(false);
                        afisareMesajAvertizare("Comanda a fost plasata cu succes!");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else afisareMesajAvertizare("Nu poti plasa comanda deoarece cosul tau de cumparaturi este gol");
        if (e.getSource().equals(butonStergeProdus)) {
            if (listaProduseComandate.size() == 0) // dacă coșul este gol nu mai putem șterge produse
                afisareMesajAvertizare("Nu mai poți șterge produse din coș, acesta este gol!");
            else if (JListProduseComandate.getSelectedValuesList().size() == 0) // dacă nu am selectat nimic pentru șters
                afisareMesajAvertizare("Trebuie să selectezi un produs înainte de a-l șterge!");
            else {
                for (Produs produsPentruSters : JListProduseComandate.getSelectedValuesList()) { // preluăm produsele selectate de către utilizator
                    try {
                        ConexiuneDB conexiuneDB = new ConexiuneDB();
                        Statement sqlStatement = conexiuneDB.getSQLConnection().createStatement();
                        sqlStatement.executeUpdate("DELETE FROM [dbo].[COS_CUMPARATURI_P3] WHERE [ID_UTILIZATOR] = " + obiectFereastra.getUtilizatorAutentificat().getIdUtilizator() +
                                " AND [ID_PRODUS] = " + produsPentruSters.getIdProdus()); // ștergem fiecare produs selectat din baza de date
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    listaProduseComandate.removeIf(produsCurent -> produsCurent.getIdProdus().equals(produsPentruSters.getIdProdus())); // în același timp le ștergem și din listă
                    obiectFereastra.getLabelNrProduseInCos().setText(String.valueOf(listaProduseComandate.size()));
                }
                if (listaProduseComandate.size() == 0) {
                    actualizeazaProduseInCos(); // ștergem produsele din JList
                    afisareCosCumparaturiGol(); // apelăm această funcție de afișare deoarece coșul nostru de cumpărături este acuma gol !
                    labelADouaParte.setVisible(false);
                } else {
                    actualizeazaProduseInCos(); // ștergem produsele selectate
                    actualizareSoldProduse(); // acutalizăm prețul ( prețul total - suma preturilor produselor șterge ... )
                }

            }
        }
        if (e.getSource().equals(butonAnulare)) {
            this.setVisible(false);
            labelAvertizare.setVisible(false);
            obiectFereastra.setVisible(true);
        }
    }

    @NotNull
    private StringBuilder creareStringComanda() {
        StringBuilder stringBuilder = new StringBuilder();
        listaProduseComandate.forEach(produscurent -> stringBuilder.append("\t").append(" ").append(produscurent).append("\n"));
        stringBuilder.append("In valoare de ").append(labelSumaProduse.getText()).append(" RON\n");
        return stringBuilder;
    }

    private void afisareMesajAvertizare(String continutMesaj) {
        labelAvertizare.setText(continutMesaj);
        if (!labelAvertizare.isVisible()) labelAvertizare.setVisible(true);
    }

}
