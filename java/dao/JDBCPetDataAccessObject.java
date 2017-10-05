package dao;

import pets.enums.Breed;
import pets.enums.PetEvent;
import pets.enums.PetSize;
import pets.enums.Sex;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class JDBCPetDataAccessObject implements PetDataAccessObject{

    private Properties connInfo;
    private String dataBaseUrl = "jdbc:sqlserver://pettelegrambot.database.windows.net:1433;database=PetTelegramBot";
    private String userName = "newadmin";
    private String password = "Topi1996";
    private Connection connection = null;

    public JDBCPetDataAccessObject() {
        connInfo = new Properties();
        connInfo.put("user", userName);
        connInfo.put("password", password);
        connInfo.put("useUnicode", "true"); // (1)
        connInfo.put("charSet", "UTF8");
    }

    public void create(String [] petData, String petKind, String chatId){

        try {
            connection = DriverManager.getConnection(dataBaseUrl, connInfo);
            String query = "INSERT INTO Events (UID, User, Name, Pet, Breed, Sex, Size, Color, Address, Event, Date, Path) VALUES (UUID(), '" + chatId + "', '" + petData[0] + "', '" + petKind + "', '" + Breed.valueOf(petData[1].toUpperCase()) + "', '" + Sex.valueOf(petData[2].toUpperCase()) + "', '" + petData[3] + "', '" + PetSize.valueOf(petData[4].toUpperCase()) + "', '" + petData[5] + "', '" + PetEvent.valueOf(petData[6]) + "', '" + new Date() + "', '" + petData[7] + "')";
            Statement statement = connection.createStatement();
            statement.execute("SET NAMES utf8");
            statement.execute("SET collation_connection='utf8_general_ci'");
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<List<String>> selectAll(){

        List<String> names = new ArrayList<>();
        List<String> breeds = new ArrayList<>();
        List<String> pets = new ArrayList<>();
        List<String> sexes = new ArrayList<>();
        List<String> sizes = new ArrayList<>();
        List<String> colors = new ArrayList<>();
        List<String> addresses = new ArrayList<>();
        List<String> events = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        Connection connection = null;

        Statement stmt = null;

        try {
            connection = DriverManager.getConnection(dataBaseUrl, userName, password);
            String query = "SELECT Name, Pet, Breed, Sex, Size, Color, Address, Event, Date, COUNT(*) FROM Events GROUP BY UID";
            stmt = connection.createStatement();
            ResultSet rs4 = stmt.executeQuery(query);

            while (rs4.next()) {
                names.add(rs4.getString(1));
                pets.add(rs4.getString(2));
                breeds.add(rs4.getString(3));
                sexes.add(rs4.getString(4));
                sizes.add(rs4.getString(5));
                colors.add(rs4.getString(6));
                addresses.add(rs4.getString(7));
                events.add(rs4.getString(8));
                dates.add(rs4.getString(9));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        List<List<String>> resultList = new ArrayList<>();
        resultList.add(names);
        resultList.add(pets);
        resultList.add(breeds);
        resultList.add(sexes);
        resultList.add(sizes);
        resultList.add(colors);
        resultList.add(addresses);
        resultList.add(events);
        resultList.add(dates);
        return resultList;
    }

    public List<List<String>> selectById(String chatId){

        List<String> names = new ArrayList<>();
        List<String> breeds = new ArrayList<>();
        List<String> pets = new ArrayList<>();
        List<String> sexes = new ArrayList<>();
        List<String> sizes = new ArrayList<>();
        List<String> colors = new ArrayList<>();
        List<String> addresses = new ArrayList<>();
        List<String> events = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dataBaseUrl, userName, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String query = "SELECT Name, Pet, Breed, Sex, Size, Color, Address, Event, Date, COUNT(*) FROM Events WHERE User=" + chatId + " GROUP BY UID";

        Statement stmt = null;

        try {
            stmt = connection.createStatement();
            ResultSet rs4 = stmt.executeQuery(query);

            while (rs4.next()) {
                names.add(rs4.getString(1));
                pets.add(rs4.getString(2));
                breeds.add(rs4.getString(3));
                sexes.add(rs4.getString(4));
                sizes.add(rs4.getString(5));
                colors.add(rs4.getString(6));
                addresses.add(rs4.getString(7));
                events.add(rs4.getString(8));
                dates.add(rs4.getString(9));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        List<List<String>> resultList = new ArrayList<>();
        resultList.add(names);
        resultList.add(pets);
        resultList.add(breeds);
        resultList.add(sexes);
        resultList.add(sizes);
        resultList.add(colors);
        resultList.add(addresses);
        resultList.add(events);
        resultList.add(dates);
        return resultList;
    }

    public void remove(String addID) {
        try {
            connection = DriverManager.getConnection(dataBaseUrl, connInfo);
            String query = "DELETE FROM Events WHERE UID='" + addID + "'";
            Statement statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
