package connectivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionClass {
    private Connection connection;

    public Connection getConnection() {
        String dbName = "restaurant_db";
        String userName = "root";
        String password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName, userName, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    public static void createTables() {
        Connection connection = new ConnectionClass().getConnection();
        try {
            Statement statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users(name VARCHAR(100), password VARCHAR(100), position VARCHAR(100));";
            statement.execute(sql);
            //sql = "Drop Table dishes;";
            //statement.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS dishes(nameDish VARCHAR(100), price VARCHAR(100) , category VARCHAR(100));";
            statement.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS dailyEvents(worker VARCHAR(100), calendarDate VARCHAR(100), description VARCHAR(100), startH DOUBLE, endH DOUBLE);";
            statement.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS orders(numberOrder VARCHAR(100), date VARCHAR(100), numberPeople VARCHAR(100), time VARCHAR(100), tableNum VARCHAR(100));";
            statement.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS dishOrder(numberOrder VARCHAR(100), name VARCHAR(100), price VARCHAR(100), category VARCHAR(100));";
            statement.execute(sql);

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createDB() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password=");
            Statement statement = connection.createStatement();
            statement.execute("CREATE DATABASE IF NOT EXISTS restaurant_db");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}