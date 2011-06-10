package org.openliberty.openaz.azapi;

public interface AzRequestContextFactory {
    
    /**
     * Returns a new AzRequestContext object that the AzAPI caller may 
     * use to populate with all the necessary information required (in
     * the form of AzAttributes) for one or more XACML Authorization 
     * requests.
     * 
     * @return  an unpopulated azRequestContext object
     */
    public AzRequestContext createAzRequestContext();
    
}
