package com.chemistry;

public class AllConstants {

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
    }

    public static class SubsExpConsts{
        public static final String SUBS_EXP_TABLE = "substance_experiment";

        public static final String CONN_ID = "connect_id";
        public static final String SUBS_EXP_ID = "substance_exp_id";
        public static final String EXP_ID = "experiment_id";
    }

    public static class SubsConsts{
        public static final String SUBS_TABLE = "substance";

        public static final String ID = "substance_id";
        public static final String TEXTURE_PATH = "texture_path";
        public static final String NAME = "name";
        public static final String TEXTURE_X = "texture_x";
        public static final String TEXTURE_Y = "texture_y";
        public static final String FOUND_PART_NAME = "found_part_name";
        public static final String OXID_PART_NAME = "oxid_part_name";
        public static final String SMALL_TEXTURE = "small_texture";
    }

    public static class EquipConsts{
        public static final String EQUIP_TABLE = "equipment";

        public static final String ID = "equip_id";
        public static final String TEXTURE_PATH = "texture_path";
        public static final String NAME = "name";
        public static final String X_POS = "x_pos";
        public static final String Y_POS = "y_pos";
    }
    public static class EquipExpConsts{
        public static final String EQUIP_EXP_TABLE = "equip_experiment";

        public static final String CONNECT_ID = "connect_id";
        public static final String EQUIP_EXP_ID = "equip_exp_id";
        public static final String EXP4_ID = "experiment_id";
    }

    public static class FoundConsts {
        public static final String FOUND_TABLE = "foundation";

        public static final String FOUNDATION_NAME = "foundation_name";
        public static final String NAME = "name";
        public static final String FOUND_STATE_MIN = "found_state_min";
        public static final String FOUND_STATE_MAX = "found_state_max";
        public static final String ELECTROCHEM_POSITION = "electrochem_position";
    }

    public static class OxidConsts {
        public static final String OXID_TABLE = "oxidizers";

        public static final String OXIDIZER_NAME = "oxidizer_name";
        public static final String NAME = "name";
        public static final String OXID_STATE_MIN = "oxid_state_min";
        public static final String OXID_STATE_MAX = "oxid_state_max";

    }

}
