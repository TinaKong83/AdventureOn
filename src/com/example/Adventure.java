package com.example;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

//enabled: if true, by default the room is open
//if false, you need an item to unlock that room

/**
 * A simple text-based “adventure game” that takes a description of the world in JSON and lets
 * a user interactively navigate through the world.
 *
 * @see <a href="https://courses.engr.illinois.edu/cs126/sp2019/assignments/adventure.pdf</a>
 **/
public class Adventure {
    private static final int STATUS_OK = 200;
    private static final int COMMAND_INDEX = 3;
    private static Layout layout;
    private static Player player;
    private static Room currentRoom;
    private static Directions directionCommand;
    private static Monster monster;
    private static boolean gameEnded = false;
    private static boolean directionIsLocked = false;

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Layout getLayout() {
        return layout;
    }

    public static void main(String[] arguments) throws UnirestException, MalformedURLException {
        player = layout.getPlayer();
        testAlternateUrl();
        System.out.println(beginGame());

        Scanner scanner = new Scanner(System.in);
        while (!gameEnded) {
            String originalInput = scanner.nextLine();
            String userInput = originalInput.toLowerCase();
            if (userEndsGame(originalInput) || reachedFinalRoom(currentRoom)) {
                System.out.println("You have reached your final destination.");
                break;
            }
            String[] userInputArray = userInput.split(" ");
            //String[] possibleDirectionArray = currentRoom.possibleDirection().toLowerCase().split(", ");
            if (userInputArray.length < 2) {
                System.out.println(printInvalidCommand(originalInput, currentRoom));

                //user said "go east", with some valid direction command
                //so go to your new room
            } else if (findDirectionInArray(currentRoom.getDirections(), userInput)) {
                if (directionCommand.getEnabled().equals("true")) {
                    currentRoom = moveToNewRoom(directionCommand.getDirectionName(), currentRoom);
                    System.out.println(roomInformation(player, currentRoom, currentRoom.getMonsterInRoom()));
                }

                //direction is locked, but user has the item
                if (directionCommand.getEnabled().equals("false")) {
                    System.out.println("The player needs an item to unlock this direction.");
                    if (notHaveValidKey(directionCommand.getValidKeyNames(), player.getItems())) {
                        System.out.println("The user does not have a valid key. Pick a different direction.");
                        System.out.println(roomInformation(player, currentRoom, currentRoom.getMonsterInRoom()));
                    } else {
                        currentRoom = moveToNewRoom(directionCommand.getDirectionName(), currentRoom);
                        System.out.println(roomInformation(player, currentRoom, currentRoom.getMonsterInRoom()));
                    }
                }
            } else if (userInputArray[0].equals("go")
                    && !findDirectionInArray(currentRoom.getDirections(), userInput)) {
                System.out.println(printWrongDirection(originalInput, currentRoom));
                //PICKUP ITEM IN A ROOM
            } else if (userInputArray[0].equals("pickup")) {
                String itemCommand = userInputArray[1];
                if (itemExistsInRoom(itemCommand, currentRoom.getItems())) {
                    Item itemObject = currentRoom.getItemObjectFromName(itemCommand, currentRoom.getItems());
                    pickUpItem(itemObject, currentRoom.getItems(), player.getItems());
                }
            } else {
                System.out.println(printInvalidCommand(originalInput, currentRoom));
            }
        }
    }

    /**
     * Method that allows user to specify an alternate URL.
     * If the URL is invalid, default to original URL.
     **/
    public static void testAlternateUrl() throws UnirestException, MalformedURLException {
        System.out.println("Please enter an alternate url.");
        Scanner scanner = new Scanner(System.in);
        String userUrl = scanner.nextLine();

        try {
            makeApiRequest(userUrl);
        } catch (UnirestException e) {
            System.out.println("Network not responding");
        } catch (MalformedURLException e) {
            System.out.println("Bad URL: " + userUrl);
            System.out.println("We will be defaulting to the original url" + "\n");
            makeApiRequest("https://courses.engr.illinois.edu/cs126/adventure/siebel.json");
        }
    }

    /**
     * Method that states the description and possible directions from the starting room.
     *
     * @return String of starting room information.
     **/
    public static String beginGame() {
        String beginGame = "Your journey begins here";
        beginGame = beginGame + "\n" + currentRoom.getDescription();
        beginGame = beginGame + "\n" + "From here, you can go: " + currentRoom.possibleDirection();
        return beginGame;
    }

