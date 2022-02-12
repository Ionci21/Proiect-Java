import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class FereastraAdministratorTest {

    FereastraAdministrator fereastraAdministrator = new FereastraAdministrator("Test");

    @Test
    void functieTestare() {
        fereastraAdministrator.getButonDeconectare().doClick();
        assertFalse(fereastraAdministrator.isVisible());

        fereastraAdministrator.getButonIesire().doClick();
        assertFalse(fereastraAdministrator.isVisible());
    }

}