package com.example;

import org.junit.Test;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class AdventureTest {
    private static Adventure adventure;

    @Before
    public void setUp() throws Exception {
        Gson gson = new Gson();
        Adventure.makeApiRequest("https://courses.engr.illinois.edu/cs126/adventure/siebel.json");
        adventure = gson.fromJson(com.example.Data.getFileContentsAsString("Data/siebel.json"), Adventure.class);
    }

    @Test(expected = MalformedURLException.class)
    public void testBadURL() throws Exception {
        Adventure.makeApiRequest("foobar");
    }

    @Test
    public void roomFromDirectionString() {
        assertEquals("SiebelEntry", adventure.getCurrentRoom().roomFromDirection("east"));
    }

    /*@Test
    public void roomFromDirection() {
        System.out.println(adventure.getCurrentRoom().getName());
        assertEquals("AcmOffice", adventure.getCurrentRoom().roomFromDirection("northeast"));
    }*/
}
