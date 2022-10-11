package com.chemistry;

import java.sql.*;

import static com.chemistry.ChemistryModelingMainWindow.currentUser;
import static com.chemistry.ExperimentChooseWindow.choosenExperiment;

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
        String select = "SELECT DISTINCT * FROM " + AllConstants.UserConsts.USERS_TABLE + " where " + AllConstants.UserConsts.EMAIL
                + "='" + email + "' and " + AllConstants.UserConsts.PASSWORD + "='" + password + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        if (rset.next()){
            currentUser.setExps_completed(rset.getString(AllConstants.UserConsts.EXPS_COMPLETED));
            currentUser.setCurrent_exp(rset.getString(AllConstants.UserConsts.CURRENT_EXP));
            currentUser.setFIO(rset.getString(AllConstants.UserConsts.FIO));
            currentUser.setPassword(rset.getString(AllConstants.UserConsts.PASSWORD));
            currentUser.setEmail(rset.getString(AllConstants.UserConsts.EMAIL));
            return true;
        } else return false;
    }

    public Integer checkAvailableExperiments() {
        int UserExperiment = Integer.parseInt(currentUser.getCurrent_exp());
        return UserExperiment;
    }

    public void setChoosenExperiment(String id) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.ExpConsts.EXP_TABLE + " where " + AllConstants.ExpConsts.EXP_ID
                + "='" + id + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();

        if (rset.next()){
            choosenExperiment.setExp_id(rset.getString(AllConstants.ExpConsts.EXP_ID));
            choosenExperiment.setName(rset.getString(AllConstants.ExpConsts.NAME));
            choosenExperiment.setTexture_path(rset.getString(AllConstants.ExpConsts.TEXTURE_PATH));
        }
    }

    public ResultSet getUsingSubstancesIDs(String exp_id) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.SubsExpConsts.SUBS_EXP_TABLE + " where " +
                AllConstants.SubsExpConsts.EXP_ID + "='" + exp_id + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();

        return rset;
    }

    public ResultSet getSubstanceByID(String id) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.SubsConsts.SUBS_TABLE + " where " +
                AllConstants.SubsConsts.ID + "='" + id + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        return rset;
    }

    public ResultSet getUsingEquipmentIDs(String exp_id) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.EquipExpConsts.EQUIP_EXP_TABLE + " where " +
                AllConstants.EquipExpConsts.EXP4_ID + "='" + exp_id + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        return rset;
    }

    public ResultSet getEquipmentByID(String id) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.EquipConsts.EQUIP_TABLE + " where " +
                AllConstants.EquipConsts.ID + "='" + id + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        return rset;
    }

    public ResultSet getFoundationByName(String foundation) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.FoundConsts.FOUND_TABLE + " where " +
                AllConstants.FoundConsts.FOUNDATION_NAME + "='" + foundation + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        return rset;
    }

    public ResultSet getOxidByName(String oxid) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.OxidConsts.OXID_TABLE + " where " +
                AllConstants.OxidConsts.OXIDIZER_NAME + "='" + oxid + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        return rset;
    }
}
