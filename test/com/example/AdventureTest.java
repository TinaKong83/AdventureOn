package com.example;

import org.junit.Rule;
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

    @Test
    public void findDirectionInArrayMatthewsStreet() {
        String[] directionsArray = adventure.getLayout().getRooms().get(0).possibleDirection().split(", ");

        assertTrue(adventure.findDirectionInArray(directionsArray, "go EAST"));
        assertFalse(adventure.findDirectionInArray(directionsArray, "go south"));
        assertFalse(adventure.findDirectionInArray(directionsArray, ""));
        assertFalse(adventure.findDirectionInArray(directionsArray, "east"));
        assertFalse(adventure.findDirectionInArray(directionsArray, null));
    }

    @Test
    public void findDirectionInArraySiebelEastHallway() {
        String[] directionsArray = adventure.getLayout().getRooms().get(3).possibleDirection().split(", ");

        assertFalse(adventure.findDirectionInArray(directionsArray, "south"));
        assertFalse(adventure.findDirectionInArray(directionsArray, "go south!!"));
        assertFalse(adventure.findDirectionInArray(directionsArray, ""));
        assertTrue(adventure.findDirectionInArray(directionsArray, "GO northeast"));
        assertFalse(adventure.findDirectionInArray(null, null));
    }

    @Test
    public void userEndsGame() {
        assertTrue(adventure.userEndsGame("exit"));
        assertTrue(adventure.userEndsGame("qUiT"));
        assertTrue(adventure.userEndsGame("quit"));
        assertFalse(adventure.userEndsGame(""));
        assertFalse(adventure.userEndsGame(null));
    }

    @Test
    public void roomInformation() {
        assertEquals("You are on Matthews, outside the Siebel Center" + "\n" + "From here, you can go: East",
                adventure.roomInformation(adventure.getLayout().getRooms().get(0)));
        assertEquals("You are in the north hallway.  You can see Siebel 1112 and the door toward NCSA." + "\n" + "From here, you can go: South, NorthEast",
                adventure.roomInformation(adventure.getLayout().getRooms().get(3)));
    }






}
