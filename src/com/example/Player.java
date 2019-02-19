package com.example;

import java.util.ArrayList;
import java.util.Scanner;

public class Player {
    private static Player player;
    private static Adventure adventure = new Adventure();
    private static Layout layout = new Layout();
    private ArrayList<Item> items;
    private int health;
    private int attack;
    private static final int PLAYER_HEALTH_BOOST = 500;

    public Layout getLayout() {
        return layout;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    //the player keeps knocking 200 points off monster, while player health is randomly subtracted
    public void fightMonster(Monster monster, Player player, Room currentRoom) {
        int playerHealth = player.getHealth();
        int monsterHealth = monster.getHealth();
        System.out.println("Before proceeding, you must battle: " + monster.getName());
        System.out.println("Monster has this much health: " + monster.getHealth());
        System.out.println("Player has this much health: " + player.getHealth());

        playerHasSpecialItem(monster);
        while (monsterHealth > 0 && playerHealth > 0) {
            System.out.println("Enter 'attack' to attack the monster");
            Scanner scanner = new Scanner(System.in);
            String userAttack = scanner.nextLine().toLowerCase();
            if (userAttack.equals("attack")) {
                monsterHealth = monsterHealth - player.getAttack();
                System.out.println("You have attacked the monster. Monster now has: " + monsterHealth + " HP");
                if (monsterHealth <= 0) {
                    System.out.println("You have successfully defeated the monster! Go to your room.\n");
                    break;
                }
            } else {
                System.out.println("I don't understand this command. Monster is now attacking...");
            }
            playerHealth = playerHealth - monster.getAttack();
            System.out.println("The monster has attacked. Player now has: " + playerHealth + " HP");
            if (playerHealth <= 0) {
                System.out.println("The monster has killed you! Try again.");
                playerHealth = playerHealth + PLAYER_HEALTH_BOOST;
            }
        }
    }

    public void playerHasSpecialItem(Monster monster) {
        if (monster.getName().equals("Mystery Serial Killer") && adventure.playerHasItem("noose", items)) {
            System.out.println("\nThe Chicago serial killer H.H. Holmes was executed by hanging. You may " +
                    "have a special item " + "that will aid you in battling this monster. \nTo use this item," +
                    " successfully answer the following questions.");
            if (playerPlaysTrivia()) {
                System.out.println("You have correctly answered all the questions! You may now use the special item.");
                Scanner scanner = new Scanner(System.in);
                String userInput = scanner.nextLine();
                if (userInput.equalsIgnoreCase("kill monster with noose")) {
                    System.out.println("You have defeated the serial killer and escaped the murder castle!");
                    System.exit(0);
                } else {
                    System.out.println("Incorrect command. Now battling monster...");
                }
            } else {
                System.out.println("You did not answer all the questions correctly. Now battling monster...");
            }
        }
    }

    public boolean playerPlaysTrivia() {
        String[] questionsArray = {"" +
                "\nTrue or False: The Chicago World's Fair of 1893 celebrated the 400th" +
                " anniversary " + "of the discovery of the New World by Columbus.",
                "Which architect designed the Woman's Building?",
                "Who assassinated the Chicago Mayor, Carter Harrison, on the last day of the fair?",
                "During the World's Fair of 1893 a serial killer was at work, using the draw of the Fair to " +
                        "ensnare his victims. What was his name?",
                "Dr. Holmes was convicted and hanged after murdering his close partner in 1895. Who was the partner?"
        };
        String[] answersArray = {"True", "Sophia Hayden", "Patrick Prendergast", "Holmes", "Benjamin Pitezel"};

        for (int i = 0; i < questionsArray.length; i++) {
            Scanner scanner = new Scanner(System.in);
            System.out.println(questionsArray[i]);
            String userAnswer = scanner.nextLine();
            if (!userAnswer.equalsIgnoreCase(answersArray[i])) {
                return false;
            }
        }
        return true;
    }
}
