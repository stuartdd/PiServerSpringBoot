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
import org.junit.Before;
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
import tools.OsUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class TestTemplates {

    @Autowired
    private MockMvc mvc;
    
    @Before
    public void before() {
        ConfigDataManager.init(new String[] {"configTestData"+OsUtils.resolveOS().name()+".json"});
    }

    @Test
    public void getTemplateNotFound() throws Exception {
        mvc.perform(get("/static/template1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"Status\":404, \"Msg\":\"Static file Not Found\", \"Entity\":\"/template1\"}"));
    }

    @Test
    public void getStaticPng() throws Exception {
        MvcResult res = mvc.perform(get("/static/static.png"))
                .andExpect(status().isOk()).andReturn();
        assertEquals("image/png", res.getResponse().getHeader("Content-Type"));
        assertTrue(res.getResponse().getContentAsByteArray().length > 100);
    }

    @Test
    public void getStaticNestedJS() throws Exception {
        MvcResult res = mvc.perform(get("/static/dart/someScript.js"))
                .andExpect(status().isOk()).andReturn();
        assertEquals("text/javascript", res.getResponse().getHeader("Content-Type"));
        assertTrue(res.getResponse().getContentAsString().length() > 20);
    }

    @Test
    public void getStaticSvg() throws Exception {
        MvcResult res = mvc.perform(get("/static/static.svg"))
                .andExpect(status().isOk()).andReturn();
        assertEquals("image/svg+xml", res.getResponse().getHeader("Content-Type"));
        assertTrue(res.getResponse().getContentAsString().length() > 20);
    }

    @Test
    public void getStaticIcon() throws Exception {
        MvcResult res = mvc.perform(get("/static/favicon.ico"))
                .andExpect(status().isOk()).andReturn();
        assertEquals("image/x-icon", res.getResponse().getHeader("Content-Type"));
        assertTrue(res.getResponse().getContentAsByteArray().length > 100);
    }

    @Test
    public void getTemplateHtml() throws Exception {
        mvc.perform(get("/static/template.html"))
                .andExpect(status().isOk())
                .andExpect(content().string("This is a template userList = [stuart,shared,nonuser,test] poleForTime = 200000 historyMaxLen = 20;"));
    }

    /**
     * <pre>
     * There are two index001.html files. One in the resources directory
     * src/main/resources/templates and one in data/templates
     *
     * This test ensures that the one defined in data/templates overrides the other.
     *
     * This override is defined in the config 'resources -> locations -> templates'
     * "locations": {
     *    "logs": "data/logs",
     *    "cache": "data/cache",
     *    "scripts": "data/scripts",
     *    "templates" : "data/templates",
     *    "static" : "data/static",
     *    "audio": "audio"
     * },
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void getTemplateHtmlOverride() throws Exception {
        MvcResult res = mvc.perform(get("/static/index001.html"))
                .andExpect(status().isOk()).andReturn();
        assertEquals("text/html", res.getResponse().getHeader("Content-Type"));
        assertTrue(res.getResponse().getContentAsString().length() > 100);
        assertTrue(res.getResponse().getContentAsString().contains("index001-templates"));
    }

    /**
     * <pre>
     * There are two styles.css files. One in the resources directory
     * src/main/resources/static and one in data/static
     *
     * This test ensures that the one defined in data/static overrides the other.
     *
     * This override is defined in the config 'resources -> locations -> static'
     * "locations": {
     *    "logs": "data/logs",
     *    "cache": "data/cache",
     *    "scripts": "data/scripts",
     *    "templates" : "data/templates",
     *    "static" : "data/static",
     *    "audio": "audio"
     * },
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void getStaticCSSOverride() throws Exception {
        MvcResult res = mvc.perform(get("/static/styles.css"))
                .andExpect(status().isOk()).andReturn();
        assertEquals("text/css", res.getResponse().getHeader("Content-Type"));
        assertTrue(res.getResponse().getContentAsString().length() > 100);
        assertTrue(res.getResponse().getContentAsString().contains("Location   : data/static"));
    }

    @Test
    public void getTemplateFileIcomStatic() throws Exception {
        MvcResult res = mvc.perform(get("/static/styleStatic.css?height=999"))
                .andExpect(status().isOk()).andReturn();
        assertEquals("text/css", res.getResponse().getHeader("Content-Type"));
        String resp = res.getResponse().getContentAsString();
        /*
        Ensure it is NOT templated because it is in the static directory
        Note urls are the same apart from the file name!
         */
        assertTrue(resp.contains("height:%{height}%"));
    }

    @Test
    public void getTemplateFileCssTemplate() throws Exception {
        MvcResult res = mvc.perform(get("/static/styleTemplate.css?height=999"))
                .andExpect(status().isOk()).andReturn();
        assertEquals("text/css", res.getResponse().getHeader("Content-Type"));
        String resp = res.getResponse().getContentAsString();
        /*
        Ensure it is templated because it is in the template directory
        Note urls are the same apart from the file name!
         */
        assertTrue(resp.contains("height:999%"));
    }

    @Test
    public void getTemplateJs() throws Exception {
        MvcResult res = mvc.perform(get("/static/template.js"))
                .andExpect(status().isOk()).andReturn();
        assertEquals("text/javascript", res.getResponse().getHeader("Content-Type"));
        assertTrue(res.getResponse().getContentAsString().length() > 100);
    }

    @Test
    public void getTemplatePng() throws Exception {
        MvcResult res = mvc.perform(get("/static/template.png"))
                .andExpect(status().is4xxClientError()).andReturn();
        assertTrue(res.getResponse().getContentAsString().contains("Cannot template binary"));
    }

    @Test
    public void getTemplateSvg() throws Exception {
        MvcResult res = mvc.perform(get("/static/template.svg"))
                .andExpect(status().isOk()).andReturn();
        assertEquals("image/svg+xml", res.getResponse().getHeader("Content-Type"));
        assertTrue(res.getResponse().getContentAsString().length() > 20);
    }

    @Test
    public void getTemplateJpg() throws Exception {
        MvcResult res = mvc.perform(get("/static/template.jpg"))
                .andExpect(status().is4xxClientError()).andReturn();
        assertTrue(res.getResponse().getContentAsString().contains("Cannot template binary"));
    }

}
