import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasă responsabilă de a afișa un istoric al comenzilor pentru fiecare utilizator autentificat
 */
public class FereastraIstoricComenzi extends JFrame implements ActionListener {
    private JPanel panouPrincipal;
    private JLabel labelNumeUtilizator;
    private JTextPane panouComenzi;
    private JButton butonInapoi, butonIesire;
    private final FereastraUtilizatorAutentificat obiectFereastra;
    private List<JButton> listaButoane;
    private final String idUtilizator;

    /**
     * Constructorul clasei
     * @param titluFereastra titlul ferestrei
     * @param numeUtilizator numele utilizatorului
     * @param idUtilizator id-ul utilizatorului
     * @param obiectFereastra obiectul ferestrei precedente
     */
    public FereastraIstoricComenzi(String titluFereastra, String numeUtilizator, String idUtilizator, FereastraUtilizatorAutentificat obiectFereastra) {
        super(titluFereastra);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(800, 600));
        this.setLocationRelativeTo(null);

        this.obiectFereastra = obiectFereastra;
        this.obiectFereastra.setVisible(false);
        listaButoane = creareListaSiAdaugareFunctionalitate();
        labelNumeUtilizator.setText(numeUtilizator);
        this.idUtilizator = idUtilizator;

        centrareText();
        afisareComenzi();

        this.add(panouPrincipal);
        this.setVisible(true);
    }

    /**
     * Metodă ce preia din baza de date pentru utilizatorul autentificat comenzile plasate de către acesta
     */
    private void afisareComenzi() {
        try {
            ConexiuneDB conexiuneDB = new ConexiuneDB();
            Statement sqlStatement = conexiuneDB.getSQLConnection().createStatement();
            ResultSet resultSet = sqlStatement.executeQuery("SELECT * FROM [dbo].[ISTORIC_COMENZI_P3] WHERE [ID_UTILIZATOR] = " + idUtilizator);
            if (!resultSet.isBeforeFirst()) System.out.println("Nicio comanda creata pentru " + labelNumeUtilizator.getText());
            else {
                StringBuilder stringBuilder = new StringBuilder();
                while (resultSet.next())
                    stringBuilder.append("Comanda cu IDul ").append(resultSet.getString("ID_COMANDA")).append(" formata din urmatoarele produse:\n")
                            .append(resultSet.getString("MESAJ_COMANDA")).append("\n");
                panouComenzi.setText(stringBuilder.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * O metodă ce se ocupă cu centrarea textului pe mijlocul JTextPane-ului
     */
    private void centrareText() {
        StyledDocument documentulCurent = panouComenzi.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        documentulCurent.setParagraphAttributes(0, documentulCurent.getLength(), center, false);
        panouComenzi.setEditable(false);
    }

    /**
     * Crează o listă de butoane și adaugă fiecărui buton funcționalitate și unele elemente de design
     * @return lista tocmai creată cu butoane
     */
    private @NotNull List<JButton> creareListaSiAdaugareFunctionalitate() {
        List<JButton> pentruReturnat = new ArrayList<>(List.of(butonInapoi, butonIesire));
        pentruReturnat.forEach(butonCurent -> {
            butonCurent.setCursor(new Cursor(Cursor.HAND_CURSOR));
            butonCurent.setFocusable(false);
            butonCurent.addActionListener(this);
        });
        return pentruReturnat;
    }

    /**
     * Dacă selectăm {@link FereastraIstoricComenzi#butonInapoi} ne întoarcem la fereastra precedentă, altfel ieșim din aplicație
     * @param e
     */
    @Override
    public void actionPerformed(@NotNull ActionEvent e) {
        if (e.getSource().equals(butonInapoi)) {
            this.dispose();
            obiectFereastra.setVisible(true);
        }
        if (e.getSource().equals(butonIesire)) {
            this.dispose();
            obiectFereastra.dispose();
        }
    }

    public JLabel getLabelNumeUtilizator() {
        return labelNumeUtilizator;
    }

    public JTextPane getPanouComenzi() {
        return panouComenzi;
    }

    public JButton getButonInapoi() {
        return butonInapoi;
    }

    public JButton getButonIesire() {
        return butonIesire;
    }
}
