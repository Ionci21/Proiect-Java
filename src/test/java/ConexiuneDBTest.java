import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConexiuneDBTest {

    ConexiuneDB conexiuneDB;
    boolean conexiuneCuSucces = true;

    @Test
    void testareConexiune() {
        try {
            conexiuneDB = new ConexiuneDB();
        } catch (SQLException e) {
            conexiuneCuSucces = false;
            e.printStackTrace();
        }
        assertTrue(conexiuneCuSucces);
    }


}