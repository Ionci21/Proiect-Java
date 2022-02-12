import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FereastraUtilizatorAutentificatTest {

    FereastraUtilizatorAutentificat fereastraUtilizatorAutentificat =
            new FereastraUtilizatorAutentificat("Test", "1;Ionci;1234;Mercescu;Ionut;Timisoara;str. Ludwig von Ybl nr. 20B;0762831669;Masculin;2000");

    @Test
    void functieTestare() {
        assertEquals("Ionci", fereastraUtilizatorAutentificat.getUtilizatorAutentificat().getUsernameUtilizator());

        fereastraUtilizatorAutentificat.getButonAdauga().doClick();
        assertEquals("Trebuie să selectezi suma pe care vrei să o adaugi la sold înainte de a apăsa pe buton!", fereastraUtilizatorAutentificat.getLabelAvertizare().getText());

        fereastraUtilizatorAutentificat.getbutonRadio1000().setSelected(true);
        fereastraUtilizatorAutentificat.getbutonRadio1000().doClick();
        fereastraUtilizatorAutentificat.getButonAdauga().doClick();
        assertEquals("3000", fereastraUtilizatorAutentificat.getLabelSuma().getText());

        fereastraUtilizatorAutentificat.getButonIesire().doClick();
        assertFalse(fereastraUtilizatorAutentificat.isVisible());

        fereastraUtilizatorAutentificat.getButonIesire().doClick();
        assertFalse(fereastraUtilizatorAutentificat.isVisible());
    }

}