    /**
     * Method that determines if the user's inputted command contains a valid direction.
     *
     * @param directionsArray, an array of possible directions one can move from the current room.
     * @param userInput,       the direction command the user inputs (e.g. go east, go west).
     * @return boolean.
     **/
    public static boolean findDirectionInArray(Directions[] directionsArray, String userInput) {
        if (userInput == null || directionsArray == null) {
            return false;
        }
        String[] userInputArray = userInput.toLowerCase().split(" ");
        if (!userInputArray[0].equals("go")) {
            return false;
        }//use trim?
        userInput = userInput.substring(COMMAND_INDEX).toLowerCase();
        for (int i = 0; i < directionsArray.length; i++) {
            if (directionsArray[i] != null && directionsArray[i].getDirectionName().toLowerCase().equals(userInput)) {
                directionCommand = directionsArray[i];
                //String currentDirection = directionsArray[i].getDirectionName();
                //String newRoomName = currentRoom.roomFromDirection(currentDirection);
                //currentRoom = layout.roomObjectFromName(newRoomName);
                return true;
            }
        }
        return false;
    }

    public static Room moveToNewRoom(String currentDirectionName, Room currentRoom) {
        String newRoomName = currentRoom.roomFromDirection(currentDirectionName);
        currentRoom = layout.roomObjectFromName(newRoomName);
        return currentRoom;
    }

    /**
     * Method that checks if the user has reached the ending room.
     *
     * @param currentRoom the room the user is currently in.
     * @return boolean.
     **/
    public static boolean reachedFinalRoom(Room currentRoom) {
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
    public static boolean userEndsGame(String userInput) {
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

    public static String roomInformation(Player player, Room currentRoom, Monster monsterInRoom) {
        StringBuilder roomInformation = new StringBuilder();
        if (currentRoom == null) {
            return null;
        }
        if (monsterInRoom == null || monsterInRoom.toString().equals("{}")) {
            return "There are no monsters in the room.\n" + currentRoom.getDescription()
                    + "\nFrom here, you can go: " + currentRoom.possibleDirection();
        }
        roomInformation.append("The monster in this room is: " + monsterInRoom.getName());
        player.fightMonster(monsterInRoom, player);

        roomInformation.append(currentRoom.getDescription() + "\nFrom here, you can go: "
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
    public static String printWrongDirection(String userInput, Room currentRoom) {
        if (userInput == null || currentRoom == null) {
            return null;
        }
        return "I can't go '" + userInput.substring(COMMAND_INDEX) + "'\n"
                + roomInformation(player, currentRoom, currentRoom.getMonsterInRoom());
    }

    /**
     * Method that states when a user enters an invalid command, and command doesn't begin with 'go'.
     *
     * @param currentRoom the room the user is currently in.
     * @param userInput   the direction command the user inputs (e.g. go east, go west).
     * @return String.
     **/
    public static String printInvalidCommand(String userInput, Room currentRoom) {
        if (userInput == null || currentRoom == null) {
            return null;
        }
        return "I don't understand '" + userInput + "'\n"
                + roomInformation(player, currentRoom, currentRoom.getMonsterInRoom());
    }

    public static boolean itemExistsInRoom(String itemToPickUp, ArrayList<Item> availableRoomItems) {
        for (int i = 0; i < availableRoomItems.size(); i++) {
            if (availableRoomItems.get(i).getName().equals(itemToPickUp)) {
                return true;
            }
        }
        return false;
    }

    //use "item" with "direction"
    public static boolean playerHasItem(String itemName, ArrayList<Item> playerItems) {
        for (int i = 0; i < playerItems.size(); i++) {
            if (playerItems.get(i).getName().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Item> pickUpItem(Item itemToPickUp, ArrayList<Item> availableRoomItems,
                                             ArrayList<Item> playerItems) {
        //if user says "pickup item", pick up that item
        for (int i = 0; i < availableRoomItems.size(); i++) {
            if (availableRoomItems.get(i).getName().equals(itemToPickUp.getName())) {
                playerItems.add(itemToPickUp);
                return playerItems;
            }
        }
        return playerItems;
    }

    public static boolean notHaveValidKey(ArrayList<String> validKeyNames, ArrayList<Item> playerItems) {
        //disjoint returns true if 2 array lists have no elements in common
        //returns false if the 2 array lists have an element in common
        return Collections.disjoint(validKeyNames, playerItems);
    }


    /*public static ArrayList<String> listToString(ArrayList<Item> availableRoomItems) {
        //Cited from: https://stackoverflow.com/questions/21201415/how-can-i-convert-arraylistobject-to-arrayliststring-or-arraylisttimestamp
        ArrayList<String> stringsList = new ArrayList<>();
        for (Item item : availableRoomItems) {
            stringsList.add(item != null ? item.toString() : null);
        }
        return stringsList;
    }*/

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
            currentRoom = layout.roomObjectFromName(layout.getStartingRoom());
        }
    }
}

