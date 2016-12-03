package com.riverauction.riverauction.eventbus;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.NoSubscriberEvent;

public final class RiverAuctionEventBus {
    private static final EventBus EVENTBUS = EventBus.getDefault();

    private static RiverAuctionEventBus INSTANCE;

    public static EventBus getEventBus() {
        synchronized (RiverAuctionEventBus.class) {
            if (INSTANCE == null) {
                INSTANCE = new RiverAuctionEventBus();
            }
        }
        return EVENTBUS;
    }

    private RiverAuctionEventBus() {
        EVENTBUS.register(this);
    }

    public static void postEvent(Object event) {
        EVENTBUS.post(event);
    }

    public static void postStickyEvent(Object event) {
        EVENTBUS.postSticky(event);
    }

    public void onEvent(NoSubscriberEvent event) {
    }
}
