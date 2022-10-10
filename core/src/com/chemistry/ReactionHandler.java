package com.chemistry;

import com.badlogic.gdx.graphics.Texture;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReactionHandler {
    ArrayList<Substance> substances;
    DBHandler handler = new DBHandler();

    public void getSubstancesFromEquipment(Equipment equipment) throws SQLException, ClassNotFoundException {
        substances = new ArrayList<>();
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
            }
            substances.add(tempSubstance);
        }
        experimentPoolSetting(substances);
    }

    public void experimentPoolSetting(ArrayList<Substance> substances){
        Map<String, Integer> pool = new HashMap<>();
        for (Substance subs : substances){
            String foundation = subs.getFoundation();
            String oxid = subs.getOxid();

            if (oxid.charAt(0) > 1 && oxid.charAt(0) <= 9) {
                int tempCount = 0;
                tempCount = (int) oxid.charAt(0);
                oxid = oxid.substring(1,oxid.length());
                System.out.println(oxid);
                pool.put(oxid, tempCount);
            } else pool.put(oxid, 1);

            if (foundation.charAt(0) > 1 && foundation.charAt(0) <= 9) {
                int tempCount = 0;
                tempCount = (int) foundation.charAt(0);
                foundation = foundation.substring(1,foundation.length());
                System.out.println(foundation);
                pool.put(foundation, tempCount);
            } else pool.put(foundation, 1);
        }

    }

    public void chemicalReaction(Map<String, Integer> pool){

    }
}

