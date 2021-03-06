/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import static org.junit.Assert.*;
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
public class TestScripts extends TestBaseClass {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getDiskStatus() throws Exception {
        MvcResult s = mvc.perform(get("/script/ds"))
                .andExpect(status().isOk()).andReturn();
        assertEquals("[{\"name\":\"/dev/sda:\",\"state\":\"standby\"},{\"name\":\"/dev/sdb:\",\"state\":\"active/idle\"}]", s.getResponse().getContentAsString());
    }
   
    @Test
    public void getFuncNotFound() throws Exception {
        MvcResult s = mvc.perform(get("/script/xxx"))
                .andExpect(status().isFailedDependency()).andReturn();
        assertTrue(s.getResponse().getContentAsString().contains("Function 'xxx' is not defined"));
    }

    @Test
    public void getFuncNotImplemented() throws Exception {
        MvcResult s = mvc.perform(get("/script/notImplemented"))
                .andExpect(status().isFailedDependency()).andReturn();
        assertTrue(s.getResponse().getContentAsString().contains("Method 'notImplemented' is not implemented"));
    }

    @Test
    public void getScriptNotFound() throws Exception {
        MvcResult s = mvc.perform(get("/script/notScript"))
                .andExpect(status().isFailedDependency()).andReturn();
        assertTrue(s.getResponse().getContentAsString().contains("Failed Dependency"));
    }

    @Test
    public void getScriptMinRC() throws Exception {
        MvcResult s = mvc.perform(get("/script/minRcError"))
                .andExpect(status().isOk()).andReturn();
        assertEquals(200, s.getResponse().getStatus());
    }
}
