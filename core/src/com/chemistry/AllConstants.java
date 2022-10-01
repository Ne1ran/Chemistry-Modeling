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
    }

}
