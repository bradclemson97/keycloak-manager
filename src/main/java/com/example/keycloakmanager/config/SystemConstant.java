package com.example.keycloakmanager.config;


/**
 * A collection of system constants.
 */
public class SystemConstant {

    private SystemConstant() {

    }

    // Endpoints
    public static final int API_VERSION = 1;
    public static final String API_USER = "user";
    public static final String API_ROLLBACK = "rollback";

    // Password Formats
    public static final String PASS_WORD_FORMAT = "(?:^|-)([^-]+)";
    public static final int USER_PASS_MIN_LENGTH = 17;
    public static final int USER_PASS_MIN_WORDS = 3;
    public static final int USER_PASS_WORD_MIN_LENGTH = 4;
    public static final int USER_PASS_MIN_SEQUENCE_SIZE = 3;
    public static final Integer USER_PASS_MIN_SEQUENCE_FACTOR = 1;
    public static final String USER_PASS_FORBIDDEN_CHARACTERS = "\"'<>\\\\/|'";

    // Password Generation
    public static final int PASS_GENERATION_MAX_ATTEMPTS = 5;
    public static final int PASS_GENERATION_LENGTH = 25;
    public static final int PASS_GENERATION_WORDS = 4;

    // User Groups
    public static final String SYSTEM_USERS_GROUP = "system-users";
}
