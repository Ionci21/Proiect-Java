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
 * Clasa {@link FereastraPrincipala} în care utilizatorul/administratorul poate să se autentifice sau să navigheze spre alte ferestre ale aplicației
 * <p></p>
 * <pre>În această clasă au fost implementate următoarele:
 *      1. Orice persoană care dorește să folosească aplicația mai întâi trebuie să-și creeze un cont, din această Fereastră există posibilitatea de a naviga spre {@link FereastraInregistrare} unde un cont poate fi creat
 *      2. Utilizatorul poate folosi contul tocmai creat pentru a se autentifica, în urma autentificării va fi trimis către o altă fereastră {@link FereastraUtilizatorAutentificat}
 *      3. Administratorul se poate conecta cu credențialele de administrator
 *      4. Diferite modalități de a verifica dacă există un cont cu credențialele introduse de către utilizator, dacă a completat field-urile, etc...</pre>
 *
 * @author Mercescu Ionut
 */
@SuppressWarnings("unused")
public class FereastraPrincipala extends JFrame implements ActionListener, MouseListener {

    private final Color culoareInchisa = new Color(38, 40, 51), culoareGalbena = new Color(255, 198, 104);
    private final List<JButton> listaButoane;
    private JPanel panouPrincipal, sectiuneLogin, sectiuneInformatii;
    private JTextField fieldUsername;
    private JPasswordField fieldParola;
    private JButton butonAutentificare, butonIesire;
    private JLabel labelUsername, labelParola, labelAutentificare, labelCreareCont, labelInformatii;

