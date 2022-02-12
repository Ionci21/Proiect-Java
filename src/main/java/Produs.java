import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Clasa {@link Produs} folosită pentru a crea obiecte de tip {@code Produs} la nevoie când citim din baza de date!
 * @author Mercescu Ionut
 */
public class Produs {

    private final String idProdus, numeProdus, pretProdus, cantitateProdus;

    /**
     * Construcorul clasei
     * @param idProdus iDul produsului
     * @param numeProdus numele produsului
     * @param pretProdus prețul produsului
     * @param cantitateProdus cantitatea produsului
     */
    public Produs(String idProdus, String numeProdus, String pretProdus, String cantitateProdus) {
        this.idProdus = idProdus;
        this.numeProdus = numeProdus;
        this.pretProdus = pretProdus;
        this.cantitateProdus = cantitateProdus;
    }

    /**
     * Al doilea constructor al clasei
     * @param informatiiProdus informațiile primite ca și linie
     */
    @Contract(pure = true)
    public Produs(@NotNull String informatiiProdus) {
        String[] elementeSeparate = informatiiProdus.split(";");
        idProdus = elementeSeparate[0];
        numeProdus = elementeSeparate[1];
        pretProdus = elementeSeparate[2];
        cantitateProdus = elementeSeparate[3];
    }

    /**
     * Afișarea obiectelor de tipul clasei Produs
     * @return String cu afișarea unui obiect de tip Produs
     */
    @Override
    public String toString() {
        return idProdus + ". " + numeProdus + " in valoare de " + pretProdus + " RON";
    }

    /**
     * Getter pentru {@link #idProdus}
     * @return iDul produsului
     */
    public String getIdProdus() {
        return idProdus;
    }

    /**
     * Getter pentru {@link #pretProdus}
     * @return prețul produsului
     */
    public String getPretProdus() {
        return pretProdus;
    }

    /**
     * Getter pentru {@link #cantitateProdus}
     * @return cantitatea produsului
     */
    public String getCantitateProdus() {
        return cantitateProdus;
    }
}
