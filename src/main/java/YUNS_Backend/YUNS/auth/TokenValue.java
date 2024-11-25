package YUNS_Backend.YUNS.auth;

public final class TokenValue {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final Long ACCESS_TTL = 1000 * 60 * 30L;
    public static final String ACCESS_HEADER = "Authorization-Access";
}
