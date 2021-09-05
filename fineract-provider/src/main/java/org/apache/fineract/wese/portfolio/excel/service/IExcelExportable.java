/*

    Created by Sinatra Gunda
    At 12:50 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.excel.service;

public interface IExcelExportable {

    public Object[] excelHeader();
    public Object[] excelTemplate();

}
