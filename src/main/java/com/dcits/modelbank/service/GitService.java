package com.dcits.modelbank.service;

import com.dcits.modelbank.model.FileModel;
import org.eclipse.jgit.diff.DiffEntry;

import java.util.List;

/**
 * Created on 2017-11-07 19:41.
 *
 * @author kevin
 */
public interface GitService {
    List<FileModel> getChangesFileListToday();
}
