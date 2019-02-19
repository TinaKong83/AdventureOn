package com.example;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * A simple text-based “adventure game” that takes a description of the world in JSON and lets
 * a user interactively navigate through the world.
 *
 * @see <a href="https://courses.engr.illinois.edu/cs126/sp2019/assignments/adventure.pdf</a>
 **/
public class Adventure {
    private static final int STATUS_OK = 200;
    private static final int COMMAND_INDEX = 3;
    private static Adventure adventure = new Adventure();
    private static Layout layout;
    private static Player player;
    private static Room currentRoom;
    private static Directions directionCommand;
    private static boolean gameEnded = false;
    private static final int ITEM_COMMAND_MAX = 4;
    private static final int DIRECTION_COMMAND_MAX = 2;

    public Directions getDirectionCommand() {
        return directionCommand;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Player getPlayer() {
        return player;
    }

    public Layout getLayout() {
        return layout;
    }

    //Citation: https://stackoverflow.com/questions/5204051/how-to-calculate-the-running-time-of-my-program
    //Citation: https://en.wikipedia.org/wiki/World%27s_Columbian_Exposition
    public static void main(String[] arguments) throws Exception {
        long start = System.currentTimeMillis();
        final String COMMAND_URL = arguments[0];
        adventure.testAlternateUrl(COMMAND_URL);
        adventure.beginGame();
        adventure.playGame();
        long end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.000");
        System.out.print("\nWhile alive, you spent " + formatter.format((end - start) / 1000d) +
                " seconds exploring the White City.");
    }

    /**
     * Method that checks runs a single game.
     **/
    public void playGame() {
        Scanner scanner = new Scanner(System.in);
        while (!gameEnded) {
            String originalInput = scanner.nextLine();
            String userInput = originalInput.toLowerCase();
            if (userEndsGame(originalInput) || reachedFinalRoom(currentRoom)) {
                System.out.println("While trying to run away, you were murdered by the serial killer H.H. Holmes.");
                System.out.println("Restart the game to find a special item that may help you defeat him.");
                break;
            }
            String[] userInputArray = userInput.split(" ");
            if (!userValidCommand(userInputArray)) {
                System.out.println(getInvalidCommand(originalInput, currentRoom));
            } else if (userInputArray[0].toLowerCase().equals("go")
                    && findDirectionInArray(currentRoom.getDirections(), userInput)) {
                userEnablesDirection();
            } else if (userInputArray[0].toLowerCase().equals("go")
                    && !findDirectionInArray(currentRoom.getDirections(), userInput)) {
                System.out.println(getWrongDirection(originalInput, currentRoom));
            } else if (findDirectionInArray(currentRoom.getDirections(), userInput)
                    && useItemWithDirection(userInput, currentRoom, directionCommand.getValidKeyNames())) {
                currentRoom = moveToNewRoom(directionCommand.getDirectionName(), currentRoom);
                System.out.println(roomMonstersInformation(player, currentRoom, currentRoom.getMonsterInRoom()));
            } else if (userInputArray[0].toLowerCase().equals("pickup")) {
                examineItem(userInputArray);
            } else {
                System.out.println(getInvalidCommand(originalInput, currentRoom));
            }
        }
    }

    /**
     * Method that checks if a room is unlocked. If unlocked, the player proceeds to the next room and battles monsters
     * if monsters exist. Otherwise, they must have a valid item.
     **/
    public void userEnablesDirection() {
        if (directionCommand.getEnabled().equals("true")) {
            currentRoom = moveToNewRoom(directionCommand.getDirectionName(), currentRoom);
            System.out.println(roomMonstersInformation(player, currentRoom, currentRoom.getMonsterInRoom()));
        } else {
            playerUnlocksDirection(directionCommand);
        }
    }

    /**
     * Method that allows user to specify an alternate URL.
     * If the URL is invalid, default to original URL.
     **/
    public void testAlternateUrl(String urlCommand) throws Exception {
        try {
            makeApiRequest(urlCommand);
        } catch (UnirestException e) {
            System.out.println("Network not responding");
        } catch (MalformedURLException e) {
            System.out.println("Bad URL: " + urlCommand);
            System.out.println("We will be defaulting to the file instead" + "\n");
            adventure.setUp("Data/adventure2.json");
            //makeApiRequest(urlCommand);
        }
    }

    /**
     * Method that states the description and possible directions from the starting room.
     *
     * @return String of starting room information.
     **/
    public void beginGame() {
        System.out.println("Welcome to the 1893 Chicago World Fair! The Chicago Fair is a celebration of the " +
                "400th anniversary of Columbus's arrival to the New World, \nand a cultural reflection of American " +
                "science, art and industry.");
        System.out.println("The exposition features over 200 buildings of predominantly neoclassical architecture, " +
                "canals and lagoons, and over 27 million visitors");
        System.out.println("Your journey in the magical White City begins here...\n");
        System.out.println(currentRoom.getDescription());
        System.out.println("From here, you can go: " + currentRoom.possibleDirection());
    }

    /**
     * @param fileName the name of the json file.
     *                 Method parses JSON Data as a file.
     **/
    public void setUp(String fileName) throws Exception {
        Gson gson = new Gson();
        layout = gson.fromJson(Data.getFileContentsAsString(fileName), Layout.class);
        player = layout.getPlayer();
        currentRoom = layout.getRoomObjectFromName(layout.getStartingRoom());
    }

    /**
     * @param currentRoom the current room.
     *                    Method retrieves the room description and possible directions.
     * @return String.
     **/
    public String getRoomDescriptionAndDirections(Room currentRoom) {
        String getRoomInstruction = currentRoom.getDescription();
        getRoomInstruction = getRoomInstruction + "\n" + "\nFrom here, you can go: " + currentRoom.possibleDirection();
        return getRoomInstruction;
    }

    /**
     * @param directionCommand the direction a player wishes to head towards.
     *                         If a room is locked, asks the player to use a valid key with the direction.
     **/
    public void playerUnlocksDirection(Directions directionCommand) {
        System.out.println("The player needs an item to unlock this direction.");
        System.out.println("Valid keys are: " + directionCommand.getValidKeyNames());
        if (!hasValidKey(directionCommand.getValidKeyNames(), player.getItems())) {
            System.out.println("The user does not have a valid key for "
                    + directionCommand.getDirectionName());
            System.out.println(getRoomDescriptionAndDirections(currentRoom));
        } else {
            System.out.println("You have a valid item. To access this direction, enter " +
                    "'use item with direction'");
        }
    }

    public String getItemInformation(String itemCommand) {
        Item itemObject = currentRoom.getItemObjectFromName(itemCommand, currentRoom.getItems());
        if (itemObject != null) {
            pickUpItem(itemObject, currentRoom.getItems(), player.getItems());
        }
        return "Your current items are: " + listOfPlayerItems() + "\n" + getRoomDescriptionAndDirections(currentRoom);
    }

    public void examineItem(String[] userInputArray) {
        String itemCommand = userInputArray[1];
        if (itemExistsInRoom(itemCommand, currentRoom.getItems())) {
            System.out.println(getItemInformation(itemCommand));
        } else {
            System.out.println("This item does not exist.");
            System.out.println(getRoomDescriptionAndDirections(currentRoom));
        }
    }

    public String listOfPlayerItems() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < player.getItems().size() - 1; i++) {
            stringBuilder.append(player.getItems().get(i).getName());
            stringBuilder.append(", ");
        }
        stringBuilder.append(player.getItems().get(player.getItems().size() - 1).getName());
        return stringBuilder.toString();
    }

    /**
     * Method that determines if the user's inputted command contains a valid direction.
     *
     * @param directionsArray, an array of possible directions one can move from the current room.
     * @param userInput,       the direction command the user inputs (e.g. go east, go west).
     * @return boolean.
     **/
    public boolean findDirectionInArray(Directions[] directionsArray, String userInput) {
        if (userInput == null || directionsArray == null) {
            return false;
        }
        String[] userInputArray = userInput.toLowerCase().split(" ");
        userInput = userInputArray[userInputArray.length - 1].toLowerCase();
        if (userInputArray.length != DIRECTION_COMMAND_MAX && userInputArray.length != ITEM_COMMAND_MAX) {
            return false;
        }
        for (int i = 0; i < directionsArray.length; i++) {
            if (directionsArray[i] != null && directionsArray[i].getDirectionName().toLowerCase().equals(userInput)) {
                directionCommand = directionsArray[i];
                return true;
            }
        }
        return false;
    }

    public boolean userValidCommand(String[] userInputArray) {
        if (userInputArray.length < DIRECTION_COMMAND_MAX) {
            return false;
        }
        if (userInputArray[0].toLowerCase().equals("go") || userInputArray[0].toLowerCase().equals("pickup")) {
            return true;
        }
        if (userInputArray.length >= ITEM_COMMAND_MAX && userInputArray[0].toLowerCase().equals("use")
                && userInputArray[2].toLowerCase().equals("with")) {
            return true;
        }
        return false;
    }

    public boolean useItemWithDirection(String userInput, Room currentRoom, ArrayList<String> validKeyNames) {
        String[] userInputArray = userInput.split(" ");
        if (userInputArray.length < ITEM_COMMAND_MAX) {
            return false;
        }
        String itemName = userInputArray[1].toLowerCase();
        if (validKeyNames.contains(itemName) && userInputArray[0].toLowerCase().equals("use")
                && userInputArray[2].equals("with") && playerHasItem(itemName, player.getItems())) {
            return true;
        }
        return false;
    }

    public Room moveToNewRoom(String currentDirectionName, Room currentRoom) {
        String newRoomName = currentRoom.getRoomFromDirection(currentDirectionName);
        currentRoom = layout.getRoomObjectFromName(newRoomName);
        return currentRoom;
    }

    /**
     * Method that checks if the user has reached the ending room.
     *
     * @param currentRoom the room the user is currently in.
     * @return boolean.
     **/
    public boolean reachedFinalRoom(Room currentRoom) {
        if (currentRoom == null) {
            return false;
        }
        return currentRoom.getName().equals(layout.getEndingRoom());
    }

    /**
     * Method that checks if the user has typed in "quit" or "enter", to end the program.
     *
     * @param userInput command inputted by the user.
     * @return boolean.
     **/
    public boolean userEndsGame(String userInput) {
        if (userInput == null) {
            return false;
        }
        userInput = userInput.toLowerCase();
        return userInput.equals("quit") || userInput.equals("exit");
    }

    /**
     * Method that states the room's description and possible directions the user can move.
     *
     * @param currentRoom the room the user is currently in.
     * @return String.
     **/

    public String roomMonstersInformation(Player player, Room currentRoom, Monster monsterInRoom) {
        StringBuilder roomInformation = new StringBuilder();
        if (currentRoom == null) {
            return null;
        }
        if (currentRoom.getMonsterInRoom().getHealth() == 0) {
            return "There are no monsters in the room.\n" + currentRoom.getDescription()
                    + "\n" + "\nFrom here, you can go: " + currentRoom.possibleDirection();
        } else {
            roomInformation.append("The monster in this room is: " + monsterInRoom.getName() + "\n");
            player.fightMonster(monsterInRoom, player, currentRoom);
        }
        roomInformation.append(currentRoom.getDescription() + "\n" + "\nFrom here, you can go: "
                + currentRoom.possibleDirection());
        return roomInformation.toString();
    }

    /**
     * Method that states when a user enters an invalid command, if the command begins with 'go'.
     *
     * @param currentRoom the room the user is currently in.
     * @param userInput   the direction command the user inputs (e.g. go east, go west).
     * @return String.
     **/
    public String getWrongDirection(String userInput, Room currentRoom) {
        if (userInput == null || currentRoom == null) {
            return null;
        }
        return "I can't go '" + userInput.substring(COMMAND_INDEX) + "'\n"
                + getRoomDescriptionAndDirections(currentRoom);
    }

    /**
     * Method that states when a user enters an invalid command, and command doesn't begin with 'go'.
     *
     * @param currentRoom the room the user is currently in.
     * @param userInput   the direction command the user inputs (e.g. go east, go west).
     * @return String.
     **/
    public String getInvalidCommand(String userInput, Room currentRoom) {
        if (userInput == null || currentRoom == null) {
            return null;
        }
        return "I don't understand '" + userInput + "'\n"
                + getRoomDescriptionAndDirections(currentRoom);
    }

    public boolean itemExistsInRoom(String itemToPickUp, ArrayList<Item> availableRoomItems) {
        for (int i = 0; i < availableRoomItems.size(); i++) {
            if (availableRoomItems.get(i).getName().equals(itemToPickUp)) {
                return true;
            }
        }
        return false;
    }

    public boolean playerHasItem(String itemName, ArrayList<Item> playerItems) {
        for (int i = 0; i < playerItems.size(); i++) {
            if (playerItems.get(i).getName().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    public void pickUpItem(Item itemToPickUp, ArrayList<Item> availableRoomItems, ArrayList<Item> playerItems) {
        for (int i = 0; i < availableRoomItems.size(); i++) {
            if (availableRoomItems.get(i).getName().equals(itemToPickUp.getName())) {
                playerItems.add(itemToPickUp);
            }
        }
    }

    public boolean hasValidKey(ArrayList<String> validKeyNames, ArrayList<Item> playerItems) {
        for (int i = 0; i < validKeyNames.size(); i++) {
            for (int j = 0; j < playerItems.size(); j++) {
                if (validKeyNames.get(i).equals(playerItems.get(j).getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    static void makeApiRequest(String url) throws UnirestException, MalformedURLException {
        final HttpResponse<String> stringHttpResponse;

        // This will throw MalformedURLException if the url is malformed.
        new URL(url);

        stringHttpResponse = Unirest.get(url).asString();
        // Check to see if the request was successful; if so, convert the payload JSON into Java objects
        if (stringHttpResponse.getStatus() == STATUS_OK) {
            String json = stringHttpResponse.getBody();
            Gson gson = new Gson();
            layout = gson.fromJson(json, Layout.class);
            player = layout.getPlayer();
            currentRoom = layout.getRoomObjectFromName(layout.getStartingRoom());
        }
    }
}

