package com.chemistry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
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
    public ArrayList<Substance> substances;
    public Map<Foundation, Integer> foundPool;
    public Map<Oxid, Integer> oxidPool;
    public DBHandler handler = new DBHandler();

    public String cause = "";

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

            ResultSet oxidFound = handler.getOxidByName(oxid); // Putting an oxidizer with its amount in its pool
            if (oxidFound.next()){
                Oxid tempOxid = new Oxid();

                tempOxid.setOxid_name(oxidFound.getString(AllConstants.OxidConsts.OXIDIZER_NAME));
                tempOxid.setName(oxidFound.getString(AllConstants.OxidConsts.NAME));
                tempOxid.setOxid_state_min(oxidFound.getString(AllConstants.OxidConsts.OXID_STATE_MIN));
                tempOxid.setOxid_state_max(oxidFound.getString(AllConstants.OxidConsts.OXID_STATE_MAX));
                tempOxid.setOxid_strength(oxidFound.getString(AllConstants.OxidConsts.OXID_STRENGTH));

                oxidPool.put(tempOxid, oxid_amount);
            }

        }
        chemicalReaction(foundPool, oxidPool);
    }
//add normal causes
    public void chemicalReaction(Map<Foundation, Integer> foundPool, Map<Oxid, Integer> oxidPool){
        ArrayList<Foundation> foundationsFirstIteration = new ArrayList<>();
        ArrayList<Oxid> oxidFirstIteration = new ArrayList<>();
        boolean startReaction = false;
        boolean containsNullOxid = false;
        int nullOxidsAmount = 0;

        boolean isFirstFoundStronger, isFirstOxidStronger;

        for (Foundation found : foundPool.keySet()){
            foundationsFirstIteration.add(found);
        }

        for (Oxid oxid : oxidPool.keySet()){
            oxidFirstIteration.add(oxid);
        }

        int firstFound = Integer.parseInt(foundationsFirstIteration.get(0).getElectrochem_pos());
        int secondFound = Integer.parseInt(foundationsFirstIteration.get(1).getElectrochem_pos());

        int firstOxidStrength = Integer.parseInt(oxidFirstIteration.get(0).getOxid_strength());
        int secondOxidStrength = Integer.parseInt(oxidFirstIteration.get(1).getOxid_strength());


        isFirstFoundStronger = firstFound < secondFound; //simplified if check of strength among oxids and founds
        isFirstOxidStronger = firstOxidStrength < secondOxidStrength;

        if (secondFound <= 7 && firstFound <= 7 && (firstFound - secondFound > 3 || secondFound - firstFound > 3)
                && firstOxidStrength != secondOxidStrength){
            startReaction = true;
        } else if ((firstFound - secondFound > 2 || secondFound - firstFound > 2)
                && firstOxidStrength != secondOxidStrength){
            startReaction = true;
        }

        for (Oxid oxid : oxidPool.keySet()){
            if (oxid.getOxid_name().equals("0") || oxid.getOxid_name().equals("O")){
                containsNullOxid = true;
                nullOxidsAmount++;
            }
        }

        for (Substance substance : substances) {
            if (substance.getSmallTexturePath().equals("H2O")) {
                startReaction = false;
                cause += "Работа с водой еще не проработана. ";
//                if (containsNullOxid){
//                    startReaction = true;
//                    break;
//                } else {
//                    startReaction = false;
//                }
            }
        }

        if (isFirstFoundStronger == isFirstOxidStronger){
            startReaction = false; //reaction won't start because strong elements are already combined
            cause += "Одно из веще. ";
        }

        if (nullOxidsAmount >= 1){
            startReaction = false;
        }

        if (startReaction){
//            reactionStarted(foundPool, oxidPool);
            newReactionStart();
        } else {
            phrase = "Реакция не пошла... Емкость очищена.";
            clearEquipment();
        }
    }
    public void newReactionStart(){
        Array<String> answer = new Array<>(); // All answer
        String answerFirstPart = ""; //First part (before =)
        String answerSecondPart = ""; //Second part (after =)

        ArrayList<Oxid> oxids = new ArrayList<>();
        ArrayList<Foundation> foundations = new ArrayList<>();

        for (Oxid oxid : oxidPool.keySet()){
            oxids.add(oxid);
        }

        for (Foundation found : foundPool.keySet()){
            foundations.add(found);
        }

        for (Substance substance : substances){
            answerFirstPart += substance.getSmallTexturePath();
            answerFirstPart += " + ";
        }
        answerFirstPart = answerFirstPart.substring(0, answerFirstPart.length()-3);

        //BEFORE SWAP
        int firstFoundationAmount = foundPool.get(foundations.get(0));
        int secondFoundationAmount = foundPool.get(foundations.get(1));

        int firstOxidAmount = oxidPool.get(oxids.get(0));
        int secondOxidAmount = oxidPool.get(oxids.get(1));

        int firstFoundationOxidState = Integer.parseInt(foundations.get(0).getFound_state_max());
        int secondFoundationOxidState = Integer.parseInt(foundations.get(1).getFound_state_max());

        int firstOxid_OxidState = -Integer.parseInt(oxids.get(0).getOxid_state_max());
        int secondOxid_OxidState = -Integer.parseInt(oxids.get(1).getOxid_state_max());

        String firstSubstanceOxidSwap = ""; //Here is substance (found - 1, oxid - 2) NO AMOUNT
        String secondSubstanceOxidSwap = ""; //Here is substance (found - 2, oxid - 1) NO AMOUNT

        //(SWAPPED)
        firstSubstanceOxidSwap += foundations.get(0).getFoundation_name() + " ";
        firstSubstanceOxidSwap += oxids.get(1).getOxid_name();

        secondSubstanceOxidSwap += foundations.get(1).getFoundation_name() + " ";
        secondSubstanceOxidSwap += oxids.get(0).getOxid_name();

        //Afterswap calculatings

        int tempSecondOxidAmount = secondOxidAmount;
        int tempFirstFoundAmount = firstFoundationAmount;
        int tempFirstOxidAmount = firstOxidAmount; //useless?
        int tempSecondFoundAmount = secondFoundationAmount;

        if (firstFoundationOxidState > secondOxid_OxidState){
            if (firstFoundationOxidState % secondOxid_OxidState == 0){
                secondOxidAmount = firstOxidAmount / secondOxid_OxidState;
                firstFoundationAmount = tempSecondFoundAmount;
            } else {
                secondOxidAmount = firstFoundationOxidState;
                firstFoundationAmount = secondOxid_OxidState;
            }
        } else if (secondOxid_OxidState > firstFoundationOxidState) {
            if (secondOxid_OxidState % firstFoundationOxidState == 0){
                firstFoundationAmount = secondOxid_OxidState / firstFoundationOxidState;
                secondOxidAmount = firstOxidAmount;
            } else {
                secondOxidAmount = firstFoundationOxidState;
                firstFoundationAmount = secondOxid_OxidState;
            }
        }

        System.out.println(firstFoundationAmount + " - " + secondOxidAmount);

        if (secondFoundationOxidState > firstOxid_OxidState){
            if (secondFoundationOxidState % firstOxid_OxidState == 0){
                firstOxidAmount = secondFoundationOxidState / firstOxid_OxidState;
                secondFoundationAmount = tempFirstFoundAmount;
            } else {
                firstOxidAmount = secondFoundationOxidState;
                secondFoundationAmount = firstOxid_OxidState;
            }
        } else if (firstOxid_OxidState > secondFoundationOxidState){
            if (firstOxid_OxidState % secondFoundationOxidState == 0){
                secondFoundationAmount = firstOxid_OxidState / secondFoundationOxidState;
                firstOxidAmount = tempSecondOxidAmount;
            } else {
                firstOxidAmount = secondFoundationOxidState;
                secondFoundationAmount = firstOxid_OxidState;
            }
        }

        System.out.println(secondFoundationAmount + " - " + firstOxidAmount);

        Array<String> tempArrayFirstSubstance = new Array<>(firstSubstanceOxidSwap.split(" "));
        Array<String> tempArraySecondSubstance = new Array<>(secondSubstanceOxidSwap.split(" "));

        //Preparing answer p2 string
        if (firstFoundationAmount > 1){
            firstSubstanceOxidSwap = firstSubstanceOxidSwap.replaceFirst(
                    tempArrayFirstSubstance.get(0), firstFoundationAmount
                            + "(" + tempArrayFirstSubstance.get(0) + ")");
        }

        if (secondOxidAmount > 1){
            firstSubstanceOxidSwap = firstSubstanceOxidSwap.replaceFirst(
                    tempArrayFirstSubstance.get(1),"("
                            + tempArrayFirstSubstance.get(1) + ")" + secondOxidAmount);
        }

        if (secondFoundationAmount > 1){
            secondSubstanceOxidSwap = secondSubstanceOxidSwap.replaceFirst(
                    tempArraySecondSubstance.get(0), secondFoundationAmount
                            + "(" + tempArraySecondSubstance.get(0) + ")");
        }

        if (firstOxidAmount > 1){
            secondSubstanceOxidSwap = secondSubstanceOxidSwap.replaceFirst(
                    tempArraySecondSubstance.get(1), "(" +
                            tempArraySecondSubstance.get(1) + ")" + firstOxidAmount);
        }

        //answer
        System.out.println(answerSecondPart);
        answerSecondPart = firstSubstanceOxidSwap + " + " + secondSubstanceOxidSwap;

        answerSecondPart = answerSecondPart.replaceAll(" ", "");
        answerSecondPart = answerSecondPart.replace("+", " + ");

        answer.add(answerFirstPart);
        answer.add(answerSecondPart);

        phrase = "Какой итог мы получили:    " + String.join(" =    ", answer) + ".    Емкость очищена";

        clearEquipment();
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

        //also need a method to every substance to check for their actual state (not min or max, just one possible, according to another evaluation) p.s. for future

        int i = tempArray.length-1;
        int firstOxidEval = oxidPool.get(oxids.get(i)) * Integer.parseInt(oxids.get(i).getOxid_state_min()); // params that are state * amount
        int secondOxidEval = oxidPool.get(oxids.get(i-1)) * Integer.parseInt(oxids.get(i-1).getOxid_state_min()); // also names should be different first is second substance etc
        if (firstOxidEval == secondOxidEval){ //This check should be working fine. All it does:
        } else if (firstOxidEval > secondOxidEval){ //We swap their amounts according to evals (2 with 1)
            int tempInt = (oxidPool.get(oxids.get(i)) * Integer.parseInt(oxids.get(i).getOxid_state_min()))
                    / Integer.parseInt(oxids.get(i-1).getOxid_state_min());

            if (tempInt == 0){ //probably useless, but for now I will leave it
                tempInt++;
            }

            int tempInt2 = (oxidPool.get(oxids.get(i-1)) * Integer.parseInt(oxids.get(i-1).getOxid_state_min()))
                    / Integer.parseInt(oxids.get(i).getOxid_state_min());

            if (tempInt2 == 0){
                tempInt2++;
            }

            oxidPool.replace(oxids.get(i), tempInt2);
            oxidPool.replace(oxids.get(i-1), tempInt);

        } else { // 1 with 2 2Fe(Cl)6 nerf
            int tempInt = (oxidPool.get(oxids.get(i-1)) * Integer.parseInt(oxids.get(i-1).getOxid_state_min()))
                    / Integer.parseInt(oxids.get(i).getOxid_state_min());

            if (tempInt == 0){
                tempInt++;
            }

            int tempInt2 = (oxidPool.get(oxids.get(i)) * Integer.parseInt(oxids.get(i).getOxid_state_min()))
                    / Integer.parseInt(oxids.get(i-1).getOxid_state_min());

            if (tempInt2 == 0){
                tempInt2++;
            }
            System.out.println(tempInt2 + " ti2");
            oxidPool.replace(oxids.get(i-1), tempInt2);
            oxidPool.replace(oxids.get(i), tempInt);
        }

        // nerf 2FeCl6 -> FeCl3
        // 2->4 still exists (not nerfed). nerfed
        if (foundPool.get(foundations.get(i-1)) % 2 == 0 && oxidPool.get(oxids.get(i)) % 2 == 0){
            foundPool.replace(foundations.get(i-1), foundPool.get(foundations.get(i-1)) / 2);
            oxidPool.replace(oxids.get(i),oxidPool.get(oxids.get(i)) / 2);
        }
        if (foundPool.get(foundations.get(i)) % 2 == 0 && oxidPool.get(oxids.get(i-1)) % 2 == 0){
                foundPool.replace(foundations.get(i), foundPool.get(foundations.get(i)) / 2);
                oxidPool.replace(oxids.get(i-1),oxidPool.get(oxids.get(i-1)) / 2);
        }

        //At upper code there was NO replacements in oxids/founds arrays. BUT it swapped their amounts already!

        boolean nullOxidizer = oxids.get(i).getOxid_name().equals("0") || oxids.get(i - 1).getOxid_name().equals("0");

        if (nullOxidizer){
            //code for substance where oxid is null (only foundation aka Na)
        } else {
            //for all substances
            int firstSFoundEval = foundPool.get(foundations.get(i-1)) * Integer.parseInt(foundations.get(i-1).getFound_state_max());
            int firstSOxidEval = -oxidPool.get(oxids.get(i)) * Integer.parseInt(oxids.get(i).getOxid_state_min());
            int secondSFoundEval = foundPool.get(foundations.get(i)) * Integer.parseInt(foundations.get(i).getFound_state_max());
            int secondSOxidEval = -oxidPool.get(oxids.get(i-1)) * Integer.parseInt(oxids.get(i-1).getOxid_state_min());
            System.out.println(firstSFoundEval + " - " + firstSOxidEval);
            System.out.println(secondSFoundEval + " - " + secondSOxidEval);

            if (firstSOxidEval > firstSFoundEval){
                foundPool.replace(foundations.get(i), firstSOxidEval / Integer.parseInt(foundations.get(i-1).getFound_state_max()));
                System.out.println(foundPool.get(foundations.get(i)));
            }
            if (secondSOxidEval > secondSFoundEval){
                foundPool.replace(foundations.get(i-1), secondSOxidEval / Integer.parseInt(foundations.get(i).getFound_state_max()));
                System.out.println(foundPool.get(foundations.get(i-1)));
            }
        }

        System.out.println(oxidPool.get(oxids.get(i)) + " - " + oxidPool.get(oxids.get(i-1)));

        for (String string : tempArray){
            System.out.println(string + " ///");
        }

        for (Foundation foundation : foundations){
            if (foundPool.get(foundation) > 1){
                tempArray[i] = foundPool.get(foundation) + "(" + tempArray[i] + ")";
            }
            if (tempArray[i].equals("H")){ // Only for water for now. Non-single hydrogen works incorrect.
                tempArray[i] = "(" + tempArray[i] + ")" + "2";
            }
            i--;
        }

        i = tempArray.length-1;

        for (Oxid oxid : oxids){
            if (oxidPool.get(oxid) <= 1){
                if (!tempArray[i].equals("(H)2") && !tempArray[i].equals("NH4")){
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

