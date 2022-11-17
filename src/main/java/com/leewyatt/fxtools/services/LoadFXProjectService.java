package com.leewyatt.fxtools.services;

import com.leewyatt.fxtools.model.Project;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;

/**
 * @author LeeWyatt
 */
public class LoadFXProjectService extends Service<List<Project>> {
    @Override
    protected Task<List<Project>> createTask() {
        return new LoadFXProjectTask();
    }
}
