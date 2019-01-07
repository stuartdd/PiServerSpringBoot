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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class TestUsers {

    @BeforeClass
    public static void beforeClass() {
        ConfigDataManager.init(new String[]{"configTestData.json"});
    }

    @Autowired
    private MockMvc mvc;

    @Test
    public void getUserData() throws Exception {
        mvc.perform(get("/userdata/stuart"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"imagesPerRow\":2,\"imageHistory\":[\"stuart/PixelPhoneSync/2018_12_27_13_48_08_IMG_20181227_134807.jpg.jpg\",\"stuart/Archive/pics/2017_01_02_19_22_28_015_Mum8.jpg.jpg\"]}"));
    }
    
    @Test
    public void getNonUserData() throws Exception {
        mvc.perform(get("/userdata/nonuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"imagesPerRow\":2,\"imageHistory\":[]}"));
    }

    @Test
    public void getUserDataNotFound() throws Exception {
        mvc.perform(get("/userdata/tony"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"Status\":404, \"Msg\":\"User Not Found\", \"Entity\":\"tony\"}"));
    }

}
