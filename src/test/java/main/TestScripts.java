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
import org.springframework.web.util.NestedServletException;

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
    }

    @Test
    public void getFuncUfs() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/func/ufs"))
                .andExpect(status().isOk()).andReturn();
        String s = mvcResult.getResponse().getContentAsString();
        System.out.println(s);
    }

    @Test
    public void getFuncNotFound() throws Exception {
        try {
            mvc.perform(get("/func/xxx"))
                    .andExpect(status().isFailedDependency());
        } catch (NestedServletException ex) {
            assertTrue(ex.getCause().getMessage().contains("is not defined"));
            return;
        }
        fail("Should not get here!");
    }
    
    @Test
    public void getFuncNotImplemented() throws Exception {
        try {
            mvc.perform(get("/func/notImplemented"))
                    .andExpect(status().isFailedDependency());
        } catch (NestedServletException ex) {
            assertTrue(ex.getCause().getMessage().contains("is not implemented"));
            return;
        }
        fail("Should not get here!");
    }
}
