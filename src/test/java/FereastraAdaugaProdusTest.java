import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FereastraAdaugaProdusTest {

    FereastraAdaugaProdus fereastraAdaugaProdus = new FereastraAdaugaProdus("Test", new FereastraAdministrator("Test"));

    @Test
    void functieTestare() {
        fereastraAdaugaProdus.getButonAdaugaProdus().doClick();
        assertEquals("Completează toate câmpurile înainte de a încerca să adaugi un produs!", fereastraAdaugaProdus.getLabelAvertisment().getText());

        fereastraAdaugaProdus.getFieldNumeProdus().setText("Test");
        fereastraAdaugaProdus.getFieldCantitate().setText("5");
        fereastraAdaugaProdus.getFieldPretUnitar().setText("5");
        fereastraAdaugaProdus.getButonAdaugaProdus().doClick();
        assertEquals("Produsul \"Test\" a fost adaugat cu succes in stocul de produse!", fereastraAdaugaProdus.getLabelAvertisment().getText());

        fereastraAdaugaProdus.getButonInapoi().doClick();
        assertFalse(fereastraAdaugaProdus.isVisible());
    }

}