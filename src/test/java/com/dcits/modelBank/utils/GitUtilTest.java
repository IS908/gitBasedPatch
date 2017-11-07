package com.dcits.modelBank.utils;

import org.eclipse.jgit.diff.DiffEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created on 2017-11-06 22:39.
 *
 * @author kevin
 */
public class GitUtilTest {
    private GitUtil gitUtil;

    @Before
    public void beforeTest() {
        gitUtil = GitUtil.getInstance();
    }

    @Test
    public void commitCode() throws Exception {
        gitUtil.commitCode("add logback.xml ");
    }

    @Test
    public void showBranchesDiffFileList() throws Exception {
        List<DiffEntry> list = gitUtil.showBranchesDiffFileList("master", "develop");

//        for (DiffEntry entry:list) {
//            System.out.println(entry);
//        }
        gitUtil.showDiffBySingleFile(list);
    }

    @Test
    public void readElementsAtTest() {
    }

}