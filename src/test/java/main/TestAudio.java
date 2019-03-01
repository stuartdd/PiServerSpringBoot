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

import io.AudioStatusIO;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
import tools.JsonUtils;
import tools.OsUtils;

/**
 *
 * @author stuart
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class TestAudio {

    @Autowired
    private MockMvc mvc;

    private static final String JSON = "{\"action\":\"query\",\"status\":\"PLAYING\",\"message\":\"dog.mp3\",\"duration\":14.24,\"position\":5.03}";

    @BeforeClass
    public static void beforeClass() {
        ConfigDataManager.init(new String[]{"configTestData" + OsUtils.resolveOS().name().toUpperCase() + ".json"});
    }

    @Test
    @Ignore
    public void testInOrder() throws Exception {
        testAudioStatusJsonLoad();
        getStatusWhenNotStarted();
        stop();
        playWithPauseTrackProgress();
        playWithVolumeTrackProgress();
    }

    
    public void testAudioPlay() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/audio/play/BackDoorOpened.mp3?vol=75")).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
    }
    
    public void playWithPauseTrackProgress() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/audio/play/dog.mp3?vol=75")).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
        AudioStatusIO audioStatus = (AudioStatusIO) JsonUtils.beanFromJson(AudioStatusIO.class, resp);
        assertEquals("play", audioStatus.getAction());
        assertEquals("dog.mp3", audioStatus.getMessage());
        assertEquals("PLAYING", audioStatus.getStatus());
        Thread.sleep(1000);

        mvcResult = mvc.perform(get("/audio/pause/true")).andExpect(status().isOk()).andReturn();
        resp = mvcResult.getResponse().getContentAsString();
        audioStatus = (AudioStatusIO) JsonUtils.beanFromJson(AudioStatusIO.class, resp);
        assertEquals("pause", audioStatus.getAction());
        assertEquals("dog.mp3", audioStatus.getMessage());
        assertEquals("PAUSED", audioStatus.getStatus());
        Thread.sleep(1000);

        mvcResult = mvc.perform(get("/audio/pause/false")).andExpect(status().isOk()).andReturn();
        resp = mvcResult.getResponse().getContentAsString();
        audioStatus = (AudioStatusIO) JsonUtils.beanFromJson(AudioStatusIO.class, resp);
        assertEquals("pause", audioStatus.getAction());
        assertEquals("dog.mp3", audioStatus.getMessage());
        assertEquals("PLAYING", audioStatus.getStatus());
        Thread.sleep(1000);
        stop();
    }

    public void testAudioStatusJsonLoad() {
        AudioStatusIO audioStatus = (AudioStatusIO) JsonUtils.beanFromJson(AudioStatusIO.class,
                JSON);
        assertEquals("query", audioStatus.getAction());
        assertEquals("PLAYING", audioStatus.getStatus());
        assertEquals("dog.mp3", audioStatus.getMessage());
        assertEquals(14.24, audioStatus.getDuration(), 0.001);
        assertEquals(5.03, audioStatus.getPosition(), 0.001);
    }

    public void stop() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/audio/stop")).andExpect(status().isOk()).andReturn();
        AudioStatusIO audioStatus = (AudioStatusIO) JsonUtils.beanFromJson(AudioStatusIO.class, mvcResult.getResponse().getContentAsString());
        assertEquals("stop", audioStatus.getAction());
        assertEquals("STOPPED", audioStatus.getStatus());
        assertEquals(0.0, audioStatus.getDuration(), 0.0);
        assertEquals(0.0, audioStatus.getPosition(), 0.0);
    }

    public void playWithVolumeTrackProgress() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/audio/play/dog.mp3?vol=40")).andExpect(status().isOk()).andReturn();
        String resp = mvcResult.getResponse().getContentAsString();
        System.out.println(resp);
        AudioStatusIO audioStatus = (AudioStatusIO) JsonUtils.beanFromJson(AudioStatusIO.class, resp);
        assertEquals("play", audioStatus.getAction());
        assertEquals("dog.mp3", audioStatus.getMessage());
        assertEquals("PLAYING", audioStatus.getStatus());
        assertEquals(40, audioStatus.getVolume());
        assertTrue(audioStatus.getDuration() > 0);
        Thread.sleep(1000);

        mvcResult = mvc.perform(get("/audio/status")).andExpect(status().isOk()).andReturn();
        resp = mvcResult.getResponse().getContentAsString();
        audioStatus = (AudioStatusIO) JsonUtils.beanFromJson(AudioStatusIO.class, resp);
        assertTrue(audioStatus.getDuration() > 0);
        double pos = audioStatus.getPosition();
        assertTrue(pos > 0);
        assertEquals("query", audioStatus.getAction());
        assertEquals("dog.mp3", audioStatus.getMessage());
        assertEquals("PLAYING", audioStatus.getStatus());
        assertEquals(40, audioStatus.getVolume());
        Thread.sleep(1000);

        mvcResult = mvc.perform(get("/audio/status"))
                .andExpect(status().isOk()).andReturn();
        resp = mvcResult.getResponse().getContentAsString();
        audioStatus = (AudioStatusIO) JsonUtils.beanFromJson(AudioStatusIO.class, resp);
        assertTrue(audioStatus.getDuration() > 0);
        assertTrue(audioStatus.getPosition() > pos);
        assertEquals("query", audioStatus.getAction());
        assertEquals("dog.mp3", audioStatus.getMessage());
        assertEquals("PLAYING", audioStatus.getStatus());
        assertEquals(40, audioStatus.getVolume());
        Thread.sleep(1000);
        stop();
    }

    @Test
    public void getStatusWhenNotStarted() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/audio/status")).andExpect(status().isOk()).andReturn();
        AudioStatusIO audioStatus = (AudioStatusIO) JsonUtils.beanFromJson(AudioStatusIO.class, mvcResult.getResponse().getContentAsString());
        assertEquals("query", audioStatus.getAction());
        assertEquals("STOPPED", audioStatus.getStatus());
        assertEquals(0.0, audioStatus.getDuration(), 0.0);
        assertEquals(0.0, audioStatus.getPosition(), 0.0);
        assertEquals(99, audioStatus.getVolume());
    }

}
