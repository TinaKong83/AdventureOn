package com.example;

import org.junit.Rule;
import org.junit.Test;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Scanner;

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
    public void roomFromDirectionACMOffice() {
        assertEquals("SiebelEntry", adventure.getLayout().getRooms().get(2).roomFromDirection("South"));
    }

    @Test
    public void roomFromDirectionSiebelNorthHallway() {
        assertEquals("Siebel1112", adventure.getLayout().getRooms().get(3).roomFromDirection("NorthEast"));
    }

    @Test
    public void roomFromDirectionMatthewsStreet() {
        //System.out.println(adventure.getCurrentRoom().getName());
        assertEquals("SiebelEntry", adventure.getLayout().getRooms().get(0).roomFromDirection("east"));
    }

    @Test
    public void roomFromDirectionSiebelEntry() {
        assertEquals("SiebelNorthHallway", adventure.getLayout().getRooms().get(1).roomFromDirection("north"));
        assertEquals("AcmOffice", adventure.getLayout().getRooms().get(1).roomFromDirection("northeast"));
    }

    @Test
    public void roomFromDirectionSiebelEastHallway() {
        assertEquals("SiebelBasement", adventure.getLayout().getRooms().get(5).roomFromDirection("Down"));
    }

    @Test
    public void roomFromDirectionSiebelBasement() {
        assertEquals("SiebelEastHallway", adventure.getLayout().getRooms().get(7).roomFromDirection("up"));
    }

    @Test
    public void possibleDirectionsMatthewsStreet() {
        assertEquals("East", adventure.getLayout().getRooms().get(0).possibleDirection());
    }

    @Test
    public void possibleDirectionsSiebelEntry() {
        assertEquals("West, Northeast, North, East", adventure.getLayout().getRooms().get(1).possibleDirection());
    }

    @Test
    public void possibleDirectionsSiebelEastHallway() {
        assertEquals("West, South, Down", adventure.getLayout().getRooms().get(5).possibleDirection());
    }

    @Test
    public void possibleDirectionsSiebelBasement() {
        assertEquals("Up", adventure.getLayout().getRooms().get(7).possibleDirection());
    }

}
