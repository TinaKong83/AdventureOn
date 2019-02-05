package com.example;

import org.junit.Test;
import org.junit.Before;

import java.net.MalformedURLException;

import static org.junit.Assert.*;

public class AdventureTest {
    private static Adventure adventure = new Adventure();

    @Before
    public void setUp() throws Exception {
        adventure.makeApiRequest("https://courses.engr.illinois.edu/cs126/adventure/siebel.json");
    }

    @Test(expected = MalformedURLException.class)
    public void testBadURL() throws Exception {
        Adventure.makeApiRequest("foobar");
        Adventure.makeApiRequest("");
        Adventure.makeApiRequest("https://courses.engr.illinois.edu/cs126/adventure/circular.json");
    }

    @Test
    public void roomFromDirectionGoodInput() {
        assertEquals("Siebel1112", adventure.getLayout()
                .getRooms()
                .get(3)
                .roomFromDirection("NoRTHeast"));
        assertEquals("SiebelEntry", adventure.getLayout()
                .getRooms()
                .get(0)
                .roomFromDirection("east"));
        assertEquals("SiebelEastHallway", adventure.getLayout()
                .getRooms()
                .get(7)
                .roomFromDirection("Up"));
    }

    @Test
    public void roomFromDirectionBadInput() {
        assertNull(adventure.getLayout()
                .getRooms()
                .get(0)
                .roomFromDirection(null));
        assertNull(adventure.getLayout()
                .getRooms()
                .get(0)
                .roomFromDirection("up!!"));
        assertNull(adventure.getLayout()
                .getRooms()
                .get(3)
                .roomFromDirection(""));
        assertNull(adventure.getLayout()
                .getRooms()
                .get(7)
                .roomFromDirection("go east"));
    }

    @Test
    public void possibleDirections() {
        assertEquals("East", adventure.getLayout()
                .getRooms()
                .get(0)
                .possibleDirection());
        assertEquals("West, Northeast, North, East", adventure.getLayout()
                .getRooms().get(1)
                .possibleDirection());
        assertEquals("Up", adventure.getLayout().getRooms()
                .get(7)
                .possibleDirection());
    }

    @Test
    public void findDirectionInArrayMatthewsStreetGoodInput() {
        String[] directionsArray = adventure.getLayout().getRooms().get(0).possibleDirection().split(", ");
        assertTrue(adventure.findDirectionInArray(directionsArray, "go EAST"));
        assertTrue(adventure.findDirectionInArray(directionsArray, "GO EaST"));
    }

    @Test
    public void findDirectionInArrayMatthewsStreetBadInput() {
        String[] directionsArray = adventure.getLayout().getRooms().get(0).possibleDirection().split(", ");

        assertFalse(adventure.findDirectionInArray(directionsArray, "!"));
        assertFalse(adventure.findDirectionInArray(directionsArray, "go south"));
        assertFalse(adventure.findDirectionInArray(directionsArray, "SOSIOJEJGSB ONOENBOISENB"));
        assertFalse(adventure.findDirectionInArray(directionsArray, ""));
        assertFalse(adventure.findDirectionInArray(directionsArray, "east"));
        assertFalse(adventure.findDirectionInArray(directionsArray, null));
    }

    @Test
    public void findDirectionInArraySiebelEastHallwayGoodInput() {
        String[] directionsArray = adventure.getLayout().getRooms().get(3).possibleDirection().split(", ");
        assertTrue(adventure.findDirectionInArray(directionsArray, "GO nOrthEast"));
    }

    @Test
    public void findDirectionInArraySiebelEastHallwayBadInput() {
        String[] directionsArray = adventure.getLayout().getRooms().get(3).possibleDirection().split(", ");
        assertFalse(adventure.findDirectionInArray(directionsArray, "south"));
        assertFalse(adventure.findDirectionInArray(directionsArray, "go south!!"));
        assertFalse(adventure.findDirectionInArray(directionsArray, "@"));
        assertFalse(adventure.findDirectionInArray(directionsArray, ""));
        assertFalse(adventure.findDirectionInArray(null, null));
    }

    @Test
    public void userEndsGameGoodInput() {
        assertTrue(adventure.userEndsGame("exit"));
        assertTrue(adventure.userEndsGame("qUiT"));
        assertTrue(adventure.userEndsGame("quit"));
    }

