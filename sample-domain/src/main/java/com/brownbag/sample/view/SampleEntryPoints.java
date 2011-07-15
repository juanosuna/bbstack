package com.brownbag.sample.view;

import com.brownbag.core.view.MainEntryPoints;
import com.brownbag.core.view.entity.EntryPoint;
import com.brownbag.sample.view.account.AccountEntryPoint;
import com.brownbag.sample.view.contact.ContactEntryPoint;
import com.brownbag.sample.view.opportunity.OpportunityEntryPoint;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Juan
 * Date: 5/22/11
 * Time: 11:06 PM
 */
@Component
@Scope("session")
public class SampleEntryPoints extends MainEntryPoints {

    @Resource
    private ContactEntryPoint contactEntryPoint;

    @Resource
    private AccountEntryPoint accountEntryPoint;

    @Resource
    private OpportunityEntryPoint opportunityEntryPoint;

    @Override
    public List<EntryPoint> getEntryPoints() {
        List<EntryPoint> entryPoints = new ArrayList<EntryPoint>();
        entryPoints.add(contactEntryPoint);
        entryPoints.add(accountEntryPoint);
        entryPoints.add(opportunityEntryPoint);

        return entryPoints;
    }
}
