import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class FereastraStergeProdusTest {

    FereastraStergeProdus fereastraStergeProdus = new FereastraStergeProdus("Test", new FereastraAdministrator("Test"));

    @Test
    void functieTestare() {
        fereastraStergeProdus.getButonStergeProdus().doClick();
        System.out.println(fereastraStergeProdus.getLabelAvertisment().getText());

        fereastraStergeProdus.getButonInapoi().doClick();
        assertFalse(fereastraStergeProdus.isVisible());
    }

}