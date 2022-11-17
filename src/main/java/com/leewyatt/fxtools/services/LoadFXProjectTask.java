package com.leewyatt.fxtools.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leewyatt.fxtools.model.Project;
import com.leewyatt.fxtools.utils.AlphanumComparator;
import com.leewyatt.fxtools.utils.FileUtil;
import com.leewyatt.fxtools.utils.OSUtil;
import javafx.concurrent.Task;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LeeWyatt
 */
public class LoadFXProjectTask extends Task<List<Project>> {
    @Override
    protected List<Project> call() throws Exception {

        Gson gson = new Gson();
        String content = FileUtil.readString("/data/fxproject.json");
        updateProgress(1, 1);
        List<Project> projectList = gson.fromJson(content, new TypeToken<List<Project>>() {
        }.getType());
        if (OSUtil.isEnglish()) {
            projectList = projectList.stream().filter(item -> !"zh-cn-webpage".equalsIgnoreCase(item.getType())).collect(Collectors.toList());
        }
        projectList.sort((p1,p2)->{
            if (p1.getType().equalsIgnoreCase(p2.getType())) {
                return new AlphanumComparator().compare(p1.getName(),p2.getName());
            }else {
               return p1.getType().compareTo(p2.getType());
            }
        });

        return projectList;
    }
}
