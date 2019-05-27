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

/**
 *
 * @author stuart
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class TestServer extends TestBaseClass {

    @Autowired
    private MockMvc mvc;

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
        MvcResult mvcResult = mvc.perform(get("/files/loc/cache/name/ufs"))
                .andExpect(status().isOk()).andReturn();
        String s = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue(s.contains("\"Name\":\"shared\""));
        Assert.assertTrue(s.contains("\"Name\":\"fred\""));
        Assert.assertTrue(s.contains("\"Name\":\"sally\""));
        Assert.assertTrue(s.contains("\"Name\":\"tom\""));
    }
    @Test
    public void getUsers() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/server/users"))
                .andExpect(status().isOk()).andReturn();
        String s = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue(s.contains("\"shared\""));
        Assert.assertTrue(s.contains("\"stuart\""));
        Assert.assertTrue(s.contains("\"nonuser\""));
        Assert.assertTrue(s.contains("\"test\""));
        Assert.assertTrue(s.contains("\"name\":\"Stuart\""));
    }

}
