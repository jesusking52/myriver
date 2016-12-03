package com.riverauction.riverauction.states;

import com.jhcompany.android.libs.preference.State;
import com.jhcompany.android.libs.preference.StateCtx;
import com.jhcompany.android.libs.preference.States;

public class DeviceStates extends States {

    private static final String STATES_NAME = "state_device";

    public static final State<String> PHONE_NUMBER = defineString(STATES_NAME, "phone_number", null);
    public static void clear(StateCtx stateCtx) {
        clear(stateCtx, STATES_NAME);
    }
}
