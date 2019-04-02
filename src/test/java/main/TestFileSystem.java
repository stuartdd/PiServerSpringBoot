/*
 * Copyright (C) 2018 Stuiart Davies (stuartdd)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
-PcmdLineArgs=${cmd-line-args}
-PjvmLineArgs=${jvm-line-args}
 */
package main;

import config.ConfigDataManager;
import io.FileListIo;
import io.PathsIO;
import java.io.File;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import tools.EncodeDecode;
import tools.JsonUtils;
import tools.OsUtils;
import tools.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class TestFileSystem {

    @Autowired
    private MockMvc mvc;

    private static final String JSON = "{\n"
            + "	\"loc\": \"thumbs\",\n"
            + "	\"user\": \"stuart\",\n"
            + "	\"paths\": [{\n"
            + "		\"name\": \"/200609 Pics-01/200707A0\",\n"
            + "		\"encName\": \"/200609%20Pics%2D01/200707A0\"\n"
            + "	}, {\n"
            + "		\"name\": \"/200608\",\n"
            + "		\"encName\": \"/200608\"\n"
            + "	}]\n"
            + "}";

    @BeforeClass
    public static void beforeClass() {
        ConfigDataManager.init(new String[]{"configTestData" + OsUtils.resolveOS().name().toUpperCase() + ".json"});
        String lName = ConfigDataManager.getLogName().toLowerCase();
        File f = new File(ConfigDataManager.getLogPath());
        File[] l = f.listFiles();
        for (File fil : l) {
            String n = fil.getName();
            if (!n.contains("Test")) {
                if ((n.toLowerCase().contains(lName)) && (n.toLowerCase().endsWith(".log"))) {
                    fil.delete();
                }
            }
        }
    }

    @Test
    public void testInOrder() throws Exception {
        testGetPathsUserNotFound();
        testGetPathsLocNotFound();
        testFilesAll();
        testLogs();
        testReadLogs();
        testFileLoadJpg();
        testFileLoadJpgJpg();
        testFileLoadOriginal();
        testFilesJpgGif();
        testFilesJpg();
        testFilesNoMatch();
        testFilesGif();
        testPathIoJsonLoad();
        testGetPathsSrc();
    }

    private void testFileLoadJpgJpg() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/files/user/stuart/loc/original/path/" + EncodeDecode.encode("lg 001") + "/name/" + EncodeDecode.encode("2006_08_22_15_53_15_22082006010.jpg.jpg") + "?thumbnail=true")).andExpect(status().isOk()).andReturn();
        byte[] resp = mvcResult.getResponse().getContentAsByteArray();
        assertEquals(919160, resp.length);
    }

    private void testFileLoadJpg() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/files/user/stuart/loc/original/path/" + EncodeDecode.encode("lg 001") + "/name/" + EncodeDecode.encode("2006_08_22_15_53_15_22082006010.jpg.jpg") + "?thumbnail=true")).andExpect(status().isOk()).andReturn();
        byte[] resp = mvcResult.getResponse().getContentAsByteArray();
        assertEquals(919160, resp.length);
    }

    private void testFileLoadOriginal() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/files/user/stuart/loc/original/path/" + EncodeDecode.encode("lg 001") + "/name/" + EncodeDecode.encode("2006_08_22_15_53_15_22082006010.jpg.jpg") + "?thumbnail=true")).andExpect(status().isOk()).andReturn();
        byte[] resp = mvcResult.getResponse().getContentAsByteArray();
        assertEquals(919160, resp.length);
    }

    private void testReadLogs() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/files/loc/logs/name/" + EncodeDecode.encode("PiServerTest.log"))).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
        assertTrue(resp.length() > 100);
        assertTrue(resp.contains("ERROR Server: &lt;Dry-Run&gt; "));
        assertFalse(resp.contains("<"));
        assertFalse(resp.contains(">"));
    }

    private void testLogs() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/files/loc/logs?ext=log")).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
        FileListIo fileList = (FileListIo) JsonUtils.beanFromJson(FileListIo.class, resp);
        assertEquals(3, fileList.getFiles().size());
        assertEquals("PiServerTest.log", fileList.getFiles().get(0).getName().getName());
        assertEquals("UGlTZXJ2ZXJUZXN0LmxvZw==", fileList.getFiles().get(0).getName().getEncName());
    }

    public void testPathIoJsonLoad() {
        PathsIO paths = (PathsIO) JsonUtils.beanFromJson(PathsIO.class, JSON);
        assertEquals("thumbs", paths.getLoc());
        assertEquals("stuart", paths.getUser());
        assertEquals(2, paths.getPaths().size());
        assertEquals("/200609%20Pics%2D01/200707A0", paths.getPaths().get(0).getEncName());
        assertEquals("/200609 Pics-01/200707A0", paths.getPaths().get(0).getName());
    }

    private void testFilesJpg() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/files/user/stuart/loc/thumbs/path/" + EncodeDecode.encode("lg 001") + "?ext=jpg")).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
        FileListIo files = (FileListIo) JsonUtils.beanFromJson(FileListIo.class, resp);
        assertEquals(2, files.getFiles().size());
        assertEquals("stuart", files.getUser());
        assertEquals("thumbs", files.getLoc());
        assertEquals("lg 001", files.getPath().getName());
        assertEquals(EncodeDecode.encode("lg 001"), files.getPath().getEncName());
    }

    private void testFilesJpgGif() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/files/user/stuart/loc/original/path/" + EncodeDecode.encode("lg 001") + "?ext=jpg,gif")).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
        FileListIo files = (FileListIo) JsonUtils.beanFromJson(FileListIo.class, resp);
        assertEquals(4, files.getFiles().size());
        assertEquals("stuart", files.getUser());
        assertEquals("original", files.getLoc());
        assertEquals("lg 001", files.getPath().getName());
        assertEquals(EncodeDecode.encode("lg 001"), files.getPath().getEncName());
    }

    private void testFilesGif() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/files/user/stuart/loc/original/path/" + EncodeDecode.encode("lg 001") + "?ext=gif")).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
        FileListIo files = (FileListIo) JsonUtils.beanFromJson(FileListIo.class, resp);
        assertEquals(2, files.getFiles().size());
        assertEquals("stuart", files.getUser());
        assertEquals("original", files.getLoc());
        assertEquals("lg 001", files.getPath().getName());
        assertEquals(EncodeDecode.encode("lg 001"), files.getPath().getEncName());
    }

    private void testFilesAll() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/files/user/stuart/loc/original/path/" + EncodeDecode.encode("lg 001"))).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
        FileListIo files = (FileListIo) JsonUtils.beanFromJson(FileListIo.class, resp);
        assertEquals(4, files.getFiles().size());
        assertEquals("stuart", files.getUser());
        assertEquals("original", files.getLoc());
        assertEquals("lg 001", files.getPath().getName());
        assertEquals(EncodeDecode.encode("lg 001"), files.getPath().getEncName());
    }

    private void testFilesNoMatch() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/files/user/stuart/loc/thumbs/path/" + EncodeDecode.encode("lg 001") + "?ext=jxx")).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
        FileListIo files = (FileListIo) JsonUtils.beanFromJson(FileListIo.class, resp);
        assertEquals(0, files.getFiles().size());
        assertEquals("stuart", files.getUser());
        assertEquals("thumbs", files.getLoc());
        assertEquals("lg 001", files.getPath().getName());
        assertEquals(EncodeDecode.encode("lg 001"), files.getPath().getEncName());
    }

    private void testGetPathsSrc() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/paths/user/test/loc/src")).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
        PathsIO paths = (PathsIO) JsonUtils.beanFromJson(PathsIO.class, resp);
        String str = StringUtils.listToString(paths.getPaths(), "|");
        str = StringUtils.replaceAll(str, '\\', '/');
        assertTrue(str.contains("main/java/main|"));
        assertTrue(str.contains("main/java/services|"));
        assertTrue(str.contains("test/java/main|"));
        assertTrue(str.contains("test/resources/templates"));
    }

    private void testGetPathsUserNotFound() throws Exception {
        mvc.perform(get("/paths/user/tony/loc/fred"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"Status\":404, \"Msg\":\"Not Found\", \"Entity\":\"tony\"}"));
    }

    private void testGetPathsLocNotFound() throws Exception {
        mvc.perform(get("/paths/user/stuart/loc/fred"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"Status\":404, \"Msg\":\"Not Found\", \"Entity\":\"stuart.fred\"}"));
    }
}
