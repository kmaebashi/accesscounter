package com.kmaebashi.accesscounter.dbaccess;
import java.sql.*;
import javax.naming.*;
import javax.sql.*;

public class AccessCounterDbAccess {
    public static int getCount(String counterId) throws Exception {
        Context context = new InitialContext();
        DataSource ds = (DataSource)context.lookup("java:comp/env/jdbc/accesscounter");

        int current;
        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            current = getCurrentCount(conn, counterId);
            updateCounter(conn, counterId, current + 1);
            conn.commit();
        }

        return current;
    }

    private static int getCurrentCount(Connection conn, String counterId) throws Exception {
        final String sql = """
SELECT COUNTER FROM ACCESSCOUNTER
WHERE COUNTERID=?
FOR UPDATE
""";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, counterId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int ret = rs.getInt("COUNTER");

        return ret;
    }

    private static int updateCounter(Connection conn, String counterId, int nextCount)
        throws Exception {
        final String sql = """
UPDATE ACCESSCOUNTER SET
COUNTER = ?,
UPDATED = now()
WHERE COUNTERID=?
""";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, nextCount);
        ps.setString(2, counterId);
        int ret = ps.executeUpdate();

        return ret;
    }

    public static void main(String[] args) throws Exception {
        int counter = getCount("kmaebashi");
        System.out.println(counter);
    }
}
