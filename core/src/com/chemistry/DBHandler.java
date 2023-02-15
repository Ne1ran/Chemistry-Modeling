package com.chemistry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.chemistry.dto.*;

import java.sql.*;
import java.util.ArrayList;

import static com.chemistry.ChemistryModelingMainWindow.currentUser;
import static com.chemistry.CustomExperimentWindow.createInGameNameForSubstance;
import static com.chemistry.ExperimentChooseWindow.choosenExperiment;

public class DBHandler extends Config{
    Connection connection;

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        String connStr = "jdbc:mysql://"+ Host + ":" + Port + "/" + Name;
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(connStr, User, Password);
        return connection;
    }

    public void addNewUser(com.chemistry.dto.User user) throws SQLException, ClassNotFoundException {
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

    public Foundation getFoundationByName(String foundation) throws SQLException, ClassNotFoundException {
        Foundation foundation1 = new Foundation();

        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.FoundConsts.FOUND_TABLE + " where " +
                AllConstants.FoundConsts.FOUNDATION_NAME + "='" + foundation + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();

        if (rset.next()){
            foundation1.setFoundation_name(rset.getString(AllConstants.FoundConsts.FOUNDATION_NAME));
            foundation1.setName(rset.getString(AllConstants.FoundConsts.NAME));
            foundation1.setElectrochem_pos(rset.getString(AllConstants.FoundConsts.ELECTROCHEM_POSITION));
            foundation1.setPossible_states(rset.getString(AllConstants.FoundConsts.POSSIBLE_STATES));
        }

        return foundation1;
    }

    public Oxid getOxidByName(String oxid) throws SQLException, ClassNotFoundException {
        Oxid oxid1 = new Oxid();

        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.OxidConsts.OXID_TABLE + " where " +
                AllConstants.OxidConsts.OXIDIZER_NAME + "='" + oxid + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();

        if (rset.next()){
            oxid1.setOxid_name(rset.getString(AllConstants.OxidConsts.OXIDIZER_NAME));
            oxid1.setName(rset.getString(AllConstants.OxidConsts.NAME));
            oxid1.setOxid_strength(rset.getString(AllConstants.OxidConsts.OXID_STRENGTH));
            oxid1.setPossible_states(rset.getString(AllConstants.OxidConsts.POSSIBLE_STATES));
        }

        return oxid1;
    }

    public String getExperimentNameById(int id) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT name FROM " + AllConstants.ExpConsts.EXP_TABLE + " where " +
                AllConstants.ExpConsts.EXP_ID + "='" + id + "'";
        PreparedStatement preparedStatement = getConnection().prepareStatement(select);
        rset = preparedStatement.executeQuery();
        if (rset.next()){
            return rset.getString(AllConstants.ExpConsts.NAME);
        }
        return "Name not detected...";
    }

    public ArrayList<Substance> getAllSubstances() throws SQLException, ClassNotFoundException {
        ArrayList<Substance> substancesNames = new ArrayList<>();
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.SubsConsts.SUBS_TABLE;
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        while (rset.next()){
            Substance tempSubstance = new Substance();
            tempSubstance.setSubId(rset.getString(AllConstants.SubsConsts.ID));
            tempSubstance.setName(rset.getString(AllConstants.SubsConsts.NAME));
            tempSubstance.setFoundation(rset.getString(AllConstants.SubsConsts.FOUND_PART_NAME));
            tempSubstance.setOxid(rset.getString(AllConstants.SubsConsts.OXID_PART_NAME));
            tempSubstance.setFound_amount(rset.getString(AllConstants.SubsConsts.FOUND_AMOUNT));
            tempSubstance.setOxid_amount(rset.getString(AllConstants.SubsConsts.OXID_AMOUNT));

            tempSubstance.setSubstanceNameInGame(createInGameNameForSubstance(tempSubstance.getFoundation()
                    + "-"  + tempSubstance.getFound_amount() + " "
                    + tempSubstance.getOxid() + "-" + tempSubstance.getOxid_amount()));

            substancesNames.add(tempSubstance);
        }
        return substancesNames;
    }

    public ResultSet getSubstanceByIDInSubsExpsTable(String subsId) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.SubsExpConsts.SUBS_EXP_TABLE + " Where "
                + AllConstants.SubsExpConsts.SUBS_EXP_ID + " ='" + subsId + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        return rset;
    }

    public ResultSet getSubstanceByIDInSubsExpsTableForExpWindow(String subsId, String expId) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.SubsExpConsts.SUBS_EXP_TABLE + " Where "
                + AllConstants.SubsExpConsts.SUBS_EXP_ID + " ='" + subsId + "'"
                + "AND " + AllConstants.SubsExpConsts.EXP_ID + " ='" + expId + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        return rset;
    }

    public ResultSet getEquipmentByIDInEquipExpTable(String equipId) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.EquipExpConsts.EQUIP_EXP_TABLE + " Where "
                + AllConstants.EquipExpConsts.EQUIP_EXP_ID + " ='" + equipId + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        return rset;
    }

    public ResultSet getEquipmentByIDInEquipExpTableForExpWindow(String equipId, String expId) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.EquipExpConsts.EQUIP_EXP_TABLE + " Where "
                + AllConstants.EquipExpConsts.EQUIP_EXP_ID + " ='" + equipId + "'"
                + "AND " + AllConstants.EquipExpConsts.EXP4_ID + " ='" + expId + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        return rset;
    }

    public ArrayList<String> getAllEquipmentsNames() throws SQLException, ClassNotFoundException {
        ArrayList<String> equipments = new ArrayList<>();
        ResultSet rset = null;
        String select = "SELECT name FROM " + AllConstants.EquipConsts.EQUIP_TABLE;
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        while (rset.next()){
            equipments.add(rset.getString(1));
        }
        return equipments;
    }

    public ResultSet findEquipmentByItsName(String equipName) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.EquipConsts.EQUIP_TABLE + " Where "
                + AllConstants.EquipConsts.NAME + " ='" + equipName + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        return rset;
    }

    public String saveNewExperiment(Experiment thisExperiment) throws SQLException, ClassNotFoundException {
        String insert = "INSERT INTO " + AllConstants.ExpConsts.EXP_TABLE + "(" + AllConstants.ExpConsts.NAME +
                ',' + AllConstants.ExpConsts.TEXTURE_PATH +
                ',' + AllConstants.ExpConsts.CREATOR + ')' + "VALUES(?,?,?)";
        PreparedStatement prst = getConnection().prepareStatement(insert);
        prst.setString(1, thisExperiment.getName());
        prst.setString(2, thisExperiment.getTexture_path());
        prst.setString(3, currentUser.getFIO());
        prst.executeUpdate();

        ResultSet rset = null;
        String select = "SELECT exp_id FROM " + AllConstants.ExpConsts.EXP_TABLE + " Where "
                + AllConstants.ExpConsts.NAME + " ='" + thisExperiment.getName() + "'"
                + "AND " + AllConstants.ExpConsts.CREATOR + " ='" + currentUser.getFIO() + "'" ;
        PreparedStatement prst2 = getConnection().prepareStatement(select);
        rset = prst2.executeQuery();

        if (rset.next()){
            return rset.getString(1);
        }
        return "";
    }

    public int findSystemExperiments() throws SQLException, ClassNotFoundException {
        int num = 0;
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.ExpConsts.EXP_TABLE + " Where "
                + AllConstants.ExpConsts.CREATOR + " ='SYSTEM'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        while (rset.next()){
            num++;
        }
        return num;
    }

    public ArrayList<Experiment> getAllSystemExperiments() throws SQLException, ClassNotFoundException {
        ArrayList<Experiment> experiments = new ArrayList<>();
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.ExpConsts.EXP_TABLE + " Where "
                + AllConstants.ExpConsts.CREATOR + " ='SYSTEM'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        while (rset.next()){
            Experiment tempExp = new Experiment();
            tempExp.setExp_id(rset.getString(AllConstants.ExpConsts.EXP_ID));
            tempExp.setName(rset.getString(AllConstants.ExpConsts.NAME));
            experiments.add(tempExp);
        }
        return experiments;
    }
    public ArrayList<Experiment> getAllCustomExperiments(String fio) throws SQLException, ClassNotFoundException {
        ArrayList<Experiment> experiments = new ArrayList<>();
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.ExpConsts.EXP_TABLE + " Where "
                + AllConstants.ExpConsts.CREATOR + " ='" + fio + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        while (rset.next()){
            Experiment tempExp = new Experiment();
            tempExp.setExp_id(rset.getString(AllConstants.ExpConsts.EXP_ID));
            tempExp.setName(rset.getString(AllConstants.ExpConsts.NAME));
            experiments.add(tempExp);
        }
        return experiments;
    }
    public int findCustomExperimentsOfThisUser(String fio) throws SQLException, ClassNotFoundException {
        int num = 0;
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.ExpConsts.EXP_TABLE + " Where "
                + AllConstants.ExpConsts.CREATOR + " ='" + fio + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        while (rset.next()){
            num++;
        }
        return num;
    }

    public void saveSubstances(Array<Substance> substancesPlaced, String expId) throws SQLException, ClassNotFoundException {
        for (Substance substance : substancesPlaced) {
            String insert = "INSERT INTO " + AllConstants.SubsExpConsts.SUBS_EXP_TABLE + "("
                    + AllConstants.SubsExpConsts.SUBS_EXP_ID + ','
                    + AllConstants.SubsExpConsts.EXP_ID + ','
                    + AllConstants.SubsExpConsts.SUBS_X + ','
                    + AllConstants.SubsExpConsts.SUBS_Y + ')'
                    + "VALUES(?,?,?,?)";
            PreparedStatement prst = getConnection().prepareStatement(insert);
            prst.setString(1, substance.getSubId());
            prst.setString(2, expId);
            prst.setString(3, String.valueOf(substance.getX()));
            prst.setString(4, String.valueOf(720-substance.getY()-substance.getHeight()));
            prst.executeUpdate();
        }
    }

    public void saveEquipment(Array<Equipment> equipmentPlaced, String expId) throws SQLException, ClassNotFoundException {
        for (Equipment equipment : equipmentPlaced) {
            String insert = "INSERT INTO " + AllConstants.EquipExpConsts.EQUIP_EXP_TABLE + "("
                    + AllConstants.EquipExpConsts.EQUIP_EXP_ID + ','
                    + AllConstants.EquipExpConsts.EXP4_ID + ','
                    + AllConstants.EquipExpConsts.EQUIP_X + ','
                    + AllConstants.EquipExpConsts.EQUIP_Y + ')'
                    + "VALUES(?,?,?,?)";
            PreparedStatement prst = getConnection().prepareStatement(insert);
            prst.setString(1, equipment.getId());
            prst.setString(2, expId);
            prst.setString(3, String.valueOf(equipment.getX()));
            prst.setString(4, String.valueOf(720-equipment.getY()-equipment.getHeight()));
            prst.executeUpdate();
        }
    }

    public String getFoundationStrengthByName(String name) throws SQLException, ClassNotFoundException {
        String strength = "0";
        ResultSet rset = null;
        String select = "SELECT electrochem_position FROM " + AllConstants.FoundConsts.FOUND_TABLE + " Where "
                + AllConstants.FoundConsts.FOUNDATION_NAME + " ='" + name + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();

        if (rset.next()){
            strength = rset.getString(1);
        }

        return strength;

    }

    public String getOxidStrengthByName(String name) throws SQLException, ClassNotFoundException {
        String strength = "0";
        ResultSet rset = null;
        String select = "SELECT oxid_strength FROM " + AllConstants.OxidConsts.OXID_TABLE + " Where "
                + AllConstants.OxidConsts.OXIDIZER_NAME + " ='" + name + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();

        if (rset.next()){
            strength = rset.getString(1);
        }

        return strength;

    }

    public ResultSet findSubstanceById(String subId) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT * FROM " + AllConstants.SubsConsts.SUBS_TABLE + " WHERE " +
                AllConstants.SubsConsts.ID + " ='" + subId + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        return rset;
    }

    public boolean getIsFoundationSimple(String elementName) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT isSimple FROM " + AllConstants.FoundConsts.FOUND_TABLE + " WHERE " +
                AllConstants.FoundConsts.FOUNDATION_NAME + " ='" + elementName + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();

        if (rset.next()){
            return rset.getString(1).equals("1");
        }

        return false;
    }

    public boolean getIsOxidizerSimple(String elementName) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT isSimple FROM " + AllConstants.OxidConsts.OXID_TABLE + " WHERE " +
                AllConstants.OxidConsts.OXIDIZER_NAME + " ='" + elementName + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();

        if (rset.next()){
            return rset.getString(1).equals("1");
        }

        return false;
    }

    public boolean CheckIfReactionPossible(Array<String> tempArrayFirstSubstance) throws SQLException, ClassNotFoundException {
        Boolean check = false;
        ResultSet rset = null;
        String select = "SELECT checkToStartReaction FROM " + AllConstants.SubsConsts.SUBS_TABLE +
                " WHERE " + AllConstants.SubsConsts.FOUND_PART_NAME + " ='" + tempArrayFirstSubstance.get(0)
                + "' AND " + AllConstants.SubsConsts.OXID_PART_NAME + " ='" + tempArrayFirstSubstance.get(1) + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();

        if (rset.next()){
            if (rset.getString(1).equals("1")){
                check = true;
            }
        } else check = true;
        return check;
    }

    public String getDissotiationReaction(Array<String> substance) throws SQLException, ClassNotFoundException {
        String reaction = "";
        ResultSet rset = null;
        String select = "SELECT dissotiation_reaction FROM " + AllConstants.SubsConsts.SUBS_TABLE + " Where "
                + AllConstants.SubsConsts.FOUND_PART_NAME + " ='" + substance.get(0) + "' AND " +
                AllConstants.SubsConsts.OXID_PART_NAME + " ='" + substance.get(1) + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();

        if (rset.next()){
            reaction = rset.getString(1);
        }

        return reaction;
    }

    public String getSubstanceType(String foundationName, String oxidName) throws SQLException, ClassNotFoundException {
        ResultSet rset = null;
        String select = "SELECT substance_type FROM " + AllConstants.SubsConsts.SUBS_TABLE + " Where "
                + AllConstants.SubsConsts.FOUND_PART_NAME + " ='" + foundationName + "' AND " +
                AllConstants.SubsConsts.OXID_PART_NAME + " ='" + oxidName + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        rset = prst.executeQuery();
        if (rset.next()){
            return rset.getString(1);
        } else return "";
    }

    public int getUnstableTypeById(String subId) throws SQLException, ClassNotFoundException {
        String OVR_type = "0";

        String select = "SELECT unstable_type FROM " + AllConstants.SubsConsts.SUBS_TABLE + " Where "
                + AllConstants.SubsConsts.ID + " ='" + subId + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        ResultSet rset = prst.executeQuery();
        if (rset.next()){
            OVR_type = rset.getString(1);
        }

        return Integer.parseInt(OVR_type);
    }

    public int getOxidStateByName(String oxid) throws SQLException, ClassNotFoundException {
        int i = -1;

        String select = "SELECT possible_states FROM " + AllConstants.OxidConsts.OXID_TABLE + " Where "
                + AllConstants.OxidConsts.OXIDIZER_NAME + " ='" + oxid + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        ResultSet rset = prst.executeQuery();
        try {
            if (rset.next()) {
                i = Integer.parseInt(rset.getString(1));
            }
        } catch (Exception e){
            return i;
        }

        return i;
    }

    public int getFoundationStateByName(String foundation) throws SQLException, ClassNotFoundException {
        int i = 1;

        String select = "SELECT possible_states FROM " + AllConstants.FoundConsts.FOUND_TABLE + " Where "
                + AllConstants.FoundConsts.FOUNDATION_NAME + " ='" + foundation + "'";
        PreparedStatement prst = getConnection().prepareStatement(select);
        ResultSet rset = prst.executeQuery();
        try {
            if (rset.next()) {
                i = Integer.parseInt(rset.getString(1));
            }
        } catch (Exception e){
            return i;
        }

        return i;
    }
}


