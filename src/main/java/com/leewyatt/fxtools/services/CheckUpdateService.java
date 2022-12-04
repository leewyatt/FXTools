package com.leewyatt.fxtools.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * @author LeeWyatt
 */
public class CheckUpdateService extends Service<Void> {
    @Override
    protected Task<Void> createTask() {
        return new CheckUpdateTask();
    }
}
