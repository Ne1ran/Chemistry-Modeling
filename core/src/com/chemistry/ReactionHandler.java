package com.chemistry;

import com.badlogic.gdx.utils.Array;
import com.chemistry.dto.Equipment;
import com.chemistry.dto.Foundation;
import com.chemistry.dto.Oxid;
import com.chemistry.dto.Substance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chemistry.ExperimentWindow.*;

public class ReactionHandler {
    public ArrayList<Substance> substances;
    public Map<Foundation, Integer> foundPool;
    public Map<Oxid, Integer> oxidPool;
    public DBHandler handler = new DBHandler();

    public String cause = "";

    public void getSubstancesFromEquipment(Equipment equipment) throws SQLException, ClassNotFoundException {
        substances = new ArrayList<>(equipment.getSubstancesInside());
        foundPool = new LinkedHashMap<>();
        oxidPool = new LinkedHashMap<>();
        experimentPoolSetting(substances);
    }


    public void experimentPoolSetting(ArrayList<Substance> substances) throws SQLException, ClassNotFoundException {
        for (Substance subs : substances){
            String foundation = subs.getFoundation();
            String oxid = subs.getOxid();
            Integer found_amount = Integer.valueOf(subs.getFound_amount());
            Integer oxid_amount = Integer.valueOf(subs.getOxid_amount());

            Foundation foundationFound = handler.getFoundationByName(foundation); // Putting a foundation with its amount in its pool
            foundPool.put(foundationFound, found_amount);

            Oxid oxidFound = handler.getOxidByName(oxid); // Putting an oxidizer with its amount in its pool
            oxidPool.put(oxidFound, oxid_amount);

        }
        chemicalReaction(foundPool, oxidPool);
    }
//add normal causes
    public void chemicalReaction(Map<Foundation, Integer> foundPool, Map<Oxid, Integer> oxidPool) throws SQLException, ClassNotFoundException {
        ArrayList<Foundation> foundationsFirstIteration = new ArrayList<>();
        ArrayList<Oxid> oxidFirstIteration = new ArrayList<>();
        boolean startBasicSwapReaction = false;
        boolean containsNullOxid = false;
        boolean canOVRbeStarted = checkForOVR_Possibility();

        int nullOxidsAmount = 0;

        if (canOVRbeStarted){ //add a check so next substance will be used as environment
            phrase = "Началась окислительно-восстановительная реакция. Выберите среду, в которой она будет происходить. Добавьте воду, кислоту или щелочь";
            waitForAddition = true;
//            StartOVR_Reaction("neutral");
//            StartOVR_Reaction("acid");
//            StartOVR_Reaction("alkaline");
        } else {
            Array<Boolean> arrayOfEverythingCheckedAndAdditionReaction = getSimpleSwapAndAdditingReactionsPossibilities();
            if (arrayOfEverythingCheckedAndAdditionReaction.get(0)){
                boolean isFirstFoundStronger, isFirstOxidStronger;

                for (Foundation found : foundPool.keySet()){
                    foundationsFirstIteration.add(found);
                }

                for (Oxid oxid : oxidPool.keySet()){
                    oxidFirstIteration.add(oxid);
                }

                //Strength of elements
                int firstFound = Integer.parseInt(foundationsFirstIteration.get(0).getElectrochem_pos());
                int secondFound = Integer.parseInt(foundationsFirstIteration.get(1).getElectrochem_pos());

                int firstOxidStrength = Integer.parseInt(oxidFirstIteration.get(0).getOxid_strength());
                int secondOxidStrength = Integer.parseInt(oxidFirstIteration.get(1).getOxid_strength());

                isFirstFoundStronger = firstFound < secondFound; //simplified if check of strength among oxids and founds
                isFirstOxidStronger = firstOxidStrength < secondOxidStrength;

                for (Oxid oxid : oxidPool.keySet()){
                    if (oxid.getOxid_name().equals("0")){
                        containsNullOxid = true;
                        nullOxidsAmount++;
                    }
                }

                if (isFirstFoundStronger == isFirstOxidStronger){
                    startBasicSwapReaction = false; //reaction won't start because strong elements are already combined fine
                    cause += "Вещества находятся в правильном балансе. ";
                }

                if (secondFound <= 7 && firstFound <= 7 && (firstFound - secondFound > 1 || secondFound - firstFound > 1)
                        && firstOxidStrength != secondOxidStrength){
                    startBasicSwapReaction = true;
                } else if ((firstFound > secondFound || secondFound > firstFound)
                        && firstOxidStrength != secondOxidStrength){
                    startBasicSwapReaction = true;
                }

                if (nullOxidsAmount > 1){
                    startBasicSwapReaction = false;
                }

                for (Substance substance : substances) {
                    if (substance.getSubstanceNameInGame().equals("H2O")) {
                        if (containsNullOxid){
                            startBasicSwapReaction = true;
                            break;
                        } else {
                            startBasicSwapReaction = false;
                        }
                    }
                }

                if (startBasicSwapReaction){
                    StartSimpleSwapReaction();
                } else {
                    System.out.println("Reaction didnt started: " + foundationsFirstIteration.get(0).getFoundation_name()
                            + oxidFirstIteration.get(0).getOxid_name() + " + " +
                            foundationsFirstIteration.get(1).getFoundation_name()
                            + oxidFirstIteration.get(1).getOxid_name());
                    phrase = "Реакция не пошла... Очистите емкость повторным нажатием. Причина: ";
                    phrase += cause;
                }
            } else if (arrayOfEverythingCheckedAndAdditionReaction.get(1)){
                phrase = "Addition reaction has started!!";
                System.out.println("Addition reaction has started!!!");
                StartAdditingReaction();
            } else {
                phrase = "No reaction at all";
            }
        }

    }

