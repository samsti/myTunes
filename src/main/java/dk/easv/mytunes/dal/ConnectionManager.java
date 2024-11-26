package dk.easv.mytunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.mytunes.exceptions.DBException;

import java.sql.Connection;

public class ConnectionManager {
    private final SQLServerDataSource ds;

    public ConnectionManager() throws DBException
    {
        ds = new SQLServerDataSource();
        ds.setServerName("EASV-DB4");
        ds.setDatabaseName("iTunesGroup6");
        ds.setPortNumber(1433);
        ds.setUser("CSe2024b_e_26");
        ds.setPassword("CSe2024bE26!24");
        ds.setTrustServerCertificate(true);
    }

    public Connection getConnection() throws SQLServerException
    {
        return ds.getConnection();
    }

}