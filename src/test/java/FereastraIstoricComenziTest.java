import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FereastraIstoricComenziTest {

    FereastraIstoricComenzi fereastraIstoricComenzi = new FereastraIstoricComenzi("Test", "Ionci", "1",
            new FereastraUtilizatorAutentificat("Test", "1;Ionci;1234;Mercescu;Ionut;Timisoara;str. Ludwig von Ybl nr. 20B;0762831669;Masculin;2000"));

    @Test
    void functieTestare() {
        fereastraIstoricComenzi.getButonInapoi().doClick();
        assertFalse(fereastraIstoricComenzi.isVisible());

        assertEquals("Ionci", fereastraIstoricComenzi.getLabelNumeUtilizator().getText());

        fereastraIstoricComenzi.getButonIesire().doClick();
        assertFalse(fereastraIstoricComenzi.isVisible());

        assertEquals("", fereastraIstoricComenzi.getPanouComenzi().getText());
    }

}