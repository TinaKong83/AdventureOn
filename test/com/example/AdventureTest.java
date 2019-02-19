package com.example;

import org.junit.Test;
import org.junit.Before;

import java.net.MalformedURLException;

import static org.junit.Assert.*;

public class AdventureTest {
    private Adventure adventure = new Adventure();
    private Player player = new Player();

    @Before
    public void setUp() throws Exception {
        Adventure.makeApiRequest("https://pastebin.com/raw/4uKfWzNL");
    }

    @Test(expected = MalformedURLException.class)
    public void testBadURL() throws Exception {
        Adventure.makeApiRequest("foobar");
        Adventure.makeApiRequest("");
        Adventure.makeApiRequest("https://courses.engr.illinois.edu/cs126/adventure/circular.json");
    }

    @Test
    public void roomFromDirectionGoodInput() {
        assertEquals("Palace Of Fine Arts", adventure.getLayout()
                .getRooms()
                .get(3)
                .roomFromDirection("NoRTHeast"));
        assertEquals("Woman's Building", adventure.getLayout()
                .getRooms()
                .get(0)
                .roomFromDirection("east"));
        assertEquals("Holmes Castle", adventure.getLayout()
                .getRooms()
                .get(6)
                .roomFromDirection("SOUTHeast"));
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
                .get(6)
                .roomFromDirection("go east"));
    }

    @Test
    public void possibleDirections() {
        assertEquals("East, Southeast", adventure.getLayout()
                .getRooms()
                .get(0)
                .possibleDirection());
        assertEquals("West, Northeast, North", adventure.getLayout()
                .getRooms().get(1)
                .possibleDirection());
        assertEquals("North, Southeast", adventure.getLayout().getRooms()
                .get(6)
                .possibleDirection());
    }

    @Test
    public void findDirectionInArrayFerrisWheelGoodInput() {
        Directions[] directionsArray = adventure.getLayout().getRooms().get(0).getDirections();
        assertTrue(adventure.findDirectionInArray(directionsArray, "go EAST"));
        assertTrue(adventure.findDirectionInArray(directionsArray, "GO EaST"));
    }

    @Test
    public void findDirectionInArrayFerrisWheelBadInput() {
        Directions[] directionsArray = adventure.getLayout().getRooms().get(0).getDirections();

        assertFalse(adventure.findDirectionInArray(directionsArray, "!"));
        assertFalse(adventure.findDirectionInArray(directionsArray, "go south"));
        assertFalse(adventure.findDirectionInArray(directionsArray, "SOSIOJEJGSB ONOENBOISENB"));
        assertFalse(adventure.findDirectionInArray(directionsArray, ""));
        assertFalse(adventure.findDirectionInArray(directionsArray, "east"));
        assertFalse(adventure.findDirectionInArray(directionsArray, null));
    }

    @Test
    public void findDirectionInArrayTransportBuildingGoodInput() {
        Directions[] directionsArray = adventure.getLayout().getRooms().get(3).getDirections();
        assertTrue(adventure.findDirectionInArray(directionsArray, "GO nOrthEast"));
    }

    @Test
    public void findDirectionInArrayTransportBuildingBadInput() {
        Directions[] directionsArray = adventure.getLayout().getRooms().get(3).getDirections();
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

    /*@Test
    public void roomInformationGoodInput() {
        /*assertEquals("There are no monsters in the room.\n" +
                        "You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the tallest " +
                        "exposition in the Exposition.\n" +
                        "From here, you can go: East, Southeast",
                adventure.roomInformation(adventure.getLayout().getPlayer(),
                        adventure.getLayout().getRooms().get(0),
                        adventure.getLayout().getRooms().get(0).getMonsterInRoom()));
        assertEquals("You are in the north hallway.  You can see Siebel 1112 and the door toward NCSA."
                        + "\n" + "From here, you can go: South, NorthEast",
                adventure.roomInformation(adventure.getLayout().getPlayer(),
                        adventure.getLayout().getRooms().get(3),
                        adventure.getLayout().getRooms().get(3).getMonsterInRoom()));
    }*/

    @Test
    public void roomInformationBadInput() {
        assertNull(adventure.roomInformation(null,
                null, null));
    }

    @Test
    public void descriptionAndDirection() {
        assertEquals("You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the " +
                        "tallest exposition in the Exposition.\n" + "From here, you can go: East, Southeast",
                adventure.descriptionAndDirections(adventure.getLayout().getRooms().get(0)));
        assertEquals("You are at Jackson Park, which contains serene Japanese gardens, woodland trails and " +
                        "beaches.\n" + "From here, you can go: North, Southeast",
                adventure.descriptionAndDirections(adventure.getLayout().getRooms().get(6)));
    }

    @Test
    public void listPlayerItems() {
        assertEquals("lockpick", adventure.listOfPlayerItems());
    }

    @Test
    public void userValidCommand() {
        String userInput = "use lockpick with east";
        String[] userInputArray = userInput.split(" ");
        assertTrue(adventure.userValidCommand(userInputArray));
    }

    @Test
    public void printWrongDirection() {
        assertEquals("I can't go 'west'\n" +
                "You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the tallest exposition in the Exposition.\n" +
                "From here, you can go: East, Southeast", adventure.printWrongDirection("go west", adventure.getLayout()
                .getRooms()
                .get(0)));
        assertEquals("I can't go 'SB SJO biji'\n" +
                "You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the tallest exposition in the Exposition.\n" +
                "From here, you can go: East, Southeast", adventure.printWrongDirection("go SB SJO biji",
                adventure.getLayout()
                        .getRooms()
                        .get(0)));
        assertEquals("I can't go ''\n" +
                        "You are at Jackson Park, which contains serene Japanese gardens, woodland trails and beaches.\n" +
                        "From here, you can go: North, Southeast",
                adventure.printWrongDirection("go ", adventure.getLayout()
                        .getRooms()
                        .get(6)));
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
        assertEquals("I don't understand 'go'\n" +
                "You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the tallest exposition in the Exposition.\n" +
                "From here, you can go: East, Southeast", adventure.printInvalidCommand("go", adventure.getLayout()
                .getRooms()
                .get(0)));
        assertEquals("I don't understand 'pO'\n" +
                        "You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the tallest " +
                        "exposition in the Exposition.\n" +
                        "From here, you can go: East, Southeast",
                adventure.printInvalidCommand("pO", adventure.getLayout()
                        .getRooms()
                        .get(0)));
        assertEquals("I don't understand 'go '\n" + "You are standing beneath the Ferris Wheel of the Chicago " +
                "World Fair. It was the tallest exposition in the Exposition.\n" + "From here, you can go: " +
                "East, Southeast", adventure.printInvalidCommand("go ", adventure.getLayout()
                .getRooms()
                .get(0)));
        assertEquals("I don't understand ' '\n" +
                        "You are at Jackson Park, which contains serene Japanese gardens, woodland trails and beaches.\n" +
                        "From here, you can go: North, Southeast",
                adventure.printInvalidCommand(" ", adventure.getLayout()
                        .getRooms()
                        .get(6)));
        assertEquals("I don't understand 'gophers are TASTY'\n" +
                        "You are at Jackson Park, which contains serene Japanese gardens, woodland trails and beaches.\n" +
                        "From here, you can go: North, Southeast",
                adventure.printInvalidCommand("gophers are TASTY", adventure.getLayout()
                        .getRooms()
                        .get(6)));
    }

    @Test
    public void reachedFinalRoom() {
        assertFalse(adventure.reachedFinalRoom(adventure.getLayout()
                .getRooms()
                .get(0)));
        assertTrue(adventure.reachedFinalRoom(adventure.getLayout()
                .getRooms()
                .get(5)));
        assertFalse(adventure.reachedFinalRoom(adventure.getLayout()
                .getRooms()
                .get(6)));
    }

    @Test
    public void reachedFinalRoomBadInput() {
        assertFalse(adventure.reachedFinalRoom(null));
    }
}
