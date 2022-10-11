package com.chemistry;

import com.badlogic.gdx.graphics.Texture;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReactionHandler {
    ArrayList<Substance> substances;
    Map<Foundation, Integer> foundPool;
    Map<Oxid, Integer> oxidPool;
    DBHandler handler = new DBHandler();

    public void getSubstancesFromEquipment(Equipment equipment) throws SQLException, ClassNotFoundException {
        substances = new ArrayList<>();
        foundPool = new HashMap<>();
        oxidPool = new HashMap<>();
        for (String substanceId : equipment.getSubstancesInside()){
            Substance tempSubstance = new Substance();
            ResultSet substanceFromDB = handler.getSubstanceByID(substanceId);
            if (substanceFromDB.next()){
                tempSubstance.setSubId(substanceFromDB.getString(AllConstants.SubsConsts.ID));
                tempSubstance.setTexture_path(new Texture(substanceFromDB.getString(AllConstants.SubsConsts.TEXTURE_PATH)));
                tempSubstance.setName(substanceFromDB.getString(AllConstants.SubsConsts.NAME));
                tempSubstance.setFoundation(substanceFromDB.getString(AllConstants.SubsConsts.FOUND_PART_NAME));
                tempSubstance.setOxid(substanceFromDB.getString(AllConstants.SubsConsts.OXID_PART_NAME));
                tempSubstance.setSmallTexturePath(new Texture(substanceFromDB.getString(AllConstants.SubsConsts.SMALL_TEXTURE)));
                tempSubstance.setFound_amount(substanceFromDB.getString(AllConstants.SubsConsts.FOUND_AMOUNT));
                tempSubstance.setOxid_amount(substanceFromDB.getString(AllConstants.SubsConsts.OXID_AMOUNT));
            }
            substances.add(tempSubstance);
        }
        experimentPoolSetting(substances);
    }

    public void experimentPoolSetting(ArrayList<Substance> substances) throws SQLException, ClassNotFoundException {
        for (Substance subs : substances){
            System.out.println(subs.getName());
            String foundation = subs.getFoundation();
            String oxid = subs.getOxid();

            Integer found_amount = Integer.valueOf(subs.getFound_amount());
            Integer oxid_amount = Integer.valueOf(subs.getOxid_amount());

            ResultSet foundationFound = handler.getFoundationByName(foundation); // Putting a foundation with its amount in a pool
            if (foundationFound.next()){
                Foundation tempFoundation = new Foundation();
                tempFoundation.setFoundation_name(foundationFound.getString(AllConstants.FoundConsts.FOUNDATION_NAME));
                tempFoundation.setName(foundationFound.getString(AllConstants.FoundConsts.NAME));
                tempFoundation.setFound_state_min(foundationFound.getString(AllConstants.FoundConsts.FOUND_STATE_MIN));
                tempFoundation.setFound_state_max(foundationFound.getString(AllConstants.FoundConsts.FOUND_STATE_MAX));
                tempFoundation.setElectrochem_pos(foundationFound.getString(AllConstants.FoundConsts.ELECTROCHEM_POSITION));

                System.out.println(tempFoundation.getName() + " - колво " + found_amount);
                foundPool.put(tempFoundation, found_amount);
            }

            ResultSet oxidFound = handler.getOxidByName(oxid);
            if (oxidFound.next()){
                Oxid tempOxid = new Oxid();

                tempOxid.setOxid_name(oxidFound.getString(AllConstants.OxidConsts.OXIDIZER_NAME));
                tempOxid.setName(oxidFound.getString(AllConstants.OxidConsts.NAME));
                tempOxid.setOxid_state_min(oxidFound.getString(AllConstants.OxidConsts.OXID_STATE_MIN));
                tempOxid.setOxid_state_max(oxidFound.getString(AllConstants.OxidConsts.OXID_STATE_MAX));

                System.out.println(tempOxid.getName() + " - количво " + oxid_amount);
                oxidPool.put(tempOxid, oxid_amount);
            }

        }

    }

    public void chemicalReaction(Map<String, Integer> pool){

    }
}