    public void StartOVR_Reaction(Substance environment) throws SQLException, ClassNotFoundException {
        String answer = "";
        String answerFirstPart = "";
        String answerSecondPart = "";


        for (Substance substance : substances){
            answerFirstPart += substance.getSubstanceNameInGame() + " + ";
        }

        answerSecondPart = iterationReaction(substances, environment);

        answerFirstPart = answerFirstPart.substring(0, answerFirstPart.length()-3);
        answerSecondPart = answerSecondPart.substring(0, answerSecondPart.length()-3);

        answer = answerFirstPart + " = " + answerSecondPart;
        System.out.println("Уравнение ОВР реакции при какой-то среде: " + answer);
    }

    public String iterationReaction(ArrayList<Substance> substances, Substance environmentSubstance) throws SQLException, ClassNotFoundException {
        String reaction = "";

        int receiverElectrons = 0;
        int giverElectrons = 0;

        String freeIonName = "";
        String freeOxidizerName = "";

        String environment = getCurrentEnvironmentBySubstance(environmentSubstance);

        if (environment.equals("Щелочь") || environment.equals("Вода") || environment.equals("Кислота")){
            for (Substance substance : substances) {

                if (substance.getUnstable_type().equals("1")) {

                    switch (environment) {
                        case "Вода": {
                            String newSubstanceName = "";
                            freeIonName = substance.getFoundation();

                            Oxid oxid = getOxidFromDB(substance.getOxid());

                            int startState = Integer.parseInt(oxid.getPossible_states());

                            Foundation newFoundation = getFoundationFromDB(oxid.getOxid_name().split("_")[0]);
                            Array<String> oxidAndAmount = new Array<>(splitNumbersIfNeeded(oxid.getOxid_name().split("_")[1]));
                            Oxid newOxid = getOxidFromDB(oxidAndAmount.get(0));
                            int oxidAmount = Integer.parseInt(oxidAndAmount.get(1));

                            int oxidState = Integer.parseInt(newOxid.getPossible_states().split(";")[0]) * oxidAmount;

                            int foundationState = -oxidState + startState; // + - = -, - - = +

                            int foundationWantState = 0;

                            foundationWantState = Integer.parseInt(newFoundation.getPossible_states().split(";")[0]);
                            int newOxidAmount = foundationWantState / -Integer.parseInt(newOxid.getPossible_states().split(";")[0]);
                            newSubstanceName = newFoundation.getFoundation_name() + newOxid.getOxid_name() + newOxidAmount;
                            receiverElectrons = Math.abs(foundationState - foundationWantState);

                            reaction += newSubstanceName + " + ";

                            break;
                        }
                        case "Кислота": {
                            Oxid acidOxid = handler.getOxidByName(environmentSubstance.getOxid());

                            int acidOxidState = Integer.parseInt(acidOxid.getPossible_states());

                            String newSubstanceName = "";
                            freeIonName = substance.getFoundation();

                            Oxid oxid = getOxidFromDB(substance.getOxid());

                            Foundation newFoundation = getFoundationFromDB(oxid.getOxid_name().split("_")[0]);

                            int foundationWantState = 0;

                            String foundationPossibleStates = newFoundation.getPossible_states();

                            foundationWantState = Integer.parseInt(foundationPossibleStates.split(";")[0]);

                            Array<String> tempArray = new Array<>(foundationPossibleStates.split(";")[1].split(","));

                            int foundationActualState = Integer.parseInt(tempArray.get(tempArray.indexOf(String.valueOf(foundationWantState), false)-1));

                            newFoundation.setPossible_states(String.valueOf(foundationActualState));

                            String newSaline = createSubstanceName(newFoundation, acidOxid);

                            reaction += newSaline + " + ";

                            reaction += "H2O" + " + ";

                            break;
                        }
                        case "Щелочь": {
                            freeIonName = substance.getFoundation();
                            reaction += "H2O" + " + ";

                            String newSubstanceName = "";

                            Oxid oxid = getOxidFromDB(substance.getOxid());

                            int startState = Integer.parseInt(oxid.getPossible_states());

                            int endState = 0;

                            Foundation newFoundation = getFoundationFromDB(oxid.getOxid_name().split("_")[0]);
                            Array<String> oxidAndAmount = new Array<>(splitNumbersIfNeeded(oxid.getOxid_name().split("_")[1]));
                            Oxid newOxid = getOxidFromDB(oxidAndAmount.get(0));
                            int oxidAmount = Integer.parseInt(oxidAndAmount.get(1));

                            int oxidState = Integer.parseInt(newOxid.getPossible_states().split(";")[0]) * oxidAmount;

                            int foundationState = -oxidState + startState; // + - = -, - - = +

                            int foundationWantState = 0;

                            String foundationPossibleStates = newFoundation.getPossible_states();

                            foundationWantState = Integer.parseInt(newFoundation.getPossible_states().split(";")[0]);

                            Array<String> tempArray = new Array<>(foundationPossibleStates.split(";")[1].split(","));

                            int foundationActualState = Integer.parseInt(tempArray.get(tempArray.indexOf(String.valueOf(foundationWantState), false)+1));

                            oxid.setOxid_name(oxid.getOxid_name().replaceAll("_", ""));

                            endState = -(-oxidState - foundationActualState);

                            oxid.setPossible_states(String.valueOf(endState));

                            Foundation foundation = getFoundationFromDB(freeIonName);

                            String compiledStringAnswer = createSubstanceName(foundation, oxid);

                            receiverElectrons = Math.abs(foundationState - foundationWantState);

                            reaction += compiledStringAnswer + " + ";

                            break;
                        }
                    }

                } else if (substance.getUnstable_type().equals("-1")) { //Restorer always becomes what we need (N +3 -> N +5)
                    Oxid oxid = getOxidFromDB(substance.getOxid());

                    int startState = Integer.parseInt(oxid.getPossible_states());

                    Foundation newFoundation = getFoundationFromDB(oxid.getOxid_name().split("_")[0]);
                    Array<String> oxidAndAmount = new Array<>(splitNumbersIfNeeded(oxid.getOxid_name().split("_")[1]));
                    Oxid newOxid = getOxidFromDB(oxidAndAmount.get(0));
                    int oxidAmount = Integer.parseInt(oxidAndAmount.get(1));

                    int oxidState = Integer.parseInt(newOxid.getPossible_states().split(";")[0]) * oxidAmount;

                    int foundationState = -oxidState + startState; // + - = -, - - = +
                    int foundationWantState = Integer.parseInt(newFoundation.getPossible_states().split(";")[0]);
                    giverElectrons = Math.abs(foundationState - foundationWantState);

                    int newOxidAmount = (foundationWantState + (-startState)) / -Integer.parseInt(newOxid.getPossible_states());

                    freeOxidizerName = newFoundation.getFoundation_name() + "_" + newOxid.getOxid_name() + newOxidAmount;

                    String newSaline = createSubstanceName(handler.getFoundationByName(substance.getFoundation()), handler.getOxidByName(freeOxidizerName));

                    reaction += newSaline + " + ";
                } else {
                    System.out.println("Not really possible?");
                }
            }

            if (!freeIonName.equals("") && !freeOxidizerName.equals("")){
                switch (environment){
                    case "Вода": {
                        String additionalAlkaline = createSubstanceName(handler.getFoundationByName(freeIonName), handler.getOxidByName("O_H"));
                        reaction += additionalAlkaline + " + ";
                        break;
                    }
                    case "Кислота": {
                        String additionalSaline = createSubstanceName(handler.getFoundationByName(freeIonName), handler.getOxidByName(environmentSubstance.getOxid()));
                        reaction += additionalSaline + " + ";
                        break;
                    }
                    case "Щелочь": {
                        //nothing. only h2o
                    }
                }
            }
        }
        return reaction;
    }

