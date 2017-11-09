package com.dcits.modelbank.service.impl;

import com.dcits.modelbank.jgit.GitHandler;
import com.dcits.modelbank.model.FileModel;
import com.dcits.modelbank.service.GitService;
import com.dcits.modelbank.utils.Const;
import com.dcits.modelbank.xmlUtils.XmlBulider;
import org.eclipse.jgit.diff.DiffEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created on 2017-11-07 19:42.
 *
 * @author kevin
 */
@Service("gitService")
public class GitServiceImpl implements GitService {
    private static final Logger logger = LoggerFactory.getLogger(GitServiceImpl.class);

    @Resource
    private GitHandler gitHandler;
    @Resource
    private XmlBulider xmlBulider;

    @Override
    public List<FileModel> genChangesFileListToday() {
        List<FileModel> fileModelList = new ArrayList<>();
        Map<String, List<DiffEntry>> lists = gitHandler.test();
        for (String key : lists.keySet()) {
            logger.info(key);
            List<DiffEntry> list = lists.get(key);
            FileModel fileModel = diffEntry2FileModel(list);
            fileModelList.add(fileModel);
        }
        xmlBulider.execute(fileModelList);
        return null;
    }

    public FileModel diffEntry2FileModel(List<DiffEntry> entries) {
        FileModel fileModel = new FileModel();
        if (entries.size() < 1)  return fileModel;
        Iterator<DiffEntry> iterator = entries.iterator();
        DiffEntry entry = iterator.next();
        fileModel.setName(entry.getNewPath());
        fileModel.setType(entry.getNewMode().toString());
        fileModel.setModule("api");
        fileModel.setPath(entry.getOldPath());
        Map<String, String> map = diffEntry2Map(entry);
        List<Map<String, String>> list = new ArrayList<>();
        list.add(map);
        while (iterator.hasNext()) {
            entry = iterator.next();
            map = diffEntry2Map(entry);
            list.add(map);
        }
        fileModel.setAuthors(list);
        return fileModel;
    }

    private Map<String, String> diffEntry2Map(DiffEntry entry) {
        Map<String, String> map = new HashMap<>();
        map.put(Const.AUTHOR_NAME, "kevin");
        map.put(Const.VERSION_ID, entry.getNewId().name());
        map.put(Const.CHANGE_TYPE, entry.getChangeType().name());
        map.put(Const.DESC, entry.getOldId().name());
        map.put(Const.CHECK, "true");
        return map;
    }

    public GitHandler getGitHandler() {
        return gitHandler;
    }

    public void setGitHandler(GitHandler gitHandler) {
        this.gitHandler = gitHandler;
    }
}
