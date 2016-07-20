package org.ipvp.auth.conversations;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
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

    /**
     * A message that tells the player to enter their authentication code
     */
    public static final BaseComponent[] ENTER_CODE_TEXT;

    /**
     * A message that tells the player that they have successfully authenticated
     */
    public static final BaseComponent[] AUTHENTICATED_TEXT;

    /**
     * A message that tells the player they need to authenticate when they login
     */
    public static final BaseComponent[] LOGIN_REQUIRES_AUTH;

    /**
     * A message that tells the player to enter their authentication code when they login
     */
    public static final BaseComponent[] LOGIN_REQUIRES_AUTH_ASK;

    /**
     * A messages that tells the player they have been authenticated
     */
    public static final BaseComponent[] NOW_AUTHENTICATED;

    /**
     * A message that tells the player that they have cancelled the authentication
     */
    public static final BaseComponent[] CANCEL_SETUP;

    /**
     * A message that tells the player they still require authentication
     */
    public static final BaseComponent[] NEED_TO_AUTHENTICATE;

    /**
     * A message that tells the player that the plugin is currently waiting to check if they have AuthenticationData
     */
    public static final BaseComponent[] WAITING_FOR_DATA;

    /**
     * A message that tells the player they have disabled authentication
     */
    public static final BaseComponent[] DISABLED_AUTHENTICATION;

    /**
     * A message that tells the player we are now remembering their IP
     */
    public static final BaseComponent[] NOW_REMEMBERING_IP;

    /**
     * A message that tells the player we are no longer remembering their IP
     */
    public static final BaseComponent[] NO_LONGER_REMEMBERING_IP;

    // Ugly instantiation of basecomponents
    static {
        TextComponent emptyLine = new TextComponent("\n");
        TextComponent emptySpace = new TextComponent(" ");

        // Create our grey brackets
        TextComponent leftBracket = new TextComponent("[");
        TextComponent rightBracket = new TextComponent("]");
        TextComponent bracketSpacer = new TextComponent("|");
        leftBracket.setColor(ChatColor.GRAY);
        rightBracket.setColor(ChatColor.GRAY);
        bracketSpacer.setColor(ChatColor.GRAY);

        // Create the continue button
        TextComponent continueButton = new TextComponent("Continue ");
        HoverEvent clickToContinue = new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                TextComponent.fromLegacyText(ChatColor.GREEN + "Click to continue"));
        continueButton.setHoverEvent(clickToContinue);
        ClickEvent continueAction = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "continue");
        continueButton.setClickEvent(continueAction);
        continueButton.setColor(ChatColor.GREEN);

        // Create the exit button
        TextComponent exitButton = new TextComponent("Exit");
        HoverEvent clickToExit = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                TextComponent.fromLegacyText(ChatColor.RED + "Click to exit"));
        exitButton.setHoverEvent(clickToExit);
        ClickEvent exitAction = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "exit");
        exitButton.setClickEvent(exitAction);
        exitButton.setColor(ChatColor.RED);

        // Create the [Next | Exit] buttons
        TextComponent nextOrExit = new TextComponent(leftBracket);
        nextOrExit.addExtra(continueButton);
        nextOrExit.addExtra(bracketSpacer);
        nextOrExit.addExtra(emptySpace);
        nextOrExit.addExtra(exitButton);
        nextOrExit.addExtra(rightBracket);

        // Create BaseComponent[] PRE_START_TEXT
        TextComponent preStartText = new TextComponent("Before we start, please be aware that anything in ");
        TextComponent space = new TextComponent(" ");
        TextComponent preStartSecondPart = new TextComponent(" is clickable! ");
        preStartText.setColor(ChatColor.GREEN);
        preStartSecondPart.setColor(ChatColor.GREEN);
        PRE_START_TEXT = new BaseComponent[]{ emptyLine, preStartText, leftBracket, space, rightBracket,
                preStartSecondPart, nextOrExit };

        // Create BaseComponent[] INTRO_MESSAGE
        TextComponent introMessage = new TextComponent("Let's set up Two-factor authentication for your Minecraft " +
                "account on iPvP. ");
        introMessage.setColor(ChatColor.GREEN);
        INTRO_MESSAGE = new BaseComponent[]{ introMessage, nextOrExit };

        // Create BaseComponent[] QUIT_INFORMATION
        TextComponent quitInformation = new TextComponent("You can quit this process by typing \"exit\" or you can click" +
                " the Exit button at any time. ");
        quitInformation.setColor(ChatColor.GOLD);
        QUIT_INFORMATION = new BaseComponent[]{ quitInformation, nextOrExit };

        // Create BaseComponent[] INSTALL_APP_TEXT
        TextComponent installAppMain = new TextComponent("Firstly, you need to install the ");
        TextComponent installAppGoogleAuthenticator = new TextComponent("\"Google Authenticator\"");
        TextComponent installAppSecond = new TextComponent(" app for your mobile device. ");
        installAppMain.setColor(ChatColor.GREEN);
        installAppGoogleAuthenticator.setColor(ChatColor.AQUA);
        installAppSecond.setColor(ChatColor.GREEN);
        INSTALL_APP_TEXT = new BaseComponent[]{ installAppMain, installAppGoogleAuthenticator,
                installAppSecond, nextOrExit };

        // Create BaseComponent[] OPEN_APP_TEXT
        TextComponent openApp = new TextComponent("Open your Authenticator app and scan this QR code! (Press the + button) ");
        openApp.setColor(ChatColor.GREEN);
        OPEN_APP_TEXT = new BaseComponent[]{ openApp, nextOrExit };

        // Create BaseComponent[] ENTER_CODE_TEXT
        TextComponent enterCode = new TextComponent("Please type in the code that your app is giving you. ");
        enterCode.setColor(ChatColor.GREEN);
        ENTER_CODE_TEXT = new BaseComponent[]{ enterCode, leftBracket, exitButton, rightBracket };

        // Create BaseComponent[] AUTHENTICATED_TEXT
        TextComponent auth = new TextComponent("Thank you for enabling iPvP's two-factor authentication!\n" +
                "From now on you will need to enter your code every time you log in (unless you select 'remember me') " +
                "in order to verify that it is you.\n");
        TextComponent remember = new TextComponent("Do you want us to remember you by your IP address so you won't have to enter the code unless your IP changes? ");
        remember.setColor(ChatColor.GOLD);
        TextComponent yesButton = new TextComponent("Yes");
        auth.setColor(ChatColor.GREEN);
        yesButton.setColor(ChatColor.GREEN);
        yesButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/auth ipenable"));
        AUTHENTICATED_TEXT = new BaseComponent[]{ auth, remember, leftBracket, yesButton, rightBracket };

        // Create BaseComponent[] LOGIN_REQUIRES_AUTH
        TextComponent loginAuth = new TextComponent("You have two-factor authentication enabled on this account!\n");
        TextComponent loginAuth2 = new TextComponent("Please enter your 6 digit code from the Google Authenticator app to" +
                " be able to move servers. If you enabled remember me, your IP is not the same anymore.");
        loginAuth.setColor(ChatColor.GREEN);
        loginAuth2.setColor(ChatColor.YELLOW);
        LOGIN_REQUIRES_AUTH = new BaseComponent[]{ loginAuth, loginAuth2 };

        // Create BaseComponent[] LOGIN_REQUIRES_AUTH_ASK
        TextComponent loginAuthAsk = new TextComponent("Please type in the code that your app is giving you.");
        loginAuthAsk.setColor(ChatColor.GREEN);
        LOGIN_REQUIRES_AUTH_ASK = new BaseComponent[]{ loginAuthAsk };

        // Create NOW_AUTHENTICATED
        TextComponent authenticated = new TextComponent("You are now authenticated.");
        authenticated.setColor(ChatColor.GREEN);
        NOW_AUTHENTICATED = new BaseComponent[]{ authenticated };

        // Create CANCEL_SETUP
        TextComponent cancelSetup = new TextComponent("Cancelled two-factor setup process! Try again soon.");
        cancelSetup.setColor(ChatColor.RED);
        CANCEL_SETUP = new BaseComponent[]{ cancelSetup };

        // Create NEED_TO_AUTHENTICATE
        TextComponent needToAuth = new TextComponent("You need to authenticate yourself first!");
        needToAuth.setColor(ChatColor.RED);
        NEED_TO_AUTHENTICATE = new BaseComponent[]{ needToAuth };

        // Create WAITING_FOR_DATA
        TextComponent waitingForData = new TextComponent("Please wait while your /auth data is checked...");
        waitingForData.setColor(ChatColor.RED);
        WAITING_FOR_DATA = new BaseComponent[]{ waitingForData };

        // Create DISABLED_AUTHENTICATION
        TextComponent disabledAuth = new TextComponent("Disabled two-factor authentication! You can still enable it " +
                "again by running \"/auth\"");
        disabledAuth.setColor(ChatColor.GREEN);
        DISABLED_AUTHENTICATION = new BaseComponent[]{ disabledAuth };

        // Create NOW_REMEMBERING_IP
        TextComponent rememberingIp = new TextComponent("We will now remember you by your " +
                "IP and will no longer ask for a code every time you log in!");
        rememberingIp.setColor(ChatColor.GREEN);
        NOW_REMEMBERING_IP = new BaseComponent[]{ rememberingIp };

        // Create NO_LONGER_REMEMBERING_IP
        TextComponent noLongerRememberingIp = new TextComponent("We will no longer remember you by your " +
                "IP and will now require a code every time you log in!");
        noLongerRememberingIp.setColor(ChatColor.GREEN);
        NO_LONGER_REMEMBERING_IP = new BaseComponent[]{ noLongerRememberingIp };
    }

    private AuthenticationTexts() {

    }
}