    private String getCurrentEnvironmentBySubstance(Substance environmentSubstance) throws SQLException, ClassNotFoundException {
        String answer = "";

        answer = handler.getSubstanceType(environmentSubstance.getFoundation(), environmentSubstance.getOxid());

        return answer;
    }

    public String createSubstanceName(Foundation foundation, Oxid oxid) throws SQLException, ClassNotFoundException {
        String answer = "";

        int foundationState = Integer.parseInt(foundation.getPossible_states());
        int oxidState = -Integer.parseInt(oxid.getPossible_states());

        int foundAmount = 0;
        int oxidAmount = 0;

        if (oxid.getOxid_name().contains("_")){
            oxid.setOxid_name(oxid.getOxid_name().replaceAll("_", ""));
        }

        if (foundationState == oxidState){
            foundAmount = 1;
            oxidAmount = 1;
        } else if (foundationState > oxidState){
            foundAmount = oxidState;
            oxidAmount = foundationState;
        } else {
            foundAmount = oxidState;
            oxidAmount = foundationState;
        }

        if (foundAmount > 1){
            if (handler.getIsFoundationSimple(foundation.getFoundation_name())){
                answer += foundation.getFoundation_name() + foundAmount;
            } else {
                answer += foundation.getFoundation_name() + "(" + foundAmount + ")";
            }
        } else {
            answer += foundation.getFoundation_name();
        }

        if (oxidAmount > 1){
            if (handler.getIsOxidizerSimple(oxid.getOxid_name())) {
                answer += oxid.getName() + oxidAmount;
            } else {
                answer += "(" + oxid.getOxid_name() + ")" + oxidAmount;
            }
        } else {
            answer += oxid.getOxid_name();
        }

        return answer;
    }

