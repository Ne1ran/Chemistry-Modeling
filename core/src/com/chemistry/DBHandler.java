package com.chemistry;

import java.sql.*;

public class DBHandler extends Config{
    Connection connection;

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        String connStr = "jdbc:mysql://"+ Host + ":" + Port + "/" + Name;
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(connStr, User, Password);
        return connection;
    }

    public void addNewUser(User user) throws SQLException, ClassNotFoundException {
        String insert = "INSERT INTO " + AllConstants.UserConsts.USERS_TABLE + "(" + AllConstants.UserConsts.FIO +
                ',' + AllConstants.UserConsts.CURRENT_EXP + ',' + AllConstants.UserConsts.EXPS_COMPLETED +
                ',' + AllConstants.UserConsts.EMAIL + ',' + AllConstants.UserConsts.PASSWORD + ')' + "VALUES(?,?,?,?,?)";
        PreparedStatement prst = getConnection().prepareStatement(insert);
        prst.setString(1, user.getFIO());
        prst.setString(2, user.getCurrent_exp());
        prst.setString(3, user.getExps_completed());
        prst.setString(4, user.getEmail());
        prst.setString(5, user.getPassword());
        prst.executeUpdate();
        System.out.println("Added user successfully!");
    }

    public boolean authorize(String email, String password) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.UserConsts.USERS_TABLE + " where " + AllConstants.UserConsts.EMAIL
                + "='" + email + "' and " + AllConstants.UserConsts.PASSWORD + "='" + password + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();

        return rset != null;
    }


}
