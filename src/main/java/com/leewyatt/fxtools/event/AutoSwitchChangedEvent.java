package com.leewyatt.fxtools.event;

/**
 * @author LeeWyatt
 */
public class AutoSwitchChangedEvent {
    private boolean autoSwitch;

    public AutoSwitchChangedEvent() {
    }

    public AutoSwitchChangedEvent(boolean autoSwitch) {
        this.autoSwitch = autoSwitch;
    }

    public boolean isAutoSwitch() {
        return autoSwitch;
    }

    public void setAutoSwitch(boolean autoSwitch) {
        this.autoSwitch = autoSwitch;
    }
}