    public Array<String> splitNumbersIfNeeded(String str){
        Array<String> array = new Array<>();
        String elem = "";
        String amount = "1";
        String regex1 = "\\D+";
        String regex2 = "\\d+";
        Pattern pattern1 = Pattern.compile(regex1);
        Pattern pattern2 = Pattern.compile(regex2);
        Matcher matcher = pattern1.matcher(str);
        while (matcher.find()){
            elem = str.substring(matcher.start(), matcher.end());
        }
        Matcher matcher1 = pattern2.matcher(str);
        while (matcher1.find()){
            amount = str.substring(matcher1.start(), matcher1.end());
        }
        array.add(elem);
        array.add(amount);
        return array;
    }

    public Foundation getFoundationFromDB(String foundName) throws SQLException, ClassNotFoundException {
        return handler.getFoundationByName(foundName);
    }

    public Oxid getOxidFromDB(String oxidName) throws SQLException, ClassNotFoundException { //with full possible states
        Oxid tempOxid = handler.getOxidByName(oxidName);
        return tempOxid;
    }


    public Array<Boolean> getSimpleSwapAndAdditingReactionsPossibilities() {
        Array<Boolean> array = new Array<>();

        String substance1type = substances.get(0).getSubstanceType();
        String substance2type = substances.get(1).getSubstanceType();

        boolean everyThingChecked = false;
        boolean isReactionAdditing = false;

        if (substance1type.contains(AllConstants.ReactionHandlerUtility.METAL) //if one of them is metal
                || substance2type.contains(AllConstants.ReactionHandlerUtility.METAL)) {
            everyThingChecked = true;

            if (substance1type.contains(AllConstants.ReactionHandlerUtility.OXID_ACID) ||   //if metal react with oxid
                    substance2type.contains(AllConstants.ReactionHandlerUtility.OXID_ACID)) { // its addition reaction
                isReactionAdditing = true;
                everyThingChecked = false;
            }

        } else if (substance1type.contains(AllConstants.ReactionHandlerUtility.OXID) || //if one of the is oxid
                substance2type.contains(AllConstants.ReactionHandlerUtility.OXID)) {

            if (
                    (substance1type.contains(AllConstants.ReactionHandlerUtility.OXID_ALKALINE) && // if alkaline
                            substance2type.contains(AllConstants.ReactionHandlerUtility.OXID_ACID) || // and second is acid

                            (substance2type.contains(AllConstants.ReactionHandlerUtility.OXID_ALKALINE) &&
                                    substance1type.contains(AllConstants.ReactionHandlerUtility.OXID_ACID))
                    )) {
                isReactionAdditing = true;

            } else if (
                    ((substance1type.contains(AllConstants.ReactionHandlerUtility.OXID_ACID) || //if one of the oxids
                            substance1type.contains(AllConstants.ReactionHandlerUtility.OXID_ALKALINE)) && //is either acid or alkaline
                            substance2type.contains(AllConstants.ReactionHandlerUtility.WATER)) //and second elem is water
                            ||
                            ((substance2type.contains(AllConstants.ReactionHandlerUtility.OXID_ACID) ||
                                    substance2type.contains(AllConstants.ReactionHandlerUtility.OXID_ALKALINE)) &&
                                    substance1type.contains(AllConstants.ReactionHandlerUtility.WATER))) {
                isReactionAdditing = true; //this is an addition reaction

            } else if ((substance1type.contains(AllConstants.ReactionHandlerUtility.OXID_ACID) && // NO3 + NaOH = NaOH + H2O
                    substance2type.contains(AllConstants.ReactionHandlerUtility.ALKALI)) || // basic swap

                    (substance2type.contains(AllConstants.ReactionHandlerUtility.OXID_ACID) &&
                            substance1type.contains(AllConstants.ReactionHandlerUtility.ALKALI))
            ) {

                everyThingChecked = true;

            } else if ((substance1type.contains(AllConstants.ReactionHandlerUtility.OXID_ALKALINE) && //Na2O + H2SO4 = NaSO4 + H2O
                    substance2type.contains(AllConstants.ReactionHandlerUtility.ACID)) || //basic swap

                    (substance2type.contains(AllConstants.ReactionHandlerUtility.OXID_ALKALINE) &&
                            substance1type.contains(AllConstants.ReactionHandlerUtility.ACID))) {

                everyThingChecked = true;

            }

        } else if (substance1type.contains(AllConstants.ReactionHandlerUtility.ACID) || // if it's acid
                substance2type.contains(AllConstants.ReactionHandlerUtility.ACID)) {

            if ((substance1type.contains(AllConstants.ReactionHandlerUtility.ACID) && //then simple swap
                    substance2type.contains(AllConstants.ReactionHandlerUtility.ALKALI)) || //if one of the is alkali

                    (substance1type.contains(AllConstants.ReactionHandlerUtility.ALKALI) &&
                            substance2type.contains(AllConstants.ReactionHandlerUtility.ACID))) {

                everyThingChecked = true;
            }

        } else if (substance1type.contains(AllConstants.ReactionHandlerUtility.SALINE) || //if one of them
                substance2type.contains(AllConstants.ReactionHandlerUtility.SALINE)) { //is saline

            if ((substance1type.contains(AllConstants.ReactionHandlerUtility.SALINE) && // if the second
                    (substance2type.contains(AllConstants.ReactionHandlerUtility.ACID) || // is either acid
                            substance2type.contains(AllConstants.ReactionHandlerUtility.ALKALI) || //or alkali
                            substance2type.contains(AllConstants.ReactionHandlerUtility.SALINE) || //or another saline
                            substance1type.contains(AllConstants.ReactionHandlerUtility.OXID))) //or any normal oxids
                    ||
                    (substance2type.contains(AllConstants.ReactionHandlerUtility.SALINE) &&
                            (substance1type.contains(AllConstants.ReactionHandlerUtility.ACID) ||
                                    substance1type.contains(AllConstants.ReactionHandlerUtility.ALKALI) ||
                                    substance1type.contains(AllConstants.ReactionHandlerUtility.SALINE) ||
                                    substance1type.contains(AllConstants.ReactionHandlerUtility.OXID)))
            ) {
                everyThingChecked = true;
            }
        }
        array.add(everyThingChecked);
        array.add(isReactionAdditing);
        return array;
    }

