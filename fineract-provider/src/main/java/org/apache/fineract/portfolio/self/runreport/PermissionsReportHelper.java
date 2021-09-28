/*

    Created by Sinatra Gunda
    At 6:57 AM on 9/25/2021

*/
package org.apache.fineract.portfolio.self.runreport;

import org.apache.fineract.infrastructure.dataqueries.data.ReportData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.mix.data.ContextData;
import org.apache.fineract.useradministration.data.PermissionData;
import org.apache.fineract.useradministration.data.RoleData;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.service.PermissionReadPlatformService;
import org.apache.fineract.useradministration.service.RoleReadPlatformService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PermissionsReportHelper {

    public static Collection<ReportData> selfServiceReports(PermissionReadPlatformService permissionReadPlatformService , RoleReadPlatformService roleReadPlatformService , PlatformSecurityContext platformSecurityContext){

        //Collection<>
        Collection<PermissionData> permissionDataCollection = new ArrayList<>();

        AppUser appUser = platformSecurityContext.authenticatedUser();

        Long userId = appUser.getId();

        Collection<RoleData> roleDataCollection = roleReadPlatformService.retrieveAppUserRoles(userId);

        // for each role find reports that the user has permission by linking with role permission table

        System.err.println("--------this client have these roles ---------------------"+roleDataCollection.size());

        Predicate<PermissionData> isReport = (e)->{

            return e.getGrouping().equalsIgnoreCase("report");
        };

        roleDataCollection.stream().forEach((e)->{

            System.err.println("-----------------------client role is --------------------"+e.getName());
            Long roleId = e.getId();

            System.err.println("-----------------------role id is-----------------------"+roleId);

            Collection<PermissionData> permissionDataCollectionTemp =  permissionReadPlatformService.retrieveAllRolePermissions(roleId);

            System.err.println("--------------non filtered list size is ------------------------"+permissionDataCollection.size());

            Collection<PermissionData> filteredList =  permissionDataCollectionTemp.stream().filter(isReport).collect(Collectors.toList());

            System.err.println("------------------filtered list is now -----------------"+filteredList.size());

            permissionDataCollection.addAll(filteredList);

        });


        System.err.println("------------------all reports here are ----------------"+permissionDataCollection.size());

        Collection<ReportData> reportDataCollection = new ArrayList<>();

        permissionDataCollection.stream().forEach((e)->{

            System.err.println("--------------report name ---------------"+e.getEntityName());
            ReportData reportData = ReportData.fromReportName(e.getEntityName());
            reportDataCollection.add(reportData);
        });

        return reportDataCollection;

    }
}
