import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FereastraPrincipalaTest {

    FereastraPrincipala fereastraPrincipala = new FereastraPrincipala("Test");

    @Test
    void functieTestare() {
        fereastraPrincipala.getButonIesire().doClick();
        assertFalse(fereastraPrincipala.isVisible());

        fereastraPrincipala.getButonAutentificare().doClick();
        assertEquals("Completeaza cele 2 field-uri înainte de a te autentifica", fereastraPrincipala.getLabelInformatii().getText());

        fereastraPrincipala.getFieldUsername().setText("Ionci");
        fereastraPrincipala.getFieldParola().setText("12345");
        fereastraPrincipala.getButonAutentificare().doClick();
        assertEquals("Nu există niciun utilizator cu credențialele tocmai introduse!", fereastraPrincipala.getLabelInformatii().getText());

        fereastraPrincipala.getFieldUsername().setText("Ionci");
        fereastraPrincipala.getFieldParola().setText("1234");
        fereastraPrincipala.getButonAutentificare().doClick();
        assertFalse(fereastraPrincipala.isVisible());

        fereastraPrincipala.getFieldUsername().setText("Admin");
        fereastraPrincipala.getFieldParola().setText("admin");
        fereastraPrincipala.getButonAutentificare().doClick();
        assertFalse(fereastraPrincipala.isVisible());
    }

}