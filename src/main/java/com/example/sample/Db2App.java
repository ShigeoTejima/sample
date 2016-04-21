package com.example.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Db2App {

    public static void main(String[] args) {
        new Db2App().run();
    }

    void run() {
//        String url = "jdbc:db2://localhost:60001/sample";
        String url = "jdbc:db2:sample";
        /*
        String user = "tejima_shigeo";
        String password = "teshijige3224";
        */
        try (Connection connection = DriverManager.getConnection(url);) {
            System.out.println("connected.");

            /*
            try (Statement statement = connection.createStatement()) {
                String sql = "select * from foo where name = 'John'";
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        Integer id = resultSet.getInt(1);
                        String name = resultSet.getString(2);
                        System.out.println(String.format("id: %s, name: %s", id, name));
                    }
                }
            }
            */

            String sql = "select id, name from foo where name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, "RINGOOOOOOOOOOOOO");
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Integer id = resultSet.getInt(1);
                        String name = resultSet.getString(2);
                        System.out.println(String.format("id: %s, name: %s", id, name));
                    }
                }
            }

            /*
            String sql = "update foo set name = concat(name, 'x') where name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, "RINGOOOOOOOOOOOOO");
                int count = preparedStatement.executeUpdate();
                System.out.println(String.format("updated count: %s", count));
            }
                    */

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
