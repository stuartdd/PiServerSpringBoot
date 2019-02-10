/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import static org.junit.Assert.*;
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
import services.FunctionService;

/**
 *
 * @author stuart
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class TestScripts {

    @Autowired
    private MockMvc mvc;

    @BeforeClass
    public static void beforeClass() {
        ConfigDataManager.init(new String[]{"configTestData.json"});
        FunctionService.init(ConfigDataManager.getConfigData().getFunctions());
    }

    @Test
    public void getFuncTestTree() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/func/testtree?user=stuart"))
                .andExpect(status().isOk()).andReturn();
        String s = mvcResult.getResponse().getContentAsString();
        assertTrue(s.contains("getDir.bat"));
    }

    @Test
    public void getFuncNotFound() throws Exception {
        MvcResult s = mvc.perform(get("/func/xxx"))
                .andExpect(status().isFailedDependency()).andReturn();
        assertTrue(s.getResponse().getContentAsString().contains("Function 'xxx' is not defined"));
    }

    @Test
    public void getFuncNotImplemented() throws Exception {
        MvcResult s = mvc.perform(get("/func/notImplemented"))
                .andExpect(status().isFailedDependency()).andReturn();
        assertTrue(s.getResponse().getContentAsString().contains("Method 'notImplemented' is not implemented"));
    }
    
    @Test
    public void getScriptNotFound() throws Exception {
        MvcResult s = mvc.perform(get("/func/notScript"))
                .andExpect(status().isFailedDependency()).andReturn();
        assertTrue(s.getResponse().getContentAsString().contains("Failed Dependency"));
    }
    
    @Test
    public void getScriptMinRC() throws Exception {
        MvcResult s = mvc.perform(get("/func/minRcError"))
                .andExpect(status().isOk()).andReturn();
        assertTrue(s.getResponse().getContentAsString().contains("Q0:returnCode.bat"));
    }
}
