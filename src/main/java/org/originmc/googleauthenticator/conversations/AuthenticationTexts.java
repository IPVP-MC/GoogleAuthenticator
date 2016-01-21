package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Holds all of our authentication prompts
 */
public final class AuthenticationTexts {

    /**
     * The very first message that tells the player about texts in the brackets being clickable
     */
    public static final BaseComponent[] PRE_START_TEXT;

    /**
     * The introduction message
     */
    public static final BaseComponent[] INTRO_MESSAGE;

    /**
     * A message that gives information about how to exit the authentication process
     */
    public static final BaseComponent[] QUIT_INFORMATION;

    /**
     * A message that tells the player to install the Google Authenticator app
     */
    public static final BaseComponent[] INSTALL_APP_TEXT;

    /**
     * A message that tells the player to scan the QR code in their app
     */
    public static final BaseComponent[] OPEN_APP_TEXT;

    static {
        TextComponent emptyLine = new TextComponent("\n");

        // Create our grey brackets
        TextComponent leftBracket = new TextComponent("[");
        TextComponent rightBracket = new TextComponent("]");
        TextComponent bracketSpacer = new TextComponent("|");
        leftBracket.setColor(ChatColor.GRAY);
        rightBracket.setColor(ChatColor.GRAY);
        bracketSpacer.setColor(ChatColor.GRAY);

        // Create the continue button
        TextComponent continueButton = new TextComponent("Continue ");
        ClickEvent continueAction = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "continue");
        continueButton.setClickEvent(continueAction);
        continueButton.setColor(ChatColor.GREEN);

        // Create the exit button
        TextComponent exitButton = new TextComponent(" Exit");
        ClickEvent exitAction = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "exit");
        exitButton.setClickEvent(exitAction);
        exitButton.setColor(ChatColor.RED);

        // Create the [Next | Exit] buttons
        TextComponent nextOrExit = new TextComponent(leftBracket);
        nextOrExit.addExtra(continueButton);
        nextOrExit.addExtra(bracketSpacer);
        nextOrExit.addExtra(exitButton);
        nextOrExit.addExtra(rightBracket);

        // Create BaseComponent[] PRE_START_TEXT
        TextComponent preStartText = new TextComponent("Before we start, please be aware that anything in ");
        TextComponent space = new TextComponent(" ");
        TextComponent preStartSecondPart = new TextComponent(" is clickable! ");
        preStartText.setColor(ChatColor.GREEN);
        preStartSecondPart.setColor(ChatColor.GREEN);
        PRE_START_TEXT = new BaseComponent[]{ emptyLine, preStartText, leftBracket, space, rightBracket,
                preStartSecondPart, nextOrExit, emptyLine };

        // Create BaseComponent[] INTRO_MESSAGE
        TextComponent introMessage = new TextComponent("Let's set up Two-factor authentication for your Minecraft " +
                "account on Origin. ");
        introMessage.setColor(ChatColor.GREEN);
        INTRO_MESSAGE = new BaseComponent[]{ emptyLine, introMessage, nextOrExit, emptyLine };

        // Create BaseComponent[] QUIT_INFORMATION
        TextComponent quitInformation = new TextComponent("You can quit this process by typing \"exit\" or you can click" +
                " the Exit button at any time. ");
        quitInformation.setColor(ChatColor.GOLD);
        QUIT_INFORMATION = new BaseComponent[]{ emptyLine, quitInformation, nextOrExit, emptyLine };

        // Create BaseComponent[] INSTALL_APP_TEXT
        TextComponent installAppMain = new TextComponent("Firstly, you need to install the ");
        TextComponent installAppGoogleAuthenticator = new TextComponent("\"Google Authenticator\"");
        TextComponent installAppSecond = new TextComponent(" app for your mobile device. ");
        installAppMain.setColor(ChatColor.GREEN);
        installAppGoogleAuthenticator.setColor(ChatColor.AQUA);
        installAppSecond.setColor(ChatColor.GREEN);
        INSTALL_APP_TEXT = new BaseComponent[]{ emptyLine, installAppMain, installAppGoogleAuthenticator,
                installAppSecond, nextOrExit, emptyLine };

        // Create BaseComponent[] OPEN_APP_TEXT
        TextComponent openApp = new TextComponent("Open your Authenticator app and scan this QR code! (Press the + button) ");
        openApp.setColor(ChatColor.GREEN);
        OPEN_APP_TEXT = new BaseComponent[]{ emptyLine, openApp, nextOrExit, emptyLine };
        // TODO: Must give the map at this point
    }

    private AuthenticationTexts() {

    }
}