import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MTransaction {
    Connection con;
    List<PreparedStatement> listPreparedStatement;

    public MTransaction() {
        try {
            con = new MConnection().getConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PreparedStatement> createMTransaction(int counsTransactions, ArrayList<String> listSQL) {
        listPreparedStatement = new ArrayList();
        int n = 0;
        while (n < counsTransactions) {
            try {
                listPreparedStatement.add(con.prepareStatement(listSQL.get(n)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            n++;
        }
        return listPreparedStatement;
    }

    public void doCoommit(List<PreparedStatement> listPreparedStatement) {
        int sizeListPreparedStatement = listPreparedStatement.size();
        int n = 0;
        while (n < sizeListPreparedStatement) {
            try {
                listPreparedStatement.get(n).executeUpdate();
                con.commit();

            } catch (SQLException e) {
                e.printStackTrace();
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
