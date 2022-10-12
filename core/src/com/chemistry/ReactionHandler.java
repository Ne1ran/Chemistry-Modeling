package com.chemistry;

import com.badlogic.gdx.graphics.Texture;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReactionHandler {
    ArrayList<Substance> substances;
    Map<Foundation, Integer> foundPool;
    Map<Oxid, Integer> oxidPool;
    DBHandler handler = new DBHandler();

    public void getSubstancesFromEquipment(Equipment equipment) throws SQLException, ClassNotFoundException {
        substances = new ArrayList<>();
        foundPool = new LinkedHashMap<>();
        oxidPool = new LinkedHashMap<>();
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
            String foundation = subs.getFoundation();
            String oxid = subs.getOxid();

            Integer found_amount = Integer.valueOf(subs.getFound_amount());
            Integer oxid_amount = Integer.valueOf(subs.getOxid_amount());

            ResultSet foundationFound = handler.getFoundationByName(foundation); // Putting a foundation with its amount in its pool
            if (foundationFound.next()){
                Foundation tempFoundation = new Foundation();
                tempFoundation.setFoundation_name(foundationFound.getString(AllConstants.FoundConsts.FOUNDATION_NAME));
                tempFoundation.setName(foundationFound.getString(AllConstants.FoundConsts.NAME));
                tempFoundation.setFound_state_min(foundationFound.getString(AllConstants.FoundConsts.FOUND_STATE_MIN));
                tempFoundation.setFound_state_max(foundationFound.getString(AllConstants.FoundConsts.FOUND_STATE_MAX));
                tempFoundation.setElectrochem_pos(foundationFound.getString(AllConstants.FoundConsts.ELECTROCHEM_POSITION));

                foundPool.put(tempFoundation, found_amount);
            }

            ResultSet oxidFound = handler.getOxidByName(oxid); // Putting a oxidizer with its amount in its pool
            if (oxidFound.next()){
                Oxid tempOxid = new Oxid();

                tempOxid.setOxid_name(oxidFound.getString(AllConstants.OxidConsts.OXIDIZER_NAME));
                tempOxid.setName(oxidFound.getString(AllConstants.OxidConsts.NAME));
                tempOxid.setOxid_state_min(oxidFound.getString(AllConstants.OxidConsts.OXID_STATE_MIN));
                tempOxid.setOxid_state_max(oxidFound.getString(AllConstants.OxidConsts.OXID_STATE_MAX));

                oxidPool.put(tempOxid, oxid_amount);
            }

        }
        chemicalReaction(foundPool, oxidPool);
    }

    public void chemicalReaction(Map<Foundation, Integer> foundPool, Map<Oxid, Integer> oxidPool){
        ArrayList<Foundation> foundationsFirstIteration = new ArrayList<>();
//        ArrayList<Foundation> foundationsSecondIteration = new ArrayList<>();
        Map<Foundation, Integer> tempFoundPool = new HashMap<>();
        boolean startReaction = false;
        for (Foundation found : foundPool.keySet()){
            foundationsFirstIteration.add(found);
            System.out.println(found.getName());
        }
        int first = Integer.parseInt(foundationsFirstIteration.get(0).getElectrochem_pos());
        int second = Integer.parseInt(foundationsFirstIteration.get(1).getElectrochem_pos());
        if (second <= 8 && first <= 8 && (first - second > 4 || second - first > 4)){
            startReaction = true;
        } else if (first - second > 2 || second - first > 2){
            startReaction = true;
        }

//        for (Foundation found : foundationsSecondIteration){
//            tempFoundPool.put(found, foundPool.get(found));
//            System.out.println(found.getName());
//        }

        if (startReaction){
            reactionStarted(foundPool, oxidPool);
        } else System.out.println("Reaction failed...");
    }

    public void reactionStarted(Map<Foundation, Integer> foundPool, Map<Oxid, Integer> oxidPool){
        String answer = "";
        for (Foundation foundation : foundPool.keySet()){
            if (foundPool.get(foundation) <= 1){
                answer += "";
            } else answer += String.valueOf(foundPool.get(foundation));
            answer += foundation.getFoundation_name();
            answer += " ";
        }
        System.out.println(answer);
        String[] tempArray = answer.trim().split(" ");
        int i = tempArray.length-1;
        for (Oxid oxid : oxidPool.keySet()){
            tempArray[i] = tempArray[i] + oxid.getOxid_name();
            i--;
        }
        System.out.println(String.join(" + ", tempArray));
    }
}