    public boolean checkForOVR_Possibility() throws SQLException, ClassNotFoundException {
        int OVR_Oxidant = 0;
        int OVR_Reductant = 0;



        for (Substance substance : substances){
            int OVRSubstanceType = handler.getUnstableTypeById(substance.getSubId());
            if (OVRSubstanceType == 1){
                OVR_Reductant++;
            } else if (OVRSubstanceType == -1) {
                OVR_Oxidant++;
            }
        }

        return OVR_Oxidant == OVR_Reductant && OVR_Reductant == 1;
    }


    public void StartSimpleSwapReaction() throws SQLException, ClassNotFoundException {
        Array<String> answer = new Array<>(); // All answer
        String answerFirstPart = ""; //First part (before =)
        String answerSecondPart = ""; //Second part (after =)

        Boolean canReactionBeMade = false; //geteroreactions need either h2o or gas or osadok to be made of in the end

        ArrayList<Oxid> oxids = new ArrayList<>();
        ArrayList<Foundation> foundations = new ArrayList<>();

        for (Oxid oxid : oxidPool.keySet()){
            oxids.add(oxid);
        }

        for (Foundation found : foundPool.keySet()){
            foundations.add(found);
        }

        for (Substance substance : substances){
            answerFirstPart += substance.getSubstanceNameInGame();
            answerFirstPart += " + ";
        }
        answerFirstPart = answerFirstPart.substring(0, answerFirstPart.length()-3);

        //BEFORE SWAP
        int firstFoundationAmount = foundPool.get(foundations.get(0));
        int secondFoundationAmount = foundPool.get(foundations.get(1));

        int firstOxidAmount = oxidPool.get(oxids.get(0));
        int secondOxidAmount = oxidPool.get(oxids.get(1));


        int firstOxid_OxidState = -Integer.parseInt(oxids.get(0).getPossible_states().split(";")[0]);
        int secondOxid_OxidState = -Integer.parseInt(oxids.get(1).getPossible_states().split(";")[0]);

        int firstFoundationCurrentState, secondFoundationCurrentState;

        if (handler.getSubstanceType(foundations.get(0).getFoundation_name(),
                oxids.get(0).getOxid_name()).equals("Свободный металл")){

            firstFoundationCurrentState = Integer.parseInt(foundations.get(0).getPossible_states().split(";")[0]);

        } else if (foundations.get(0).getFoundation_name().equals("0")) {
            firstFoundationCurrentState = 1;
        } else firstFoundationCurrentState= firstOxid_OxidState * firstOxidAmount / firstFoundationAmount;


        if (handler.getSubstanceType(foundations.get(1).getFoundation_name(),
                oxids.get(1).getOxid_name()).equals("Свободный металл")){

            secondFoundationCurrentState = Integer.parseInt(foundations.get(1).getPossible_states().split(";")[0]);

        } else if (foundations.get(1).getFoundation_name().equals("0")) {
            secondFoundationCurrentState = 1;
        } else secondFoundationCurrentState = secondOxid_OxidState * secondOxidAmount / secondFoundationAmount;

        int firstFoundationOxidState = firstFoundationCurrentState;
        int secondFoundationOxidState = secondFoundationCurrentState;

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
                secondOxidAmount = firstOxid_OxidState / secondOxid_OxidState;
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
        } else {
            secondOxidAmount = 1;
            firstFoundationAmount = 1;
        }


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
        } else {
            secondFoundationAmount = 1;
            firstOxidAmount = 1;
        }


        Array<String> tempArrayFirstSubstance = new Array<>(firstSubstanceOxidSwap.split(" "));
        Array<String> tempArraySecondSubstance = new Array<>(secondSubstanceOxidSwap.split(" "));

        //Would be needed later
        String firstFoundAfterSwapStrength = handler.getFoundationStrengthByName(firstSubstanceOxidSwap.split(" ")[0]);
        String firstOxidAfterSwapStrength = handler.getOxidStrengthByName(firstSubstanceOxidSwap.split(" ")[1]);

        String secondFoundAfterSwapStrength = handler.getFoundationStrengthByName(secondSubstanceOxidSwap.split(" ")[0]);
        String secondOxidAfterSwapStrength = handler.getOxidStrengthByName(secondSubstanceOxidSwap.split(" ")[1]);

        //Preparing answer p2 string
        if (firstFoundationAmount > 1){ //Create brackets
            if (handler.getIsFoundationSimple(tempArrayFirstSubstance.get(0))) {
                firstSubstanceOxidSwap = firstSubstanceOxidSwap.replaceFirst(
                        tempArrayFirstSubstance.get(0), tempArrayFirstSubstance.get(0) + firstFoundationAmount);
            } else {
                firstSubstanceOxidSwap = firstSubstanceOxidSwap.replaceFirst(
                        tempArrayFirstSubstance.get(0), "(" + tempArrayFirstSubstance.get(0) + ")"
                                + firstFoundationAmount);
            }
        }

        if (secondOxidAmount > 1 && !oxids.get(1).getOxid_name().equals("0")){
            if (handler.getIsOxidizerSimple(tempArrayFirstSubstance.get(1))){
                firstSubstanceOxidSwap = firstSubstanceOxidSwap.replaceFirst(
                        tempArrayFirstSubstance.get(1),
                        tempArrayFirstSubstance.get(1) + secondOxidAmount);
            } else {
                firstSubstanceOxidSwap = firstSubstanceOxidSwap.replaceFirst(
                        tempArrayFirstSubstance.get(1),"(" +
                                tempArrayFirstSubstance.get(1) + ")"+ secondOxidAmount);
            }
        }

        if (secondFoundationAmount > 1){
            if (handler.getIsFoundationSimple(tempArraySecondSubstance.get(0))) {
                secondSubstanceOxidSwap = secondSubstanceOxidSwap.replaceFirst(
                        tempArraySecondSubstance.get(0), tempArraySecondSubstance.get(0) + secondFoundationAmount);
            } else {
                secondSubstanceOxidSwap = secondSubstanceOxidSwap.replaceFirst(
                        tempArraySecondSubstance.get(0), "(" + tempArraySecondSubstance.get(0) + ")"
                                + secondFoundationAmount);
            }
        }

        if (firstOxidAmount > 1 && !oxids.get(0).getOxid_name().equals("0")){
            if (handler.getIsOxidizerSimple(tempArraySecondSubstance.get(1))){
                secondSubstanceOxidSwap = secondSubstanceOxidSwap.replaceFirst(
                        tempArraySecondSubstance.get(1),
                        tempArraySecondSubstance.get(1) + firstOxidAmount);
            } else {
                secondSubstanceOxidSwap = secondSubstanceOxidSwap.replaceFirst(
                        tempArraySecondSubstance.get(1),"(" +
                                tempArraySecondSubstance.get(1) + ")"+ firstOxidAmount);
            }
        }

        //answer part 2
        if (firstSubstanceOxidSwap.contains("(0)")){ // work with H (0) or NH4 (0)
            canReactionBeMade = true;
            if (!firstSubstanceOxidSwap.contains("(0) 0")){
                if (firstSubstanceOxidSwap.split(" ")[0].length() == 1) { //Cl problems probably?
                    firstSubstanceOxidSwap = firstSubstanceOxidSwap.replace("(0)", "2"); //makes it H2
                } else firstSubstanceOxidSwap = firstSubstanceOxidSwap.replace("(0)", ""); //makes it NH4
            }

            answerSecondPart = firstSubstanceOxidSwap + " + " + secondSubstanceOxidSwap;
        } else if (secondSubstanceOxidSwap.contains("(0)")){
            canReactionBeMade = true;
            if (!secondSubstanceOxidSwap.contains("(0) 0")){
                if (secondSubstanceOxidSwap.split(" ")[0].length() == 1){
                    secondSubstanceOxidSwap = secondSubstanceOxidSwap.replace("(0)", "2");
                } else secondSubstanceOxidSwap = secondSubstanceOxidSwap.replace("(0)", "");
            }

            answerSecondPart = firstSubstanceOxidSwap + " + " + secondSubstanceOxidSwap;
        } else {

            if (Integer.parseInt(firstFoundAfterSwapStrength) > 8 && Integer.parseInt(firstOxidAfterSwapStrength) > 12){ //Dissotiation possibility
//                String dissociatedFirstSubstance = dissociate(tempArrayFirstSubstance);
                String dissociatedFirstSubstance = firstSubstanceOxidSwap;
                canReactionBeMade = true;

                if (dissociatedFirstSubstance != null) { //if there is something
                    answerSecondPart = dissociatedFirstSubstance + " + " + secondSubstanceOxidSwap;
                } else { //if not
                    if (firstSubstanceOxidSwap.contains("H (OH)")){ //change hoh -> h2o
                        answerSecondPart = firstSubstanceOxidSwap.replace("H (OH)", "H2O") + " + " + secondSubstanceOxidSwap;
                    } else if (firstSubstanceOxidSwap.contains("0 (OH)")) {
                        answerSecondPart = firstSubstanceOxidSwap.replace("0 (OH)", "H2O") + " + " + secondSubstanceOxidSwap;
                    } else answerSecondPart = firstSubstanceOxidSwap + " + " + secondSubstanceOxidSwap;
                }

            } else if (Integer.parseInt(secondFoundAfterSwapStrength) > 8 && Integer.parseInt(secondOxidAfterSwapStrength) > 13){
//                String dissociatedSecondSubstance = dissociate(tempArraySecondSubstance); its hard to do
                String dissociatedSecondSubstance = secondSubstanceOxidSwap;
                canReactionBeMade = true;

                if (dissociatedSecondSubstance != null) {
                    answerSecondPart = firstSubstanceOxidSwap + " + " + dissociatedSecondSubstance;
                } else {
                    if (secondSubstanceOxidSwap.contains("H (OH)")){
                        answerSecondPart = firstSubstanceOxidSwap + " + " + secondSubstanceOxidSwap.replace("H (OH)", "H2O");
                    } else if (secondSubstanceOxidSwap.contains("0 (OH)")){
                        answerSecondPart = firstSubstanceOxidSwap + " + " + secondSubstanceOxidSwap.replace("0 (OH)", "H2O");
                    } else answerSecondPart = firstSubstanceOxidSwap + " + " + secondSubstanceOxidSwap;
                }

            } else answerSecondPart = firstSubstanceOxidSwap + " + " + secondSubstanceOxidSwap;
        }

        answerSecondPart = answerSecondPart.replaceFirst("\\(0\\) 0", "");
        answerSecondPart = answerSecondPart.replaceAll("0", "").trim();
        answerSecondPart = answerSecondPart.replaceAll(" ", "").trim();
        answerSecondPart = answerSecondPart.replace("+", " + ");

        Array<String> tempArr = new Array<>(answerSecondPart.split(" \\+ "));
        if (tempArr.size == 1){
            answerSecondPart = tempArr.get(0);
        } else if (tempArr.size == 2 && tempArr.get(0).equals("")){
            answerSecondPart = tempArr.get(1);
        }

        if (!canReactionBeMade){ // check if one of the substance is either h2o or gas or osadok

            if (handler.CheckIfReactionPossible(tempArrayFirstSubstance)){
                canReactionBeMade = true;
            }

            if (handler.CheckIfReactionPossible(tempArraySecondSubstance)){
                canReactionBeMade = true;
            }

        }

        answer.add(answerFirstPart);
        answer.add(answerSecondPart);

        if (canReactionBeMade){
            phrase = "Какой итог мы получили:    " + String.join(" = ", answer) + ".    Емкость очищена";
//            cause = "";
            System.out.println("Реакция успешна: " + String.join(" = ", answer));
        } else phrase = "Реакция не пошла(";
    }

    public void StartAdditingReaction() throws SQLException, ClassNotFoundException {
        Array<String> answer = new Array<>(); // All answer
        String answerFirstPart = ""; //First part (before =)
        String answerSecondPart = ""; //Second part (after =)

        Boolean canReactionBeMade = false; //geteroreactions need either h2o or gas or osadok to be made of in the end

        ArrayList<Oxid> oxids = new ArrayList<>();
        ArrayList<Foundation> foundations = new ArrayList<>();

        for (Oxid oxid : oxidPool.keySet()){
            oxids.add(oxid);
        }

        for (Foundation found : foundPool.keySet()){
            foundations.add(found);
        }

        for (Substance substance : substances){
            answerFirstPart += substance.getSubstanceNameInGame();
            answerFirstPart += " + ";
        }
        answerFirstPart = answerFirstPart.substring(0, answerFirstPart.length()-3);

        if (substances.get(0).getOxid().contains(substances.get(1).getOxid()) &&
                (substances.get(0).getSubstanceType().equals(AllConstants.ReactionHandlerUtility.OXID_ACID) ||
                substances.get(0).getSubstanceType().equals(AllConstants.ReactionHandlerUtility.WATER))){
            int matchedOxidAmount = Integer.parseInt(substances.get(0).getOxid_amount());
            String newOxidName = "";
            String foundName = "";
            matchedOxidAmount += Integer.parseInt(substances.get(1).getOxid_amount());

            if (substances.get(0).getSubstanceType().equals(AllConstants.ReactionHandlerUtility.WATER)){
                newOxidName = substances.get(1).getFoundation() + substances.get(1).getOxid() + matchedOxidAmount;
                foundName = substances.get(0).getFoundation();
            } else{
                newOxidName = substances.get(0).getFoundation() + substances.get(0).getOxid() + matchedOxidAmount; //add foundAmount??
                foundName = substances.get(1).getFoundation();
            }

            int getOxidState = -handler.getOxidStateByName(newOxidName);
            int getFoundationState = handler.getFoundationStateByName(foundName);

            int oxidAmount = 1;
            int foundAmount = 1;

            if (getOxidState % getFoundationState == 0){
                foundAmount = getOxidState/getFoundationState;
            } else if (getFoundationState % getOxidState == 0){
                oxidAmount = getFoundationState/getOxidState;
            }

            answerSecondPart = compileSubstanceName(foundName, newOxidName, foundAmount, oxidAmount);

        } else if (substances.get(1).getOxid().contains(substances.get(0).getOxid()) &&
                (substances.get(1).getSubstanceType().equals(AllConstants.ReactionHandlerUtility.OXID_ACID)||
                substances.get(1).getSubstanceType().equals(AllConstants.ReactionHandlerUtility.WATER))) {

            int matchedOxidAmount = Integer.parseInt(substances.get(1).getOxid_amount());

            matchedOxidAmount += Integer.parseInt(substances.get(0).getOxid_amount());
            String newOxidName = "";
            String foundName = "";

            if (substances.get(1).getSubstanceType().equals(AllConstants.ReactionHandlerUtility.WATER)){
                newOxidName = substances.get(0).getFoundation() + substances.get(0).getOxid() + matchedOxidAmount;
                foundName = substances.get(1).getFoundation();
            } else{
                newOxidName = substances.get(1).getFoundation() + substances.get(1).getOxid() + matchedOxidAmount; //add foundAmount??
                foundName = substances.get(0).getFoundation();
            }

            int getOxidState = -handler.getOxidStateByName(newOxidName);
            int getFoundationState = handler.getFoundationStateByName(foundName);

            int oxidAmount = 1;
            int foundAmount = 1;

            if (getOxidState % getFoundationState == 0){
                foundAmount = getOxidState/getFoundationState;
            } else if (getFoundationState % getOxidState == 0){
                oxidAmount = getFoundationState/getOxidState;
            }

            answerSecondPart = compileSubstanceName(foundName, newOxidName, foundAmount, oxidAmount);

        }

        answer.add(answerFirstPart);
        answer.add(answerSecondPart);

        phrase = "Какой итог мы получили:    " + String.join(" = ", answer) + ".    Емкость очищена";
        System.out.println("Реакция успешна: " + String.join(" = ", answer));
    }

    public String compileSubstanceName(String foundation, String oxid, int foundAmount, int oxidAmount) throws SQLException, ClassNotFoundException {
        String answer = "";

        if (foundAmount > 1){
            if (handler.getIsFoundationSimple(foundation)) {
                answer += foundation + foundAmount;
            } else {
                answer +=  foundation + "(" + foundAmount + ")";

            }
        } else {
            answer += foundation;
        }

        if (oxidAmount > 1){
            if (handler.getIsOxidizerSimple(oxid)) {
                answer += oxid + oxidAmount;
            } else {
                answer += "(" + oxid + ")" + oxidAmount;
            }
        } else {
            answer += oxid;
        }

        return answer;
    }

    public String dissociate(Array<String> substance) throws SQLException, ClassNotFoundException {
        String dissociated = "" ;
        dissociated = handler.getDissotiationReaction(substance);
        return dissociated;
    }

    public void clearEquipment(){
        for (Equipment equip : usedEquipment) {
            //need a check here if equipment is fulled with substances
            equip.setSubstancesInside(new ArrayList<Substance>());
            cause = "";
        }
    }
}
