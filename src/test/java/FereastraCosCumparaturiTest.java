import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FereastraCosCumparaturiTest {

    FereastraCosCumparaturi fereastraCosCumparaturi = new FereastraCosCumparaturi("Test",
            new FereastraUtilizatorAutentificat("Test", "1;Ionci;1234;Mercescu;Ionut;Timisoara;str. Ludwig von Ybl nr. 20B;0762831669;Masculin;2000"));

    @Test
    void actionPerformed() {

        fereastraCosCumparaturi.getButonAnulare().doClick();
        assertFalse(fereastraCosCumparaturi.isVisible());

        fereastraCosCumparaturi.getButonPlaseazaComanda().doClick();
        assertEquals("Nu poti plasa comanda deoarece cosul tau de cumparaturi este gol", fereastraCosCumparaturi.getLabelAvertizare().getText());

        fereastraCosCumparaturi.getButonStergeProdus().doClick();
        System.out.println(fereastraCosCumparaturi.getLabelAvertizare().getText());
        assertEquals("Nu mai poți șterge produse din coș, acesta este gol!", fereastraCosCumparaturi.getLabelAvertizare().getText());

    }
}