package allenw3u.swimmer.data;

/**
 * Created by Allenw3u on 2017/5/25.
 */

public final class dataContact {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private dataContact(){};
    /**
     * Possible values for the swimstyle
     */
    public static final String STYLE_UNKNOWN = "Nostyle";
    public static final String STYLE_FREESTYLE = "Freestyle";
    public static final String STYLE_BREASTSTROKE = "Breaststroke";
    public static final String STYLE_BUTTERFLY = "Butterfly";
    public static final String STYLE_BACKSTROKE ="Backstroke";

    /**
     * Possible values for the lap distance
     */
    public static final String LAP_UNKNOW = "Nolap";
    public static final String LAP_25M = "25m";
    public static final String LAP_50M = "50m";

    public static final String NO_NAME = "Noname";
}
