import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FereastraModificaProdusTest {

    FereastraModificaProdus fereastraModificaProdus = new FereastraModificaProdus("Test", new FereastraAdministrator("Test"));

    @Test
    void functieTestare() {
        fereastraModificaProdus.getButonModificaProdus().doClick();
        assertEquals("Selectează măcar un produs înainte de a încerca să modifici !", fereastraModificaProdus.getLabelAvertisment().getText());

//        fereastraModificaProdus.getListaCuProduse().setSelectedIndex(7);
//        fereastraModificaProdus.getFieldNumeProdus().setText("Test-Test");
//        fereastraModificaProdus.getButonModificaProdus().doClick();
//        System.out.println(fereastraModificaProdus.getLabelAvertisment().getText());

        fereastraModificaProdus.getButonInapoi().doClick();
        assertFalse(fereastraModificaProdus.isVisible());
    }

}