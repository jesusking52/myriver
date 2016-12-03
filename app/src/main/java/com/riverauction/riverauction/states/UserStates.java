package com.riverauction.riverauction.states;

import com.google.common.base.Strings;
import com.jhcompany.android.libs.preference.State;
import com.jhcompany.android.libs.preference.StateCtx;
import com.jhcompany.android.libs.preference.States;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.states.localmodel.MSubjectGroups;

public class UserStates extends States {

    private static final String STATES_NAME = "state_user";

    public static final State<CUser> USER = defineObject(STATES_NAME, "user", null, CUser.class);
    public static final State<String> ACCESS_TOKEN = defineString(STATES_NAME, "access_token", null);
    public static final State<MSubjectGroups> SUBJECT_GROUPS = defineObject(STATES_NAME, "subject_groups", null, MSubjectGroups.class);
    public static final State<Boolean> TUTORIAL_SHOWN = defineBoolean(STATES_NAME, "tutorial_shown", false);

    public static void clear(StateCtx stateCtx) {
        clear(stateCtx, STATES_NAME);
    }

    public static boolean hasAccessToken(StateCtx stateCtx) {
        return !Strings.isNullOrEmpty(ACCESS_TOKEN.get(stateCtx));
    }
}
