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
 */
package main;

import io.AudioStatus;
import io.PathsIO;
import static org.junit.Assert.assertEquals;
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
import tools.JsonUtils;
import tools.OsUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class TestFileSystem {

    @Autowired
    private MockMvc mvc;

    private static final String JSON = "{\"loc\":\"loc\",\"paths\":[\"P1\",\"P2\"]}";

    @BeforeClass
    public static void beforeClass() {
        ConfigDataManager.init(new String[]{"configTestData" + OsUtils.resolveOS().name().toUpperCase() + ".json"});
    }

    @Test
    public void testInOrder() throws Exception {
        testPathIoJsonLoad();
        testGetPathsSrc();
        testGetPathsUserNotFound();
        testGetPathsLocNotFound();
    }

    public void testPathIoJsonLoad() {
        PathsIO paths = (PathsIO) JsonUtils.beanFromJson(PathsIO.class, JSON);
        assertEquals("loc", paths.getLoc());
        assertEquals(2, paths.getPaths().size());
        assertEquals("P1", paths.getPaths().get(0));
        assertEquals("P2", paths.getPaths().get(1));
    }

    private void testGetPathsSrc() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/paths/user/test/loc/java")).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
        PathsIO paths = (PathsIO) JsonUtils.beanFromJson(PathsIO.class, resp);
        System.out.println(paths);
    }
    
    private void testGetPathsUserNotFound() throws Exception {
        mvc.perform(get("/paths/user/tony/loc/fred"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"Status\":404, \"Msg\":\"Not Found\", \"Entity\":\"tony\"}"));
    }
    
    private void testGetPathsLocNotFound() throws Exception {
        mvc.perform(get("/paths/user/stuart/loc/fred"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"Status\":404, \"Msg\":\"Not Found\", \"Entity\":\"fred\"}"));
    }
}
