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

import org.junit.Assert;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import tools.OsUtils;

/**
 *
 * @author stuart
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class TestServer {

    @Autowired
    private MockMvc mvc;

    @BeforeClass
    public static void beforeClass() {
        ConfigDataManager.init(new String[]{"configTestData" + OsUtils.resolveOS().name().toUpperCase() + ".json"});
    }

    @Test
    public void getServerTime() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/server/time"))
                .andExpect(status().isOk()).andReturn();
        String s = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue(s.contains("\"time\""));
        Assert.assertTrue(s.contains("\"timestamp\""));
        Assert.assertTrue(s.contains("\"millis\""));
    }
    
   @Test
    public void getUFS() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/server/ufs"))
                .andExpect(status().isOk()).andReturn();
        String s = mvcResult.getResponse().getContentAsString();
        System.out.println(s);
    }

}
