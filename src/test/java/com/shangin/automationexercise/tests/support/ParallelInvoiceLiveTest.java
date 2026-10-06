package com.shangin.automationexercise.tests.support;

import java.io.IOException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.shangin.automationexercise.tests.ui.order.PlaceOrderTest;

/** Select only repeatedInvoice; inherited tests are ordinary order tests. */
public class ParallelInvoiceLiveTest extends PlaceOrderTest {
    @DataProvider(parallel = true)
    public Object[][] invoices() {
        int count = Integer.getInteger("invoice.invocations", 4);
        if (count < 1) { throw new IllegalArgumentException("invoice.invocations must be positive"); }
        Object[][] invocations = new Object[count][1];
        for (int i = 0; i < count; i++) { invocations[i][0] = i; }
        return invocations;
    }

    @Test(dataProvider = "invoices")
    public void repeatedInvoice(int invocation) throws IOException {
        shouldDownloadInvoiceAfterPurchase();
    }
}
