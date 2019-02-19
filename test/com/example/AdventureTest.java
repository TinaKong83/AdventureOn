package com.example;

import org.junit.Test;
import org.junit.Before;

import java.net.MalformedURLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class AdventureTest {
    private Adventure adventure = new Adventure();
    private Player player = new Player();

    @Before
    public void setUp() throws Exception {
        Adventure.makeApiRequest("https://pastebin.com/raw/5G3Yqh9b");
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
                .getRoomFromDirection("NoRTHeast"));
        assertEquals("Woman's Building", adventure.getLayout()
                .getRooms()
                .get(0)
                .getRoomFromDirection("east"));
        assertEquals("Holmes Castle", adventure.getLayout()
                .getRooms()
                .get(6)
                .getRoomFromDirection("SOUTHeast"));
    }

    @Test
    public void roomFromDirectionBadInput() {
        assertNull(adventure.getLayout()
                .getRooms()
                .get(0)
                .getRoomFromDirection(null));
        assertNull(adventure.getLayout()
                .getRooms()
                .get(0)
                .getRoomFromDirection("up!!"));
        assertNull(adventure.getLayout()
                .getRooms()
                .get(3)
                .getRoomFromDirection(""));
        assertNull(adventure.getLayout()
                .getRooms()
                .get(6)
                .getRoomFromDirection("go east"));
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

    @Test
    public void roomInformationBadInput() {
        assertNull(adventure.roomMonstersInformation(null,
                null, null));
    }

    @Test
    public void descriptionAndDirection() {
        assertEquals("You are standing beneath the Ferris Wheel of the Chicago World Fair. " +
                        "It was the tallest attraction in the Exposition at 264 feet high. \n" +
                        "While riding the wheel, you can see a view of Lake Michigan.\n" +
                        "\n" +
                        "From here, you can go: East, Southeast",
                adventure.getRoomDescriptionAndDirections(adventure.getLayout().getRooms().get(0)));
        assertEquals("You are at Jackson Park, which contains serene Japanese gardens, woodland trails " +
                        "and beaches.\n" + "\n" + "From here, you can go: North, Southeast",
                adventure.getRoomDescriptionAndDirections(adventure.getLayout().getRooms().get(6)));
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
                        "You are standing beneath the Ferris Wheel of the Chicago World Fair. " +
                        "It was the tallest attraction " +
                        "in the Exposition at 264 feet high. \n" +
                        "While riding the wheel, you can see a view of Lake Michigan.\n" +
                        "\n" + "From here, you can go: East, Southeast",
                adventure.getWrongDirection("go west", adventure.getLayout()
                        .getRooms()
                        .get(0)));
        assertEquals("I can't go 'SB SJO biji'\n" +
                        "You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the tallest " +
                        "attraction in the Exposition at 264 feet high. \n" +
                        "While riding the wheel, you can see a view of Lake Michigan.\n" +
                        "\n" + "From here, you can go: East, Southeast",
                adventure.getWrongDirection("go SB SJO biji", adventure.getLayout()
                        .getRooms()
                        .get(0)));
        assertEquals("I can't go ''\n" +
                        "You are at Jackson Park, which contains serene Japanese gardens, woodland trails " +
                        "and beaches.\n" + "\n" + "From here, you can go: North, Southeast",
                adventure.getWrongDirection("go ", adventure.getLayout()
                        .getRooms()
                        .get(6)));
    }

    @Test
    public void printWrongDirectionBadInput() {
        assertNull(adventure.getWrongDirection(null, null));
    }

    @Test
    public void printInvalidCommandBadInput() {
        assertNull(adventure.getWrongDirection(null, null));
    }

    @Test
    public void printInvalidCommand() {
        assertEquals("I don't understand 'go'\n" +
                        "You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the tallest " +
                        "attraction in the Exposition at 264 feet high. \n" +
                        "While riding the wheel, you can see a view of Lake Michigan.\n" +
                        "\n" +
                        "From here, you can go: East, Southeast",
                adventure.getInvalidCommand("go", adventure.getLayout()
                        .getRooms()
                        .get(0)));
        assertEquals("I don't understand 'pO'\n" +
                        "You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the " +
                        "tallest attraction in the Exposition at 264 feet high. \n" +
                        "While riding the wheel, you can see a view of Lake Michigan.\n" +
                        "\n" + "From here, you can go: East, Southeast",
                adventure.getInvalidCommand("pO", adventure.getLayout()
                        .getRooms()
                        .get(0)));
        assertEquals("I don't understand 'go '\n" +
                        "You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the " +
                        "tallest attraction " + "in the Exposition at 264 feet high. \n" +
                        "While riding the wheel, you can see a view of Lake Michigan.\n" +
                        "\n" + "From here, you can go: East, Southeast",
                adventure.getInvalidCommand("go ", adventure.getLayout()
                        .getRooms()
                        .get(0)));
        assertEquals("I don't understand ' '\n" + "You are at Jackson Park, which contains serene " +
                        "Japanese gardens, woodland trails and beaches.\n" +
                        "\n" + "From here, you can go: North, Southeast",
                adventure.getInvalidCommand(" ", adventure.getLayout()
                        .getRooms()
                        .get(6)));
        assertEquals("I don't understand 'gophers are TASTY'\n" +
                        "You are at Jackson Park, which contains serene Japanese gardens, woodland trails " +
                        "and beaches.\n" + "\n" + "From here, you can go: North, Southeast",
                adventure.getInvalidCommand("gophers are TASTY", adventure.getLayout()
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

    @Test
    public void getItemInformation() {
        assertEquals("Your current items are: lockpick\n" +
                        "You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the tallest " +
                        "attraction in the Exposition at 264 feet high. \n" +
                        "While riding the wheel, you can see a view of Lake Michigan.\n" +
                        "\n" +
                        "From here, you can go: East, Southeast",
                adventure.getItemInformation("lockpick"));
        assertEquals("Your current items are: lockpick\n" +
                        "You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the tallest " +
                        "attraction" + " in the Exposition at 264 feet high. \n" +
                        "While riding the wheel, you can see a view of Lake Michigan.\n" +
                        "\n" +
                        "From here, you can go: East, Southeast",
                adventure.getItemInformation("bhbh"));
        assertEquals("Your current items are: lockpick\n" +
                        "You are standing beneath the Ferris Wheel of the Chicago World Fair. It was the tallest " +
                        "attraction " + "in the Exposition at 264 feet high. \n" +
                        "While riding the wheel, you can see a view of Lake Michigan.\n" +
                        "\n" + "From here, you can go: East, Southeast",
                adventure.getItemInformation(""));
    }

    @Test
    public void useItemWithDirection() {
        Room currentRoom = adventure.getLayout().getRooms().get(0);
        ArrayList<String> validKeyNames = new ArrayList<>();
        validKeyNames.add("lockpick");
        validKeyNames.add("soda");
        assertTrue(adventure.useItemWithDirection("use lockpick with east", currentRoom, validKeyNames));
        assertFalse(adventure.useItemWithDirection("use  with east", currentRoom, validKeyNames));
        assertFalse(adventure.useItemWithDirection("", currentRoom, validKeyNames));
        assertFalse(adventure.useItemWithDirection("use item with direction", currentRoom, validKeyNames));
        assertFalse(adventure.useItemWithDirection("blah blah blah blah", currentRoom, validKeyNames));
    }

    @Test
    public void itemExistsInRoom() {
        assertTrue(adventure.itemExistsInRoom("soda", adventure.getLayout().getRooms().get(0).getItems()));
        assertFalse(adventure.itemExistsInRoom("bull", adventure.getLayout().getRooms().get(0).getItems()));
        assertFalse(adventure.itemExistsInRoom("", adventure.getLayout().getRooms().get(0).getItems()));
        assertTrue(adventure.itemExistsInRoom("map", adventure.getLayout().getRooms().get(6).getItems()));
    }

    @Test
    public void playerHasItem() {
        assertFalse(adventure.playerHasItem("soda", adventure.getLayout().getPlayer().getItems()));
        assertFalse(adventure.playerHasItem("", adventure.getLayout().getPlayer().getItems()));
        assertTrue(adventure.playerHasItem("lockpick", adventure.getLayout().getPlayer().getItems()));
    }

    @Test
    public void hasValidKey() {
        ArrayList<String> validKeyNames = new ArrayList<>();
        assertFalse(adventure.hasValidKey(validKeyNames, adventure.getLayout().getPlayer().getItems()));
        validKeyNames.add("lockpick");
        validKeyNames.add("soda");
        assertTrue(adventure.hasValidKey(validKeyNames, adventure.getLayout().getPlayer().getItems()));
    }

}
