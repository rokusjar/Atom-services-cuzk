package cz.cuzk.services.atomfeed.feed.common;

/**
 * Popisuje v jakém stavu se element nachází. Je Určena pro popis feed a entry elementů.
 * Viz třídy {@link FeedElement#state} a {@link EntryElement#state}
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-17
 */
public enum State{
    /**
     * Element neobsahuje všechny povinné informace.
     */
    NOT_VALID,
    /**
     * Element obsahuje všechny povinné informace.
     */
    VALID,
    /**
     * Element obsahuje všechny povinné a doporučené informace.
     */
    RECOMMENDED,
    /**
     * Element obsahuje všechny možné informace.
     */
    FULL;
}