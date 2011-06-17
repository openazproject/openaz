package test.objects;

import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PreDecisionHandler;

public class TestPreHandler implements PreDecisionHandler{

    public void preDecide(PepRequest request) {
        System.out.println("==================================");
        System.out.println("In Pre Decide....");
        System.out.println("======================================");
    }
}
