import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class PoolConnections {
    //Лист свободных подключений
    List<Conn> listFreeConnect = new ArrayList<>();
    //Лист занятых подключений
    List<Conn> listUseConnect = new ArrayList<>();
    //кол-во подключений
    int poolSize;
    Conn newconn;

    // Конструктор пула соединений с параметром кол-во необходимых соединений
    public PoolConnections(int numberCon) {
        for (int i = 0; i < numberCon; i++) {
            listFreeConnect.add(new Conn());
        }
    }

    // Конструктор пула соединений без параметров
    public PoolConnections() {
        for (int i = 0; i < 10; i++) {
            listFreeConnect.add(new Conn());
            System.out.println("Connection created number" +i);
        }
    }

    // Метод определяет кол-во свободных соединений
    public int numberFreeConnection() {
        poolSize = listFreeConnect.size();
        return poolSize;
    }

    private void checkOldConnection(){
        for (Conn conn: listFreeConnect) {
            if(System.currentTimeMillis()-conn.getTime()>10000){
                listFreeConnect.remove(conn);
            }
        }
    }

    // Метод получения соединений из пула доступных соединений
    public Connection getConnectionFromPoolConnection() {
        Connection newConnetion;
        Conn newConn;
        //checkOldConnection();
        //Проверяем не пуст лист доступных соединений
        if (listFreeConnect != null) {
            //Выбираем последние соединение в листе доступных соединений
            newConn = listFreeConnect.get(listFreeConnect.size() - 1);
            //Изменяем время последнего использования на текущее время
            newConn.setTime(System.currentTimeMillis());
            //Получаем соединение
            newConnetion = newConn.getConnection();
            listFreeConnect.remove(newConn);
        } else {
            //Если лист доступных соединений соединений пуст создаем новое
            System.out.println("No free connection");
            newConn = new Conn();
            newConnetion = newConn.getConnection();
        }
        listUseConnect.add(newConn);
        System.out.println("Connection add to listUseConnect");
        return newConnetion;
    }

    // Метод возврата соединений в пул доступных соединений
    public void putConnection(Connection conn) {
        if (conn != null) {
            //Определяем индекс возвращаемого объекта Conn в листе listUseConnect
            int i = listUseConnect.indexOf(conn);
            //Возвращаем объект Conn в лист listFreeConnect
            listFreeConnect.add(listUseConnect.get(i));
            System.out.println("Connection add to listFreeConnect");
            //Удаляем объект Conn из листа listUseConnect
            listUseConnect.remove(i);
        } else {
            throw new NullPointerException("No Connection");
        }
    }

    //Класс обертка над соединением
    class Conn {
        // Время последнего использования соединения
        private long connectionLastUsedTime;

        private Connection createConnection() throws Exception {
            // Подгрузка драйвера БД Derby
            Class.forName("org.postgresql.Driver").newInstance();
            // Получение соединения с БД
            return DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/nameDB", "postgres", "postgres");
        }

        //Метод для создания соединения с БД
        public Connection getConnection() {
            Connection con = null;
            try {
                con = createConnection();
                connectionLastUsedTime = System.currentTimeMillis();
                con.setAutoCommit(false);
            } catch (Exception e) {
                System.err.print("Connection was not created");
            }
            return con;
        }

        public void setTime(long time) {
            connectionLastUsedTime = time;
        }

        public long getTime() {
            return connectionLastUsedTime;
        }
    }
}
