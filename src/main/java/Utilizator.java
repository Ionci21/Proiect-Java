import org.jetbrains.annotations.NotNull;

/**
 * Clasa folosită când creăm un utilizator din informațiile preluate din baza de date la autentificare
 * @author Mercescu Ionut
 */
public class Utilizator {

    private final String idUtilizator, usernameUtilizator, parolaUtilizator, numeUtilizator, prenumeUtilizator, localitateUtilizator, adresaUtilizator, nrTelefonUtilizator, sexUtilizator;
    private String soldUtilizator;

    /**
     * Constructorul clasei
     * @param dateUtilizator String cu informațiile utilizatorului ( pe care le "spargem" în elemente diferite și le atribuim fiecărui atribut al clasei în parte )
     */
    public Utilizator(@NotNull String dateUtilizator) {
        String[] elementeSeparate = dateUtilizator.split(";");
        idUtilizator = elementeSeparate[0];
        usernameUtilizator = elementeSeparate[1];
        parolaUtilizator = elementeSeparate[2];
        numeUtilizator = elementeSeparate[3];
        prenumeUtilizator = elementeSeparate[4];
        localitateUtilizator = elementeSeparate[5];
        adresaUtilizator = elementeSeparate[6];
        nrTelefonUtilizator = elementeSeparate[7];
        sexUtilizator = elementeSeparate[8];
        soldUtilizator = elementeSeparate[9];
    }

    public String getLocalitateUtilizator() {
        return localitateUtilizator;
    }

    public String getAdresaUtilizator() {
        return adresaUtilizator;
    }

    public String getNrTelefonUtilizator() {
        return nrTelefonUtilizator;
    }

    public String getUsernameUtilizator() {
        return usernameUtilizator;
    }

    public String getNumeUtilizator() {
        return numeUtilizator;
    }

    public String getPrenumeUtilizator() {
        return prenumeUtilizator;
    }

    public String getSexUtilizator() {
        return sexUtilizator;
    }

    public String getSoldUtilizator() {
        return soldUtilizator;
    }

    public void setSoldUtilizator(String soldUtilizator) {
        this.soldUtilizator = soldUtilizator;
    }

    public String getIdUtilizator() {
        return idUtilizator;
    }

    public String getParolaUtilizator() {
        return parolaUtilizator;
    }
}
