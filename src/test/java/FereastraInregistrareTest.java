import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FereastraInregistrareTest {

    FereastraInregistrare fereastraInregistrare = new FereastraInregistrare("Test");

    @Test
    void functieTestare() {
        fereastraInregistrare.getButonInregistrare().doClick();
        System.out.println(fereastraInregistrare.getLabelAvertizare().getText());
        assertEquals("Unul sau mai multe field-uri necompletate! Pentru a te înregistra cu succes toate field-urile trebuie să fie completate!",
                fereastraInregistrare.getLabelAvertizare().getText());

        completeazaFielduri("Ionci");
        System.out.println(fereastraInregistrare.getLabelAvertizare().getText());
        assertEquals("Exista deja un utilizator cu username-ul Ionci, introdu unul nou!", fereastraInregistrare.getLabelAvertizare().getText());

        completeazaFielduri("Ionci21");
        fereastraInregistrare.getButonInregistrare().doClick();
        assertFalse(fereastraInregistrare.isVisible());

    }

    private void completeazaFielduri(String usernameulIntrodus) {
        fereastraInregistrare.getFieldUsername().setText(usernameulIntrodus);
        fereastraInregistrare.getFieldParola().setText("-");
        fereastraInregistrare.getFieldNume().setText("-");
        fereastraInregistrare.getFieldPrenume().setText("-");
        fereastraInregistrare.getFieldLocalitate().setText("-");
        fereastraInregistrare.getFieldAdresa().setText("-");
        fereastraInregistrare.getFieldNumarDeTelefon().setText("-");
        fereastraInregistrare.getButonRadioMasculin().setSelected(true);
        fereastraInregistrare.getButonInregistrare().doClick();
    }
}