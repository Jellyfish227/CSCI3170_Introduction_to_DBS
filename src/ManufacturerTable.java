import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ManufacturerTable extends Table {
    public static final int COLUMNS = 4;

    @Override
    public void createTable() throws SQLException {
        deleteTable();
        Statement stmt = conn.createStatement();

    }

    @Override
    public void deleteTable() throws SQLException {

    }

    @Override
    public void loadTable() throws SQLException, IOException {
        System.out.println("Loading manufacturers...");
        try (BufferedReader reader = new BufferedReader(new FileReader(Table.getSouceDir() + "manufacturer.txt"))) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO manufacturer VALUES (?, ?, ?, ?)");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                stmt.setInt(1, Integer.parseInt(data[0]));
                stmt.setString(2, data[1]);
                stmt.setString(3, data[2]);
                stmt.setInt(4, Integer.parseInt(data[3]));
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public String[] queryTable(String query) {
        return new String[0];
    }
}
