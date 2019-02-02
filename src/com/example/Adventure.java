package com.example;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Scanner;

public class Adventure {
    private static final int STATUS_OK = 200;
    //private static final String url = "https://courses.engr.illinois.edu/cs126/adventure/siebel.json";
    private static Layout layout;
    private static Room currentRoom;
    private static boolean userQuit = false;

    public static void main(String[] arguments) {
        String url = "https://courses.engr.illinois.edu/cs126/adventure/siebel.json";

        // Make an HTTP request to the above URL
        try {
            makeApiRequest(url);
        } catch (UnirestException e) {
//            e.printStackTrace();
            System.out.println("Network not responding");
        } catch (MalformedURLException e) {
            System.out.println("Bad URL: " + url);
        }

        System.out.println("Your journey begins here");
        System.out.println(layout.getRooms().get(0).getDescription());
        System.out.println(layout.getRooms().get(0).possibleDirection());

        while (!userQuit) {
            Scanner scanner = new Scanner(System.in);
            String userInput = scanner.nextLine().toLowerCase();

            if (userInput.equals("quit") || userInput.equals("exit")) {
                userQuit = true;
                //System.exit(0);
            }

            String[] inputArray = userInput.toLowerCase().split(" ");
            String[] possibleDirectionArray = currentRoom.possibleDirection().split(", ");

            if (inputArray[0].equals("go") && inputArray.length == 2) {
                for (int i = 0; i < possibleDirectionArray.length; i++) {

                    if (possibleDirectionArray[i].equals(inputArray[1])) {
                       String currentDirection = possibleDirectionArray[i];
                       //currentRoom.roomFromDirection(currentDirection);
                    }
                }
            }
        }
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
        }
    }
}

