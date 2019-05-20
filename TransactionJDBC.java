import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class TransactionJDBC {
    //Создание объекта пула соединений
    PoolConnections newPoolConnections = new PoolConnections();
    Connection conn;

    //Конструктор транзакции
    public TransactionJDBC() {
        try {
            conn = newPoolConnections.getConnectionFromPoolConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Метод создает транзакцию, в качестве параметра принимает лист SQL запросов
    public void beginTransaction(ArrayList<String> listSQL) {

        int n = 0;
        while (n < listSQL.size()) {
            try {
                conn.prepareStatement(listSQL.get(n)).executeUpdate();
            } catch (SQLException e) {
                System.err.print("Transaction is being rolled back");
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException e1) {
                        System.err.print("Transaction was not rolled back");
                    }
                }
            } finally {
                try {
                    conn.commit();
                } catch (SQLException e) {
                    System.err.print("Transaction was not commit");
                }
            }
        }
    }

    //Метод возвращает соединение в пул
    public void putConnection(Connection con) {
        try {
            newPoolConnections.putConnection(con);
        } catch (Exception e) {
            System.err.print("Connection was not closed");
        }
    }
}
