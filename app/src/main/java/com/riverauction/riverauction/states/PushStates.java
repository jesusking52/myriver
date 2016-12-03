package com.riverauction.riverauction.states;


import com.jhcompany.android.libs.preference.State;
import com.jhcompany.android.libs.preference.StateCtx;
import com.jhcompany.android.libs.preference.States;

public class PushStates extends States {

    private static final String STATES_NAME = "state_push";

    public static final State<String> GCM_REGISTRATION_ID = defineString(STATES_NAME, "state_gcm_registration_id", null);
    public static final State<Boolean> GCM_REGISTERED_ON_SERVER = defineBoolean(STATES_NAME, "state_gcm_registered_on_server", false);

    public static void clear(StateCtx stateCtx) {
        clear(stateCtx, STATES_NAME);
    }
}