    @Test
    public void userEndsGameBadInput() {
        assertFalse(adventure.userEndsGame(""));
        assertFalse(adventure.userEndsGame("QUIT THE GAME"));
        assertFalse(adventure.userEndsGame(null));
    }

    @Test
    public void roomInformationGoodInput() {
        assertEquals("You are on Matthews, outside the Siebel Center\n" + "From here, you can go: East",
                adventure.roomInformation(adventure.getLayout()
                        .getRooms()
                        .get(0)));
        assertEquals("You are in the north hallway.  You can see Siebel 1112 and the door toward NCSA."
                        + "\n" + "From here, you can go: South, NorthEast",
                adventure.roomInformation(adventure.getLayout()
                        .getRooms()
                        .get(3)));
    }

    @Test
    public void roomInformationBadInput() {
        assertNull(adventure.roomInformation(null));
    }

    @Test
    public void printWrongDirection() {
        assertEquals("I can't go 'west'\n" + "You are on Matthews, outside the Siebel Center\n" +
                "From here, you can go: East", adventure.printWrongDirection("go west", adventure.getLayout()
                .getRooms()
                .get(0)));
        assertEquals("I can't go 'SB SJO biji'\n" + "You are on Matthews, outside the Siebel Center\n" +
                "From here, you can go: East", adventure.printWrongDirection("go SB SJO biji",
                adventure.getLayout()
                        .getRooms()
                        .get(0)));
        assertEquals("I can't go ''\nYou are in the basement of Siebel.  You see tables with students " +
                        "working and door to computer labs.\n" + "From here, you can go: Up",
                adventure.printWrongDirection("go ", adventure.getLayout()
                        .getRooms()
                        .get(7)));
    }

    @Test
    public void printWrongDirectionBadInput() {
        assertNull(adventure.printWrongDirection(null, null));
    }

    @Test
    public void printInvalidCommandBadInput() {
        assertNull(adventure.printWrongDirection(null, null));
    }

    @Test
    public void printInvalidCommand() {
        assertEquals("I don't understand 'go'\n" + "You are on Matthews, outside the Siebel Center\n" +
                "From here, you can go: East", adventure.printInvalidCommand("go", adventure.getLayout()
                .getRooms()
                .get(0)));
        assertEquals("I don't understand 'pO'\n" + "You are on Matthews, outside the Siebel Center\n" +
                "From here, you can go: East", adventure.printInvalidCommand("pO", adventure.getLayout()
                .getRooms()
                .get(0)));
        assertEquals("I don't understand 'go '\n" + "You are on Matthews, outside the Siebel Center\n" +
                "From here, you can go: East", adventure.printInvalidCommand("go ", adventure.getLayout()
                .getRooms()
                .get(0)));
        assertEquals("I don't understand ' '\n" + "You are in the basement of Siebel.  You see tables " +
                        "with students working and door to computer labs.\n" + "From here, you can go: Up",
                adventure.printInvalidCommand(" ", adventure.getLayout()
                        .getRooms()
                        .get(7)));
        assertEquals("I don't understand 'gophers are TASTY'\n" + "You are in the basement of Siebel.  You " +
                        "see tables with students working and door to computer labs.\n" + "From here, you can go: Up",
                adventure.printInvalidCommand("gophers are TASTY", adventure.getLayout()
                        .getRooms()
                        .get(7)));
        assertEquals("I don't understand 'UPpPP'\n" + "You are in the basement of Siebel.  You see tables " +
                        "with students working and door to computer labs.\n" + "From here, you can go: Up",
                adventure.printInvalidCommand("UPpPP", adventure.getLayout()
                        .getRooms()
                        .get(7)));
    }

    @Test
    public void reachedFinalRoom() {
        assertFalse(adventure.reachedFinalRoom(adventure.getLayout()
                .getRooms()
                .get(0)));
        assertFalse(adventure.reachedFinalRoom(adventure.getLayout()
                .getRooms()
                .get(7)));
        assertTrue(adventure.reachedFinalRoom(adventure.getLayout()
                .getRooms()
                .get(6)));
    }

    @Test
    public void reachedFinalRoomBadInput() {
        assertFalse(adventure.reachedFinalRoom(null));
    }
}
