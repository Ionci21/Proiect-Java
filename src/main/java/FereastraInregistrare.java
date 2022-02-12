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

/**
 * Clasa FereastraInregistrare care asigură funcționalitate pentru ca un utilizator să-și poată crea un cont cu succes în aplicație
 *
 * @author Mercescu Ionut
 */
public class FereastraInregistrare extends JFrame implements ActionListener {
    private final ButtonGroup grupButoaneRadio;
    private final List<JTextField> listaFielduri;
    private final List<JButton> listaButoane;
    private final List<JRadioButton> listaButoaneRadio;
    private JTextField fieldUsername, fieldNume, fieldPrenume, fieldLocalitate, fieldAdresa, fieldNumarDeTelefon;
    private JPasswordField fieldParola;
    private JLabel labelUsername;
    private JLabel labelParola;
    private JLabel labelNume;
    private JLabel labelPrenume;
    private JLabel labelAvertizare;
    private JRadioButton butonRadioMasculin, butonRadioFeminin;
    private JButton butonInregistrare, butonAnulare;
    private JPanel panouPrincipal;

    /**
     * Constructorul clasei
     *
     * @param titluFereastra titlul ferestrei
     */
    public FereastraInregistrare(String titluFereastra) {
        super(titluFereastra);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1024, 512));
        this.setLocationRelativeTo(null);

        labelAvertizare.setVisible(false);
        listaFielduri = creareListaFielduri();
        listaButoane = creareListaButoane();
        listaButoaneRadio = creareListaButoaneRadio();
        grupButoaneRadio = new ButtonGroup();
        modificareElementeDinListe(listaFielduri, listaButoane, listaButoaneRadio);

        this.add(panouPrincipal);
        this.setVisible(true);
    }

    public JLabel getLabelAvertizare() {
        return labelAvertizare;
    }

    /**
     * Functie care aduce modificari cosmetice elementelor din listele de mai jos, butoanelor le adauga ActionListeners
     *
     * @param listaFielduri     lista cu fieldurile ferestrei
     * @param listaButoane      lista cu butoanele ferestrei
     * @param listaButoaneRadio lista cu butoanele radio ale ferestrei
     */
    private void modificareElementeDinListe(@NotNull List<JTextField> listaFielduri, @NotNull List<JButton> listaButoane, @NotNull List<JRadioButton> listaButoaneRadio) {
        fieldParola.setBorder(BorderFactory.createEmptyBorder());
        listaFielduri.forEach(fieldCurent -> fieldCurent.setBorder(BorderFactory.createEmptyBorder()));
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
            butonRadioCurent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        });
    }

    /**
     * Adauga fieldurile intr-o lista pentru a aduce modificari asupra lor mai usor!
     *
     * @return lista cu fieldurile ( JTextField )
     */
    @Contract(" -> new")
    private @NotNull List<JTextField> creareListaFielduri() {
        return new ArrayList<>(List.of(fieldUsername, fieldNume, fieldPrenume, fieldLocalitate, fieldAdresa, fieldNumarDeTelefon));
    }

    /**
     * Adauga butoanele radio intr-o lista pentru a aduce modificari asupra lor mai usor!
     *
     * @return lista cu butoanele radio ( JRadioButton )
     */
    @Contract(" -> new")
    private @NotNull List<JRadioButton> creareListaButoaneRadio() {
        return new ArrayList<>(List.of(butonRadioMasculin, butonRadioFeminin));
    }

    /**
     * Adauga butoanele intr-o lista pentru a aduce modificari asupra lor mai usor!
     *
     * @return lista cu butoanele ( JButton )
     */
    @Contract(" -> new")
    private @NotNull List<JButton> creareListaButoane() {
        return new ArrayList<>(List.of(butonInregistrare, butonAnulare));
    }

    /**
     * În funcție de butonul selectat se pot întâmpla următoarele:
     * <pre>
     *     1. Dacă selectăm {@link FereastraInregistrare#butonInregistrare} și n-a eșuat nicio verificare vom crea un nou utilizator în baza de date
     *     2. Dacă selectăm {@link FereastraInregistrare#butonAnulare} ne vom întoarce înapoi la fereastra principală\
     * </pre>
     *
     * @param e eventul primit în urma selectării unui buton
     */
    @Override
    public void actionPerformed(@NotNull ActionEvent e) {
        if (e.getSource().equals(butonInregistrare)) {
            if (verificareCampuri()) {
                boolean maiExistaUnUtilizator = false;
                try {
                    ConexiuneDB conexiuneDB = new ConexiuneDB();
                    try (Statement sqlStatement = conexiuneDB.getSQLConnection().createStatement()) {
                        ResultSet resultSet = sqlStatement.executeQuery("SELECT * FROM [dbo].[UTILIZATORI_P3]");
                        while (resultSet.next())
                            if (fieldUsername.getText().equals(resultSet.getString("USERNAME"))) {
                                maiExistaUnUtilizator = true;
                                break;
                            }
                        if (!maiExistaUnUtilizator) {
                            sqlStatement.executeUpdate(creareStringInserare());
                            this.dispose();
                            FereastraPrincipala fereastraPrincipala = new FereastraPrincipala("Aplicatie Gestionare Stocuri");
                            afisareMesajAvertizare(fereastraPrincipala.getLabelInformatii(), "Utilizator inregistrat cu succes!");
                        } else afisareMesajAvertizare(labelAvertizare, "Exista deja un utilizator cu username-ul " + fieldUsername.getText() +
                                ", introdu unul nou!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else
                afisareMesajAvertizare(labelAvertizare, "Unul sau mai multe field-uri necompletate! Pentru a te înregistra cu succes toate field-urile trebuie să fie completate!");
        }
        if (e.getSource().equals(butonAnulare)) {
            this.dispose();
            FereastraPrincipala obiectFereastraPrincipala = new FereastraPrincipala("Aplicatie Gestionare Stocuri");
        }
    }

    /**
     * Afișează un mesaj de avertizare
     *
     * @param labelAvertizare unde afișăm mesajul
     * @param continutMesaj   conținutul mesajului
     */
    private void afisareMesajAvertizare(@NotNull JLabel labelAvertizare, String continutMesaj) {
        labelAvertizare.setText(continutMesaj);
        labelAvertizare.setVisible(true);
    }

    /**
     * Ne crează o comandă SQL cu care vom adăuga Utilizatorul în baza de date
     *
     * @return comanda SQL
     */
    private @NotNull String creareStringInserare() {
        return "INSERT INTO UTILIZATORI_P3 (USERNAME, PAROLA_UTILIZATOR, NUME_UTILIZATOR, PRENUME_UTILIZATOR, LOCALITATE_UTILIZATOR, ADRESA_UTILIZATOR, NUMAR_TELEFON, SEX_UTILIZATOR)"
                + " VALUES ('" + fieldUsername.getText() + "', '" + new String(fieldParola.getPassword()) + "', '" + fieldNume.getText() + "', '" + fieldPrenume.getText() + "', '" +
                fieldLocalitate.getText() + "', '" + fieldAdresa.getText() + "', '" + fieldNumarDeTelefon.getText() + "', '" +
                (butonRadioMasculin.isSelected() ? "Masculin" : "Feminin") + "');";
    }

    /**
     * Verifica ca toate field-urile necesare sa fie completate
     *
     * @return true daca informatiile au fost completate, false in celelalte cazuri
     */
    private boolean verificareCampuri() {
        boolean conditieFielduri = listaFielduri.stream().noneMatch(fieldCurent -> fieldCurent.getText().isEmpty());
        boolean conditieButoaneRadio = listaButoaneRadio.stream().anyMatch(AbstractButton::isSelected);
        return conditieFielduri && conditieButoaneRadio && fieldParola.getPassword().length > 0;
    }

    public List<JTextField> getListaFielduri() {
        return listaFielduri;
    }

    public List<JButton> getListaButoane() {
        return listaButoane;
    }

    public List<JRadioButton> getListaButoaneRadio() {
        return listaButoaneRadio;
    }

    public JButton getButonInregistrare() {
        return butonInregistrare;
    }

    public JButton getButonAnulare() {
        return butonAnulare;
    }

    public JPasswordField getFieldParola() {
        return fieldParola;
    }

    public JTextField getFieldUsername() {
        return fieldUsername;
    }

    public JTextField getFieldNume() {
        return fieldNume;
    }

    public JTextField getFieldPrenume() {
        return fieldPrenume;
    }

    public JTextField getFieldLocalitate() {
        return fieldLocalitate;
    }

    public JTextField getFieldAdresa() {
        return fieldAdresa;
    }

    public JTextField getFieldNumarDeTelefon() {
        return fieldNumarDeTelefon;
    }

    public JRadioButton getButonRadioMasculin() {
        return butonRadioMasculin;
    }

    public JRadioButton getButonRadioFeminin() {
        return butonRadioFeminin;
    }
}