    /**
     * Constructorul clasei {@link FereastraPrincipala}
     *
     * @param titluFereastra Titlul ferestrei
     */
    public FereastraPrincipala(String titluFereastra) {
        super(titluFereastra);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1000, 500));
        this.setLocationRelativeTo(null);

        // Pentru design și funcționalitate
        listaButoane = creareSiStilizareButoane();
        fieldUsername.setBorder(BorderFactory.createEmptyBorder());
        fieldParola.setBorder(BorderFactory.createEmptyBorder());
        adaugareImagini();
        adaugaImagineAutentificare();
        labelCreareCont.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelCreareCont.addMouseListener(this);
        labelInformatii.setVisible(false);

        this.add(panouPrincipal);
        this.setVisible(true);
    }

    /**
     * Functie ce creaza efectiv un ImageIcon cu imaginea "<b>autentificare.png</b>" care mai apoi este adăugata label-ului {@link FereastraPrincipala#labelAutentificare}
     */
    private void adaugaImagineAutentificare() {
        ImageIcon imageIcon = new ImageIcon("iconite/autentificare.png");
        Image image = imageIcon.getImage();
        Image nouaImagine = image.getScaledInstance(100, 80, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(nouaImagine);
        labelAutentificare.setIcon(imageIcon);
    }

    /**
     * Functie ce creaza ImageIcon-uri pornind de la un path spre imaginea respectiva
     *
     * @param pathImagine path-ul imaginii
     * @return un ImageIcon cu imaginea respectivă
     */
    private @NotNull ImageIcon creareImagine(String pathImagine) {
        ImageIcon imageIcon = new ImageIcon(pathImagine);
        Image image = imageIcon.getImage();
        Image nouaImagine = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(nouaImagine);
    }

    /**
     * Funcție ce se ocupă de adăugarea imaginilor create de către {@link FereastraPrincipala#creareImagine(String pathImagine)}
     */
    private void adaugareImagini() {
        ImageIcon imageIcon = creareImagine("iconite/username.png");
        ImageIcon imageIcon2 = creareImagine("iconite/parola.png");
        labelUsername.setIcon(imageIcon);
        labelParola.setIcon(imageIcon2);
        labelUsername.setIconTextGap(20);
        labelParola.setIconTextGap(20);
    }

    /**
     * Funcție ce ne crează o listă de butoane pentru a ne ușura modificarea și adăugarea funcționalității butoanelor
     *
     * @return Listă cu butoanele noi create
     */
    private @NotNull List<JButton> creareSiStilizareButoane() {
        List<JButton> pentruReturnat = new ArrayList<>(List.of(butonAutentificare, butonIesire));
        pentruReturnat.forEach(butonCurent -> {
            butonCurent.setBorderPainted(false);
            butonCurent.setFocusable(false);
            butonCurent.addActionListener(this);
            butonCurent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        });
        return pentruReturnat;
    }

    /**
     * Metoda clasei în car este creată funcționalitatea butoanelor
     * <p></p>
     * <pre>
     *      1. Dacă utilizatorul selectează {@link FereastraPrincipala#butonAutentificare} se vor face verificări înainte de a căuta în baza de date contul cu credențialele introduse
     *      2. Dacă utilizatorul selectează {@link FereastraPrincipala#butonIesire} aplicația își va termina execuția
     * </pre>
     *
     * @param e ActionEvent-ul primit în urma selectării unuia din următoarele butoane aflate în lista {@link FereastraPrincipala#listaButoane}
     */
    @Override
    public void actionPerformed(@NotNull ActionEvent e) {
        if (e.getSource().equals(butonAutentificare)) {
            if (verificareFielduriCompletate()) {
                if (fieldUsername.getText().equals("Admin") && new String(fieldParola.getPassword()).equals("admin")) {
                    FereastraAdministrator fereastraAdministrator = new FereastraAdministrator("Aplicatie Gestionare Conturi");
                    this.dispose();
                } else {
                    try {
                        ConexiuneDB conexiuneDB = new ConexiuneDB();
                        Statement statement = conexiuneDB.getSQLConnection().createStatement();
                        ResultSet resultSet = statement.executeQuery(creareStringQuery());
                        if (!resultSet.next())
                            afisareMesajAutentificareEsuata("Nu există niciun utilizator cu credențialele tocmai introduse!");
                        else verificareSiAutentificare(resultSet);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } else afisareMesajAutentificareEsuata("Completeaza cele 2 field-uri înainte de a te autentifica");
        }
        if (e.getSource().equals(butonIesire)) this.dispose();
    }

    /**
     * Funcție ce verifică dacă există un cont în baza de date cu credențialele introduse de către utilizator, există două cazuri
     * <pre>
     *     1. Dacă nu s-a găsit niciun cont cu credențialele introduse atunci autentificarea a eșuat iar un mesaj se va afișa pe {@link FereastraPrincipala#labelInformatii}
     *     2. Dacă autentificarea s-a realizat cu succes, fereastra curentă se distruge și se crează o nouă fereastră de tipul {@link FereastraUtilizatorAutentificat} unde utilizatorul poate plasa comenzi etc...
     * </pre>
     *
     * @param resultSet rezultatul în urma căutării în baza de date
     * @throws SQLException aruncă SQLException în cazul unei erori
     */
    private void verificareSiAutentificare(@NotNull ResultSet resultSet) throws SQLException {
        String usernameUtilizator = resultSet.getString("USERNAME");
        String parolaUtilizator = resultSet.getString("PAROLA_UTILIZATOR");
        if (usernameUtilizator.equals(fieldUsername.getText()) && parolaUtilizator.equals(new String(fieldParola.getPassword()))) {
            this.dispose();
            FereastraUtilizatorAutentificat fereastraUtilizatorAutentificat = new FereastraUtilizatorAutentificat("Aplicatie Gestionare Stocuri", creareQuerySelectUtilizator(resultSet));
        } else if (usernameUtilizator.equals(fieldUsername.getText()) && !(parolaUtilizator.equals(new String(fieldParola.getPassword()))))
            afisareMesajAutentificareEsuata("Parola pentru utilizatorul " + usernameUtilizator + " este introdusa gresit!");
    }

    /**
     * Crează Query-ul pentru a selecta informațiile despre un Utilizator atunci când acesta s-a autentificat cu succes
     *
     * @param resultSet datele preluate din baza de date
     * @return Query-ul
     * @throws SQLException aruncă un {@link SQLException} în cazul unei erori
     */
    @NotNull
    private String creareQuerySelectUtilizator(@NotNull ResultSet resultSet) throws SQLException {
        return resultSet.getString("ID_UTILIZATOR") + ";" + resultSet.getString("USERNAME") + ";"
                + resultSet.getString("PAROLA_UTILIZATOR") + ";" + resultSet.getString("NUME_UTILIZATOR") + ";"
                + resultSet.getString("PRENUME_UTILIZATOR") + ";" + resultSet.getString("LOCALITATE_UTILIZATOR") + ";"
                + resultSet.getString("ADRESA_UTILIZATOR") + ";" + resultSet.getString("NUMAR_TELEFON") + ";"
                + resultSet.getString("SEX_UTILIZATOR") + ";" + resultSet.getString("SOLD_UTILIZATOR");
    }

    /**
     * Afișează pe {@link FereastraPrincipala#labelInformatii} anumite mesaje
     *
     * @param continutMesaj conținutul mesajului ce urmează să fie afișat
     */
    private void afisareMesajAutentificareEsuata(String continutMesaj) {
        labelInformatii.setText(continutMesaj);
        labelInformatii.setVisible(true);
    }

    /**
     * Creză Query-ul pentru a selecta utilizatorul din baza de date cu credențialele introduse
     *
     * @return String-ul Query-ului pentur SELECT
     */
    private @NotNull String creareStringQuery() {
        return "SELECT * FROM [dbo].[UTILIZATORI_P3] WHERE USERNAME = '" + fieldUsername.getText() + "' AND PAROLA_UTILIZATOR = '"
                + new String(fieldParola.getPassword()) + "';";
    }

    /**
     * Verifică dacă field-urile {@link FereastraPrincipala#fieldUsername} și {@link FereastraPrincipala#fieldParola} sunt completate
     *
     * @return True dacă sunt completate altfel Fals
     */
    private boolean verificareFielduriCompletate() {
        return !(fieldUsername.getText().isEmpty()) && fieldParola.getPassword().length > 0;
    }

    /**
     * Getterul ce returnează lista de butoane
     *
     * @return lista {@link FereastraPrincipala#listaButoane}
     */
    public List<JButton> getListaButoane() {
        return listaButoane;
    }

    /**
     * Getter-ul ce returneaza butonul "butonAutentificare"
     *
     * @return butonul {@link FereastraPrincipala#butonAutentificare}
     */
    public JButton getButonAutentificare() {
        return butonAutentificare;
    }

    /**
     * Getter-ul ce returnează butonul "butonIesire"
     *
     * @return butonul {@link FereastraPrincipala#butonIesire}
     */
    public JButton getButonIesire() {
        return butonIesire;
    }

    /**
     * Getter-ul ce returnează field-ul "fieldUsername"
     *
     * @return field-ul {@link FereastraPrincipala#fieldUsername}
     */
    public JTextField getFieldUsername() {
        return fieldUsername;
    }

    /**
     * Getter-ul ce returnează field-ul "fieldParola"
     *
     * @return field-ul {@link FereastraPrincipala#fieldParola}
     */
    public JPasswordField getFieldParola() {
        return fieldParola;
    }

    /**
     * Getter-ul ce returnează label-ul "labelInformatii"
     *
     * @return label-ul {@link FereastraPrincipala#labelInformatii}
     */
    public JLabel getLabelInformatii() {
        return labelInformatii;
    }

    /**
     * Funcție ce se ocupă de evenimentele de mouse, folosită atunci când apăsăm pe "aici" pentu a ne redirecționa către fereastra {@link FereastraInregistrare}
     *
     * @param e eventul de mouse
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        this.dispose();
        FereastraInregistrare obiectFereastraInregistrare = new FereastraInregistrare("Aplicatie Gestionare Stocuri");
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
