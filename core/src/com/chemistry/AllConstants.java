package com.chemistry;

public class AllConstants {
    public static class ReactionHandlerUtility{
        public static final String METAL = "Свободный металл";
        public static final String GAS = "Газ";
        public static final String ALKALI = "Щелочь";
        public static final String OXID = "Оксид";
        public static final String ACID = "Кислота";
        public static final String WATER = "Вода";
        public static final String SEDIMENT = "осадок";
        public static final String OXID_ACID = "Оксид кислотный";
        public static final String OXID_ALKALINE = "Оксид основный";
        public static final String OXID_AMPHOTERIC = "Оксид амфотерный";
        public static final String SALINE = "Соль";
    }
    public static class UserConsts{
        public static final String USERS_TABLE = "users";

        public static final String USER_ID = "user_id";
        public static final String FIO = "FIO";
        public static final String CURRENT_EXP = "current_exp";
        public static final String EXPS_COMPLETED = "exps_completed";
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
    }

    public static class ExpConsts{
        public static final String EXP_TABLE = "experiments";
        public static final String EXP_ID = "exp_id";
        public static final String NAME = "name";
        public static final String TEXTURE_PATH = "texture_path";
        public static final String CREATOR = "creator_name";
    }

    public static class SubsExpConsts{
        public static final String SUBS_EXP_TABLE = "substance_experiment";

        public static final String CONN_ID = "connect_id";
        public static final String SUBS_EXP_ID = "substance_exp_id";
        public static final String EXP_ID = "experiment_id";

        public static final String SUBS_X = "subs_x";

        public static final String SUBS_Y = "subs_y";
    }

    public static class SubsConsts{
        public static final String SUBS_TABLE = "substance";
        public static final String ID = "substance_id";
        public static final String TEXTURE_PATH = "texture_path";
        public static final String NAME = "name";
        public static final String FOUND_PART_NAME = "found_part_name";
        public static final String OXID_PART_NAME = "oxid_part_name";
        public static final String OXID_AMOUNT = "oxid_amount";
        public static final String FOUND_AMOUNT = "found_amount";
        public static final String DISSOTIATION = "dissotiation_reaction";
        public static final String SUBSTANCE_TYPE = "substance_type";
    }

    public static class EquipConsts{
        public static final String EQUIP_TABLE = "equipment";

        public static final String ID = "equip_id";
        public static final String TEXTURE_PATH = "texture_path";
        public static final String NAME = "name";
    }
    public static class EquipExpConsts{
        public static final String EQUIP_EXP_TABLE = "equip_experiment";

        public static final String CONNECT_ID = "connect_id";
        public static final String EQUIP_EXP_ID = "equip_exp_id";
        public static final String EXP4_ID = "experiment_id";
        public static final String EQUIP_X = "equip_x";
        public static final String EQUIP_Y = "equip_y";
    }

    public static class FoundConsts {
        public static final String FOUND_TABLE = "foundation";

        public static final String FOUNDATION_NAME = "foundation_name";
        public static final String NAME = "name";
        public static final String POSSIBLE_STATES = "possible_states";
        public static final String ELECTROCHEM_POSITION = "electrochem_position";
        public static final String IS_SIMPLE = "isSimple";
    }

    public static class OxidConsts {
        public static final String OXID_TABLE = "oxidizers";

        public static final String OXIDIZER_NAME = "oxidizer_name";
        public static final String NAME = "name";
        public static final String POSSIBLE_STATES = "possible_states";
        public static final String OXID_STRENGTH = "oxid_strength";
        public static final String IS_SIMPLE = "isSimple";
    }

}
