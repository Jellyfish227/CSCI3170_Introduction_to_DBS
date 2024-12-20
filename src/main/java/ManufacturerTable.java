import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ManufacturerTable extends Table {
    public static final int COLUMNS = 4;
    private static String tableIdentifier = "manufacturer";

    public ManufacturerTable(String tableName) {
        super(tableName);
        tableIdentifier = tableName;
    }

    public ManufacturerTable() {
        super(tableIdentifier);
    }

    @Override
    public void createTable() throws SQLException {
        deleteTable();
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE " + getTableName() + " (mID INTEGER PRIMARY KEY, mName VARCHAR(20) NOT NULL, mAddress VARCHAR(50) NOT NULL, mPhoneNumber INTEGER NOT NULL)");
    }

    @Override
    public void loadTable() throws SQLException {
        conn.setAutoCommit(false);
        super.loadTable();

        try (BufferedReader reader = new BufferedReader(new FileReader(Table.getSourceDir() + "manufacturer.txt"))) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + getTableName() + " VALUES (?, ?, ?, ?)");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                stmt.setInt(1, Integer.parseInt(data[0]));
                stmt.setString(2, data[1]);
                stmt.setString(3, data[2]);
                stmt.setInt(4, Integer.parseInt(data[3]));
                stmt.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (IOException e) {
            conn.rollback();
            conn.setAutoCommit(true);
            System.out.println("[Error] Failed to read data file: " + e.getMessage());
            System.out.println("Error File: " + e.getStackTrace()[0].getFileName());
        } catch (SQLException e) {
            conn.rollback();
            conn.setAutoCommit(true);
            System.out.println("[Error] Database error: " + e.getMessage());
            System.out.println("Error code: " + e.getErrorCode());
        }
    }

    public void queryManufacturerSales() {
        String sql = "SELECT m.mID as \"Manufacturer ID\", " +
                "m.mName as \"Manufacturer Name\", " +
                "COALESCE(SUM(p.pPrice), 0) as \"Total Sales Value\" " +
                "FROM " + getTableName() + " m " +
                "LEFT JOIN " + PartTable.getTableIdentifier() + " p ON m.mID = p.mID " +
                "LEFT JOIN " + TransactionTable.getTableIdentifier() + " t ON p.pID = t.pID " +
                "GROUP BY m.mID, m.mName " +
                "ORDER BY \"Total Sales Value\" DESC";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("| Manufacturer ID | Manufacturer Name | Total Sales Value |");
            while (rs.next()) {
                System.out.printf("| %d | %s | %d |\n",
                        rs.getInt("Manufacturer ID"),
                        rs.getString("Manufacturer Name"),
                        rs.getInt("Total Sales Value")
                );
            }
            System.out.println("End of Query");
        } catch (SQLException e) {
            System.out.println("Error showing table content: " + e.getMessage());
        }

    }

    public static String getTableIdentifier() {
        return tableIdentifier;
    }
}
