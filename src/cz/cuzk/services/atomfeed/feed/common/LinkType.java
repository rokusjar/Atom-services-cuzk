package cz.cuzk.services.atomfeed.feed.common;

public enum LinkType {
    /**
     * Obyčejný link bez atributů.
     */
    SIMPLE,
    /**
     * Odkaz na metadata k hlavnímu feedu.
     */
    MAIN_FEED_METADATA,
    /**
     * Odkaz na metadata k dataset feedu.
     */
    DATASET_METADATA,
    /**
     * Odkaz feedu na sebe sama.
     */
    SELF,
    /**
     * Odkaz na OpenSearch dokument.
     */
    OPENSEARCH,
    /**
     * Odkaz na dataset feed.
     */
    DATASET_FEED,
    /**
     * Odkaz na INSPIRE registr
     */
    INSPIRE_REGISTRY,
    /**
     * Odkaz na datový soubor
     */
    DATA,
    /**
     * Zpětný odkaz na nadřazený feed
     */
    PARENT
}
