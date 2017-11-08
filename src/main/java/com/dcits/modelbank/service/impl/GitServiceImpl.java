package com.dcits.modelbank.service.impl;

import com.dcits.modelbank.jgit.GitHandler;
import com.dcits.modelbank.model.FileModel;
import com.dcits.modelbank.service.GitService;
import org.eclipse.jgit.diff.DiffEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017-11-07 19:42.
 *
 * @author kevin
 */
@Service
public class GitServiceImpl implements GitService {
    private static final Logger logger = LoggerFactory.getLogger(GitServiceImpl.class);

    @Resource
    private GitHandler gitHandler;

    @Override
    public List<FileModel> getChangesFileListToday() {
        List<FileModel> fileModelList = new ArrayList<>();
        Map<String, List<DiffEntry>> lists = gitHandler.test();
        for (String key : lists.keySet()) {
            logger.info(key);
            List<DiffEntry> list = lists.get(key);
        }
        return null;
    }

    public FileModel diffEntry2FileModel(List<DiffEntry> entries) {
//        FileModel fileModel = new FileModel()
        return null;
    }

    public GitHandler getGitHandler() {
        return gitHandler;
    }

    public void setGitHandler(GitHandler gitHandler) {
        this.gitHandler = gitHandler;
    }
}
