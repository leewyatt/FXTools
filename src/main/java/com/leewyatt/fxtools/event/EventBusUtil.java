package com.leewyatt.fxtools.event;

import com.google.common.eventbus.EventBus;

/**
 * @author LeeWyatt
 */
public class EventBusUtil {
    private EventBusUtil() {
    }

    private final static EventBus EVENT_BUS = new EventBus();

    public static EventBus getDefault() {
        return EVENT_BUS;
    }
}
