/*
 * Copyright (C) 2018 stuartdd
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

/**
 *
 * @author stuart
 */
import config.ConfigDataManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import tools.OsUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class TestUsersState extends TestBaseClass {

    @Autowired
    private MockMvc mvc;

    private static String STATE = "{\"imagesPerRow\":2,\"imageHistory\":[\"user/stuart/loc/images/path/lg 001/name/2006_08_22_15_54_45_22082006011.jpg.JPG\"]}";

    @Test
    public void getUserData() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/files/user/stuart/loc/data/name/state.json")).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
        assertTrue(resp.contains("\"imagesPerRow\":"));
        assertTrue(resp.contains("\"imageHistory\":"));

        mvc.perform(post("/files/user/stuart/loc/data/name/state.json").content(STATE).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isCreated());
        mvcResult = mvc.perform(get("/files/user/stuart/loc/data/name/state.json")).andExpect(status().isOk()).andReturn();
        assertEquals(STATE, mvcResult.getResponse().getContentAsString());

        mvc.perform(post("/files/user/stuart/loc/data/name/state.json").content(resp).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isCreated());
    }

}
