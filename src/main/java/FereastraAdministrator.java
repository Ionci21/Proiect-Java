import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa administrator din care putem naviga spre {@link FereastraAdaugaProdus}, {@link FereastraModificaProdus} sau {@link FereastraStergeProdus}
 * @author Mercescu Ionut
 */
public class FereastraAdministrator extends JFrame implements ActionListener {
    private JButton butonDeconectare, butonIesire, butonAdaugaProdus, butonStergeProdus, butonModificaProdus;
    private JPanel panouPrincipal;

    private final List<JButton> listaButoane;

    /**
     * Constructorul clasei
     * @param titluFereastra titlul ferestrei
     */
    public FereastraAdministrator(String titluFereastra) {
        super(titluFereastra);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(800, 400));
        this.setLocationRelativeTo(null);
        listaButoane = creareListaButoane();
        modificareButoaneDinLista(listaButoane);

        this.add(panouPrincipal);
        this.setVisible(true);
    }

    /**
     * Metodă ce se ocupă de adăugarea funcționalității și a unor elemente de design butoanelor din listă
     * @param listaButoane lista de butoane cărora le aducem modificările
     */
    private void modificareButoaneDinLista(@NotNull List<JButton> listaButoane) {
        listaButoane.forEach(butonCurent -> {
            butonCurent.setBorderPainted(false);
            butonCurent.setFocusable(false);
            butonCurent.addActionListener(this);
            butonCurent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        });
    }

    /**
     * Metodă ce ne crează listă cu butoanele prezente în clasă
     * @return lista cu butoane
     */
    @Contract(" -> new")
    private @NotNull List<JButton> creareListaButoane() {
        return new ArrayList<>(List.of(butonDeconectare, butonIesire, butonAdaugaProdus, butonStergeProdus, butonModificaProdus));
    }

    /**
     * În funcție de butonul pe care-l alegem se pot întâmpla următoarele:
     * <pre>
     *     1. Dacă alegem {@link FereastraAdministrator#butonAdaugaProdus} ni se va deschide fereastra de tipul {@link FereastraAdaugaProdus}
     *     2. Dacă alegem {@link FereastraAdministrator#butonModificaProdus} ni se va deschide fereastra de tipul {@link FereastraModificaProdus}
     *     3. Dacă alegem {@link FereastraAdministrator#butonStergeProdus} ni se va deschide fereastra de tipul {@link FereastraStergeProdus}
     *     4. Dacă alegem {@link FereastraAdministrator#butonDeconectare} ne vom întoarce înapoi la fereastra principală
     *     5. Dacă alegem {@link FereastraAdministrator#butonIesire} vom ieși din aplicație
     * </pre>
     * @param e eventul primit în urma selectării unui buton
     */
    @Override
    public void actionPerformed(@NotNull ActionEvent e) {
        if (e.getSource().equals(butonAdaugaProdus)) {
            FereastraAdaugaProdus fereastraAdaugaProdus = new FereastraAdaugaProdus("Aplicatie Gestionare Stocuri - Administrator", this);
            this.setVisible(false);
        }
        if (e.getSource().equals(butonModificaProdus)) {
            FereastraModificaProdus fereastraModificaProdus = new FereastraModificaProdus("Aplicatie Gestionare Stocuri - Administrator", this);
            this.setVisible(false);
        }
        if (e.getSource().equals(butonStergeProdus)) {
            FereastraStergeProdus fereastraStergeProdus = new FereastraStergeProdus("Aplicatie Gestionare Stocuri - Administrator", this);
            this.setVisible(false);
        }
        if (e.getSource().equals(butonDeconectare)) {
            this.dispose();
            FereastraPrincipala fereastraPrincipala = new FereastraPrincipala("Aplicatie Gestionare Stocuri - BETA");
        }
        if (e.getSource().equals(butonIesire)) this.dispose();
    }

    /**
     * Getter pentru {@link FereastraAdministrator#butonDeconectare}
     * @return butonul de deconectare
     */
    public JButton getButonDeconectare() { return butonDeconectare; }

    /**
     * Getter pentru {@link FereastraAdministrator#butonIesire}
     * @return butonul de iesire
     */
    public JButton getButonIesire() { return butonIesire; }

    /**
     * Getter pentru {@link FereastraAdministrator#listaButoane}
     * @return lista de butoane
     */
    public List<JButton> getListaButoane() { return listaButoane; }

}
