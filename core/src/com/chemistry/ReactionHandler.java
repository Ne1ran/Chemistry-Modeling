package com.chemistry;

import com.badlogic.gdx.graphics.Texture;
import com.chemistry.dto.Equipment;
import com.chemistry.dto.Foundation;
import com.chemistry.dto.Oxid;
import com.chemistry.dto.Substance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.chemistry.ExperimentWindow.phrase;
import static com.chemistry.ExperimentWindow.usedEquipment;

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
                tempSubstance.setSmallTexturePath(substanceFromDB.getString(AllConstants.SubsConsts.SMALL_TEXTURE));
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
            } else if (oxid_amount == 0 || oxid.equals("0")){
                Oxid tempOxid = new Oxid();
                tempOxid.setOxid_name("");
                tempOxid.setOxid_state_min("1");
                oxid_amount=1;

                oxidPool.put(tempOxid, oxid_amount);
            }

        }
        chemicalReaction(foundPool, oxidPool);
    }

    public void chemicalReaction(Map<Foundation, Integer> foundPool, Map<Oxid, Integer> oxidPool){
        ArrayList<Foundation> foundationsFirstIteration = new ArrayList<>();
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

        if (startReaction){
            reactionStarted(foundPool, oxidPool);
        } else {
            phrase = "Реакция не пошла... Емкость очищена.";
            clearEquipment();
        }
    }

    public void reactionStarted(Map<Foundation, Integer> foundPool, Map<Oxid, Integer> oxidPool){ // We swap foundations for oxidizers
        String answer = "";

        ArrayList<Oxid> oxids = new ArrayList<>();

        for (Oxid oxid : oxidPool.keySet()){
            oxids.add(oxid);
        }

        ArrayList<Foundation> foundations = new ArrayList<>();

        for (Foundation found : foundPool.keySet()){
            foundations.add(found);
        }

        for (Foundation foundation : foundations){
            answer += foundation.getFoundation_name();
            answer += " ";
        }
        String[] tempArray = answer.trim().split(" ");
        System.out.println(answer);

        int i = tempArray.length-1;
        int firstOxidEval = oxidPool.get(oxids.get(i)) * Integer.parseInt(oxids.get(i).getOxid_state_min()); // params that are state * amount
        int secondOxidEval = oxidPool.get(oxids.get(i-1)) * Integer.parseInt(oxids.get(i-1).getOxid_state_min());
        if (firstOxidEval == secondOxidEval){
        } else if (firstOxidEval > secondOxidEval){ //We swap their amounts according to evals (2 with 1)
            int tempInt = (oxidPool.get(oxids.get(i)) * Integer.parseInt(oxids.get(i).getOxid_state_min()))
                    / Integer.parseInt(oxids.get(i-1).getOxid_state_min());
            oxidPool.replace(oxids.get(i), (oxidPool.get(oxids.get(i-1)) * Integer.parseInt(oxids.get(i-1).getOxid_state_min()))
                    / Integer.parseInt(oxids.get(i).getOxid_state_min()));
            oxidPool.replace(oxids.get(i-1), tempInt);
        } else { // 1 with 2 2Fe(Cl)6 nerf
            int tempInt = (oxidPool.get(oxids.get(i-1)) * Integer.parseInt(oxids.get(i-1).getOxid_state_min()))
                    / Integer.parseInt(oxids.get(i).getOxid_state_min());
            oxidPool.replace(oxids.get(i-1), (oxidPool.get(oxids.get(i)) * Integer.parseInt(oxids.get(i).getOxid_state_min()))
                    / Integer.parseInt(oxids.get(i-1).getOxid_state_min()));
            oxidPool.replace(oxids.get(i), tempInt);
        }

        System.out.println(foundPool.get(foundations.get(i-1)) / 2);
        System.out.println(foundPool.get(foundations.get(i)) / 2);

        if (oxidPool.get(oxids.get(i)) >= 6){ // nerf 2FeCl6 -> FeCl3
            if (foundPool.get(foundations.get(i-1)) % 2 == 0 && oxidPool.get(oxids.get(i)) % 2 == 0){
                foundPool.put(foundations.get(i-1), foundPool.get(foundations.get(i-1)) / 2);
                oxidPool.put(oxids.get(i),oxidPool.get(oxids.get(i)) / 2);
            }
        } else if (oxidPool.get(oxids.get(i-1)) >= 6){
            if (foundPool.get(foundations.get(i)) % 2 == 0 && oxidPool.get(oxids.get(i-1)) % 2 == 0){

                foundPool.put(foundations.get(i), foundPool.get(foundations.get(i)) / 2);
                oxidPool.put(oxids.get(i-1),oxidPool.get(oxids.get(i-1)) / 2);
            }
        }

        System.out.println(oxidPool.get(oxids.get(i)) + " - " + oxidPool.get(oxids.get(i-1)));
        for (Foundation foundation : foundations){
            if (foundPool.get(foundation) > 1){
                tempArray[i] = foundPool.get(foundation) + "(" + tempArray[i] + ")";
            }
            if (tempArray[i].equals("H")){ // Only for water for now
                tempArray[i] = "(" + tempArray[i] + ")" + "2";
            }
            i--;
        }

        i = tempArray.length-1;

        for (Oxid oxid : oxids){
            if (oxidPool.get(oxid) == 1){
                if (!tempArray[i].contains("H")){
                    tempArray[i] = tempArray[i] + oxid.getOxid_name();
                }
            } else {
                tempArray[i] = tempArray[i] + "(" + oxid.getOxid_name() + ")" + oxidPool.get(oxid);
            }
            i--;
        }

        phrase = "Какой итог мы получили: " + String.join(" + ", tempArray) + ". Емкость очищена";

        clearEquipment();
    }

    public void clearEquipment(){
        for (Equipment equip : usedEquipment) {
            boolean check = false;
            for (Substance substance : substances) {
                if (!equip.getSubstancesInside().contains(substance.getSubId())) {
                    check = false;
                    break;
                } else check = true;
            }
            if (check) {
                equip.setSubstancesInside(new ArrayList<String>());
                System.out.println("Equipment is clear now");
            }
        }


    }
}

